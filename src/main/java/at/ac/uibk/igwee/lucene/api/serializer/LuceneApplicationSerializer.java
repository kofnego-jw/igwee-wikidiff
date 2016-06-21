package at.ac.uibk.igwee.lucene.api.serializer;

import at.ac.uibk.igwee.lucene.api.LuceneApplicationConfig;
import at.ac.uibk.igwee.lucene.api.LuceneException;
import at.ac.uibk.igwee.lucene.api.indexing.*;
import at.ac.uibk.igwee.lucene.api.searching.ResultSetting;
import at.ac.uibk.igwee.lucene.api.searching.SearchSetting;
import at.ac.uibk.igwee.lucene.api.viewing.DecoratorSetting;
import at.ac.uibk.igwee.lucene.api.viewing.FacetTranslation;
import at.ac.uibk.igwee.lucene.api.viewing.ViewSetting;
import at.ac.uibk.igwee.xstream.api.XStreamService;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Locale;

public class LuceneApplicationSerializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneApplicationSerializer.class);
	
	public static final String INDEXSETTING_DEFAULT_ENDING = ".is.xml";
	
	public static final String SEARCHSETTING_DEFAULT_ENDING = ".ss.xml";
	
	public static final String VIEWSETTING_DEFAULT_ENDING = ".vs.xml";

	
	private static final XStream XSTREAM;
	static {
		XSTREAM = new XStream();
		XSTREAM.setClassLoader(LuceneApplicationSerializer.class.getClassLoader());
		XSTREAM.setMode(XStream.NO_REFERENCES);
		XSTREAM.alias("IndexSetting", IndexSetting.class);
		XSTREAM.alias("AnalyzerSetting", AnalyzerSetting.class);
		XSTREAM.alias("FactorySetting", FactorySetting.class);
		XSTREAM.alias("FieldSetting", FieldSetting.class);
        XSTREAM.alias("FacetSetting", FacetSetting.class);
		
		XSTREAM.alias("SearchSetting", SearchSetting.class);
		XSTREAM.alias("ResultSetting", ResultSetting.class);
		
		XSTREAM.alias("ViewSetting", ViewSetting.class);
		XSTREAM.alias("DecoratorSetting", DecoratorSetting.class);
		XSTREAM.alias("FacetContent", FacetContent.class);
		XSTREAM.alias("FacetTranslation", FacetTranslation.class);
		
		XSTREAM.alias("locale", Locale.class);
		
	}
	
	private LuceneApplicationSerializer() {
		super();
	}
	
	public static ViewSetting loadViewSetting(InputStream is) throws LuceneException {
		String xml = toXmlString(is);
		try {
			return (ViewSetting) XSTREAM.fromXML(xml);
		} catch (Exception e) {
            LOGGER.error("Cannot convert xml to ViewSetting.", e);
			throw new LuceneException("Cannot convert xml to ViewSetting object.", e);
		}
	}
	
	public static ViewSetting loadViewSetting(File file) throws LuceneException {
        LOGGER.info("Read ViewSetting from {}.", file.getAbsolutePath());
		return loadViewSetting(getInputStreamFromFile(file));
	}
	
	public static SearchSetting loadSearchSetting(InputStream is) throws LuceneException {
		String xml = toXmlString(is);
		try {
			return (SearchSetting) XSTREAM.fromXML(xml);
		} catch (Exception e) {
            LOGGER.error("Cannot convert xml to SearchSetting.", e);
			throw new LuceneException("Cannot convert xml to SearchSetting object.", e);
		}
	}
	
	public static SearchSetting loadSearchSetting(File file) throws LuceneException {
        LOGGER.info("Read SearchSetting from {}.", file.getAbsolutePath());
		return loadSearchSetting(getInputStreamFromFile(file));
	}
	
	public static IndexSetting loadIndexSetting(InputStream is) throws LuceneException {
		String xml = toXmlString(is);
		try {
			return (IndexSetting) XSTREAM.fromXML(xml);
		} catch (Exception e) {
            LOGGER.error("Cannot convert xml to IndexSetting.", e);
			throw new LuceneException("Cannot convert xml to IndexSetting object.", e);
		}
	}
	
	public static IndexSetting loadIndexSetting(File file) throws LuceneException {
        LOGGER.info("Read IndexSetting from {}.", file.getAbsolutePath());
		return loadIndexSetting(getInputStreamFromFile(file));
	}

	
	private static String toXmlString(InputStream is) throws LuceneException {
		try {
			return XStreamService.fromInputStream(is);
		} catch (Exception e) {
            LOGGER.error("Cannot convert InputStream to XML String.", e);
			throw new LuceneException("Cannot convert to XML String.", e);
		}
	}
	
	private static InputStream getInputStreamFromFile(File file) throws LuceneException {
		try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (Exception e) {
			throw new LuceneException("Cannot open the file '" + file.getAbsolutePath() + "'.", e);
		}
	}

    /**
     *
     * @param baseDir the basic directory
     * @return a LuceneApoplicationConfig object
     * @throws LuceneException if any exception happens
     */
	public static LuceneApplicationConfig loadApplicationConfig(String baseDir) throws LuceneException {
		LuceneApplicationConfig result = new LuceneApplicationConfig();
		result.setIndexBaseDir(baseDir);
		File configBase = new File(result.getConfigurationDir());
		if (configBase.exists()) {
			for (File now : configBase.listFiles(file -> file.getName().endsWith(INDEXSETTING_DEFAULT_ENDING))) {
				IndexSetting is;
				try {
					is = loadIndexSetting(now);
				} catch (LuceneException e) {
                    LOGGER.warn("Cannot load an IndexSetting.", e);
                    continue;
				}
				result.addIndexSetting(is);
			}
			for (File now: configBase.listFiles(file -> file.getName().endsWith(SEARCHSETTING_DEFAULT_ENDING))) {
                SearchSetting ss;
				try {
                    ss = loadSearchSetting(now);
				} catch (LuceneException e) {
                    LOGGER.warn("Cannot load a SearchSetting.");
                    continue;
				}
                result.addSearchSetting(ss);
			}
			for (File now: configBase.listFiles(file -> file.getName().endsWith(VIEWSETTING_DEFAULT_ENDING))) {
                ViewSetting vs;
				try {
                    vs = loadViewSetting(now);
				} catch (LuceneException e) {
                    LOGGER.warn("Cannot load a ViewSetting.", e);
                    continue;
				}
                result.addViewSetting(vs);
			}
			// TODO Load Settings from other parts
		}
		return result;
	}

	private static void checkParent(File file) throws LuceneException {
		if (!file.getParentFile().exists()) {
            try {
                file.getParentFile().mkdirs();
            } catch (Exception e) {
                throw new LuceneException("Cannot create directory '" + file.getParentFile().getAbsolutePath() + "'.");
            }
        }
	}
	
	private static OutputStream getOutputStreamFromFile(File output) throws LuceneException {
		try {
			return new BufferedOutputStream(new FileOutputStream(output));
		} catch (Exception e) {
			throw new LuceneException("Cannot open the file for writing '" + output.getAbsolutePath() + "'.", e);
		}
	}
	
	public static void saveIndexSetting(IndexSetting is, File configBaseDir) throws LuceneException {
		String filename = is.getIndexName() + INDEXSETTING_DEFAULT_ENDING;
		File toSave = new File(configBaseDir, filename);
		checkParent(toSave);
		try {
			String xml = XSTREAM.toXML(is);
			XStreamService.toOutputStream(xml, getOutputStreamFromFile(toSave));
		} catch (Exception e) {
			throw new LuceneException("Cannot save IndexSetting to '" + toSave.getAbsolutePath() + "'.", e);
		}
	}
	
	public static void saveIndexSetting(IndexSetting is, OutputStream os) throws LuceneException {
		try {
			String xml = XSTREAM.toXML(is);
			XStreamService.toOutputStream(xml, os);
		} catch (Exception e) {
			throw new LuceneException("Cannot save IndexSetting to OutputStream.", e);
		}
	}
	
	public static void saveSearchSetting(SearchSetting ss, File configBaseDir) throws LuceneException {
		String filename = ss.getIndexName() + SEARCHSETTING_DEFAULT_ENDING;
		File toSave = new File(configBaseDir, filename);
		checkParent(toSave);
		try {
			String xml = XSTREAM.toXML(ss);
			XStreamService.toOutputStream(xml, getOutputStreamFromFile(toSave));
		} catch (Exception e) {
			throw new LuceneException("Cannot save SearchSetting to '" + toSave.getAbsolutePath() + "'.", e);
		}
	}
	
	public static void saveSearchSetting(SearchSetting ss, OutputStream os) throws LuceneException {
		try {
			String xml = XSTREAM.toXML(ss);
			XStreamService.toOutputStream(xml, os);
		} catch (Exception e) {
			throw new LuceneException("Cannot save SearchSetting to OutputStream.", e);
		}
	}
	
	public static void saveViewSetting(ViewSetting vs, File configBaseDir) throws LuceneException {
		String filename = vs.getIndexName() + VIEWSETTING_DEFAULT_ENDING;
		File toSave = new File(configBaseDir, filename);
		checkParent(toSave);
		try {
			String xml = XSTREAM.toXML(vs);
			XStreamService.toOutputStream(xml, getOutputStreamFromFile(toSave));
		} catch (Exception e) {
			throw new LuceneException("Cannot save ViewSetting to '" + toSave.getAbsolutePath() + "'.", e);
		}
	}
	
	public static void saveViewSetting(ViewSetting vs, OutputStream os) throws LuceneException {
		try {
			String xml = XSTREAM.toXML(vs);
			XStreamService.toOutputStream(xml, os);
		} catch (Exception e) {
			throw new LuceneException("Cannot save ViewSetting to OutputStream.", e);
		}
	}

    /**
     *
     * @param config The LuceneApplicationConfig to be saved.
     * @throws LuceneException
     */
	public static void saveLuceneApplicationConfigFile(LuceneApplicationConfig config) throws LuceneException {
		File configBaseDir = new File(config.getConfigurationDir());
		LOGGER.info("Saving LuceneApplicationConfig to {}.", configBaseDir.getAbsolutePath());
		for (IndexSetting is: config.getIndexSettings()) {
			try {
				saveIndexSetting(is, configBaseDir);
			} catch (Exception e) {
                LOGGER.error("Cannot save IndexSetting '" + is.getIndexName() + "'.", e);
			}
		}
		
		for (SearchSetting ss: config.getSearchSettings()) {
			try {
				saveSearchSetting(ss, configBaseDir);
			} catch (Exception e) {
                LOGGER.error("Cannot save SearchSetting '" + ss.getIndexName() + "'.", e);
			}
		}
		
		for (ViewSetting vs: config.getViewSettings()) {
			try {
				saveViewSetting(vs, configBaseDir);
			} catch (Exception e) {
                LOGGER.error("Cannot save IndexSetting '" + vs.getIndexName() + "'.", e);
			}
		}
        LOGGER.info("Saving LuceneApplicationConfig complete.");
		
	}
	
}
