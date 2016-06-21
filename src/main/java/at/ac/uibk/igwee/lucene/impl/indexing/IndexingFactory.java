package at.ac.uibk.igwee.lucene.impl.indexing;

import at.ac.uibk.igwee.lucene.api.Constants;
import at.ac.uibk.igwee.lucene.api.indexing.*;
import at.ac.uibk.igwee.lucene.impl.ImplConstants;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.icu.segmentation.ICUTokenizer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.util.CharFilterFactory;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.document.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class IndexingFactory {

	/**
	 * Default Tokenizer Factory name: standard
	 */
	private static final String DEFAULT_TOKENIZER_FACTORYNAME = "standard";

	/**
	 * Default Tokenfilter Factory name: standard
	 */
	private static final String DEFAULT_TOKENFILTER_FACTORYNAME = "standard";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexingFactory.class);
	

	/**
	 * Creates the FieldType corresponding to the setting.
	 * 
	 * @return the FieldType
	 */
	public static FieldType createFieldType(FieldSetting setting) {
        FieldType result = new FieldType();
		result.setIndexOptions(ImplConstants.getIndexOptions(setting
				.getIndexOption()));
		result.setNumericPrecisionStep(setting.getPrecisionStep());
		result.setNumericType(ImplConstants.getNumericType(setting
				.getNumericType()));
		result.setStored(setting.isStored());
		result.setStoreTermVectorOffsets(setting.isStoreTermVectorOffsets());
		result.setStoreTermVectorPositions(setting.isStoreTermVectorPositions());
		result.setStoreTermVectors(setting.isStoreTermVectors());
		result.setTokenized(setting.isTokenized());
		return result;
	}

	/**
	 * Create A Reader, to be used as CharFilter.
	 * 
	 * @param fs
	 * @param reader
	 * @return the reader if no name passed through or a new reader using the
	 *         name and param
	 * @throws LuceneIndexException
	 */
	public static Reader createCharFilter(FactorySetting fs, Reader reader)
			throws LuceneIndexException {
		String factoryName = fs.getFactoryName();
		Map<String, String> param = fs.getParameters();
		if (factoryName == null || factoryName.isEmpty())
			return reader;
		try {
			if (param == null)
				param = new HashMap<>();
			param.put(ImplConstants.LUCENE_MATCH_VERSION_KEY,
					ImplConstants.LUCENE_MATCH_VERSION_VALUE);
			CharFilterFactory cf = CharFilterFactory
					.forName(factoryName, param);
			return cf.create(reader);
		} catch (Exception e) {
			throw new LuceneIndexException("Cannot create CharFilter '"
					+ factoryName + "' with parameters (" + param + ").", e);
		}
	}

	/**
	 * Creates a token stream. Use the factory settings.
	 * 
	 * @param ts
	 *            Starting TokenStream
	 * @return a new token stream or null if anything goes wrong.
	 */
	public static TokenStream createTokenFilter(FactorySetting fs,
			TokenStream ts) throws LuceneIndexException {
		String factoryName = fs.getFactoryName();
		Map<String, String> parameters = fs.getParameters();
		try {
			if (fs.getFactoryName() == null || fs.getFactoryName().isEmpty())
				factoryName = DEFAULT_TOKENFILTER_FACTORYNAME;
//			System.out.println("Create tokenfilter with name: " + factoryName);
			parameters.put(ImplConstants.LUCENE_MATCH_VERSION_KEY,
					ImplConstants.LUCENE_MATCH_VERSION_VALUE);
			TokenFilterFactory tf = TokenFilterFactory.forName(factoryName,
					parameters);
			return tf.create(ts);
		} catch (Exception e) {
			throw new LuceneIndexException("Cannot create TokenFilter '"
					+ factoryName + "' with parameters (" + parameters + ").",
					e);
		}
	}

	/**
	 * Creates a Tokenizer.
	 * <p>
	 * Cave: Since ICU-Tokenizer Factory does need a rule file, rigth now I will
	 * just skip it.
	 *
	 * @return a tokenizer.
	 * @throws LuceneIndexException
	 *             if anything goes wrong.
	 */
	public static Tokenizer createTokenizer(FactorySetting fs)
			throws LuceneIndexException {

		if ("icu".equals(fs.getFactoryName())) {
//			return new ICUTokenizer(input);
			return new ICUTokenizer();
		}

		String factoryName = fs.getFactoryName();
		Map<String, String> params = fs.getParameters();

		try {
			if (factoryName == null || factoryName.isEmpty())
				factoryName = DEFAULT_TOKENIZER_FACTORYNAME;
			params.put(ImplConstants.LUCENE_MATCH_VERSION_KEY,
					ImplConstants.LUCENE_MATCH_VERSION_VALUE);
			TokenizerFactory tf = TokenizerFactory.forName(factoryName, params);
//			return tf.create(input);
			return tf.create();
		} catch (Exception e) {
			throw new LuceneIndexException("Cannot create Tokenizer '"
					+ factoryName + "' with parameters(" + params + ").", e);
		}
	}

	
	
	
	/**
	 * Creates a new Analyzer.
	 * 
	 * @return the analyzer with the settings.
	 */
	public static Analyzer createAnalyzer(final AnalyzerSetting as) {

		
		
		return new Analyzer() {
			
			@Override
			protected TokenStreamComponents createComponents(String filedname) {
				
				Tokenizer to = null;
				try {
					to = createTokenizer(as.getTokenizerFactorySetting());
					
				} catch (LuceneIndexException e) {
					LOGGER.error("Cannot create Tokenizer.", e);
				}
				
//				if (to==null)
//					System.out.println("Tokenizer is null.");
				
				TokenStream ts = to;
				try {
					if (as.getTokenFilterFactorySettings()!=null && !as.getTokenFilterFactorySettings().isEmpty() ) {
						for (FactorySetting fs : as.getTokenFilterFactorySettings()) {
							TokenStream test = createTokenFilter(fs, ts);
							if (test != null)
								ts = test;
							}
					} 
				} catch (LuceneIndexException e) {
					
					LOGGER.error("Cannot create Tokenfilters.", e);
				}
				
//				if (ts==null)
//					System.out.println("Tokenfilter is null.");

				return new TokenStreamComponents(to, ts);
			}

			@Override
			protected Reader initReader(String fieldname, Reader reader) {
				if (as.getCharFilterSettings() == null
						|| as.getCharFilterSettings().isEmpty())
					return reader;
				Reader result = reader;
				try {
					for (FactorySetting fs : as.getCharFilterSettings()) {
						result = createCharFilter(fs, result);
					}
				} catch (Exception e) {
					LOGGER.error("Cannot create CharFilters.", e);
				}
				return result;
			}

		};
	}

	/**
	 * Creates a PerFieldAnalyzerWrapper, with the standard analyzer using the default analyzer.
	 * @param is IndexingSetting
	 * @return A PerFieldAnalyzerWrapper
	 * @throws LuceneIndexException
	 */
	public static PerFieldAnalyzerWrapper createPerFieldAnalyzer(IndexSetting is) throws LuceneIndexException {
		return createPerFieldAnalyzer(is, null);
	}
	
	/**
	 * Creates a PerFieldAnalyzerWrapper, usable for the whole index. Including
	 * the default analyzer for DocumentID.
	 * 
	 * @return a PerFieldAnalyzerWrapper, using the defaultFieldname field as default analyzer. If the default Fieldname 
	 * cannot be found or if it is null, then the default analyzer setting will be used.
	 */
	public static PerFieldAnalyzerWrapper createPerFieldAnalyzer(IndexSetting is, String defaultFieldname)
			throws LuceneIndexException {
		
		FieldSetting fs = is.getFieldSetting(defaultFieldname);
		
		
		AnalyzerSetting defaultSetting = fs==null ? 
				is.getDefaultAnalyzerSetting() :
					is.getAnalyzerSetting(fs.getAnalyzername());
		
		Analyzer defaultAnalyzer = createAnalyzer(defaultSetting);
		Map<String, Analyzer> mapping = getFieldnameAnalyzerMapping(is,
				defaultAnalyzer);
		mapping.put(Constants.DOC_ID_DEFAULT_FIELDNAME, new KeywordAnalyzer());

		return new PerFieldAnalyzerWrapper(defaultAnalyzer, mapping);
	}

	/**
	 * Creates a map of fieldname -> analyzer. Without the analyzer for ID-field
	 * 
	 * @param defaultAna the default Analyzer
	 * @return a map of fieldname -> analyzer.
	 */
	public static Map<String, Analyzer> getFieldnameAnalyzerMapping(
			IndexSetting is, Analyzer defaultAna) throws LuceneIndexException {

		Map<String, Analyzer> mapping = new HashMap<>();

		Map<String, Analyzer> pool = getAnalyzerPool(is);

		for (FieldSetting fs : is.getFieldSettings()) {
			String fieldname = fs.getFieldname();
			Analyzer ana = pool.get(fs.getAnalyzername());
			if (ana == null)
				ana = defaultAna;
			mapping.put(fieldname, ana);
		}
		return mapping;
	}

	/**
	 * creates a map of analyzername -> analyzer.
	 * <p>
	 * CAVE: Not a map of fieldname -> analyzer!
	 * 
	 * @return a map of analyzername -> analyzer.
	 */
	public static Map<String, Analyzer> getAnalyzerPool(IndexSetting is)
			throws LuceneIndexException {
		Map<String, Analyzer> pool = new HashMap<>();
		for (AnalyzerSetting as : is.getAnalyzerSettings()) {
			pool.put(as.getAnalyzerName(), createAnalyzer(as));
		}
		return pool;
	}

}
