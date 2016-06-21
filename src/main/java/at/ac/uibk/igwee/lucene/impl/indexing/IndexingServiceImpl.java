package at.ac.uibk.igwee.lucene.impl.indexing;

import at.ac.uibk.igwee.lucene.api.Constants;
import at.ac.uibk.igwee.lucene.api.LuceneException;
import at.ac.uibk.igwee.lucene.api.indexing.*;
import at.ac.uibk.igwee.lucene.impl.FacetsConfigHolder;
import at.ac.uibk.igwee.lucene.impl.LuceneApplication;
import at.ac.uibk.igwee.lucene.impl.LuceneDirHolder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.*;
/**
 * Basic implementation of IndexingService
 *
 * Basic function principle:
 *   Create an IndexingServiceImpl use the LuceneApplication,
 *   use the doIndex() or doIndexWithFacets() methods to index your document.
 *
 * For each call of doIndex..() the index will create
 *   1. A StringField (DOC_ID_DEFAULT_FIELDNAME, docId) and a SortedDocValuesField(DOC_ID_DEFAULT_FIELDNAME)
 *   2. For each FieldContent: A Field
 *   3. For each fieldname in fieldContents: A SortedDocValueField or a SortedNumberValueField
 *   4. A document containing all these fields
 *   5. If necessary: For each FacetContent a FacetField will be created.
 *
 * @author Joseph
 *
 */
public class IndexingServiceImpl implements IndexingService {

	private static final int INDEXWRITER_POOLSIZE = 20;

    private static final int MAX_DOCVALUES_SIZE = 1000;

	/**
	 * A set of IndexSettings
	 */
	private Set<IndexSetting> indexSettings = new HashSet<>();

	/**
	 * Cache of IndexWriter
	 */
	private LinkedList<CacheEntry> cache = new LinkedList<>();
	
	/**
	 * DirHolder for getting the IndexWriter.
	 */
	private LuceneDirHolder dirHolder;

    /**
     * FacetsConfigHolder
     */
    private FacetsConfigHolder facetsConfigHolder;

    /**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexingServiceImpl.class);


	/**
	 * Constructor, pointing to the base directory.
	 * @param dirH LuceneDirHolder
	 * @param settings Settings
	 */
	public IndexingServiceImpl(LuceneDirHolder dirH, FacetsConfigHolder facetsConfigHolder, Set<IndexSetting> settings) {
		this.dirHolder = dirH;
		this.indexSettings = settings;
        this.facetsConfigHolder = facetsConfigHolder;
	}

	/**
	 * 
	 * @param indexName
	 * @return the IndexWriterConfig for the given index.
	 * @throws LuceneIndexException if no indexSetting can be found or if 
	 * the analyzer cannot be created.
	 */
	private IndexWriterConfig getDefaultIndexWriterConfig(String indexName) 
			throws LuceneIndexException {
		IndexSetting is = getIndexSetting(indexName);
		if (is == null)
			throw new LuceneIndexException("Cannot find the indexSetting for '" + 
					indexName + "'.");
		Analyzer defAnalyzer = IndexingFactory.createPerFieldAnalyzer(is);
		IndexWriterConfig conf = new IndexWriterConfig(defAnalyzer);
		conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
		return conf;
	}
	
	/**
	 * Gets an IndexWriterConfig with open mode set to OpenMode.CREATE
	 * @param indexName
	 * @return
	 * @throws LuceneIndexException
	 */
	private IndexWriterConfig getCreateNewIndexWriterConfig(String indexName) 
			throws LuceneIndexException {
		IndexWriterConfig conf = getDefaultIndexWriterConfig(indexName);
		conf.setOpenMode(OpenMode.CREATE);
		return conf;
	}

	/**
	 * Gets a TaxonomyWriter instance with OpenMode.CREATE_OR_APPEND
	 * @param indexName
	 * @return
	 * @throws LuceneIndexException
	 */
	private TaxonomyWriter getTaxonomyWriter(String indexName) 
			throws LuceneIndexException {
		String taxIndexName = getTaxonomyWriterName(indexName);
		CacheEntry ce = getCacheEntry(taxIndexName);
		if (ce!=null) {
			return ce.getTaxonomyWriter();
		}
		Directory dir = getDirectory(taxIndexName);
		TaxonomyWriter writer = null;
		try {
			writer = new DirectoryTaxonomyWriter(dir, IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		} catch (Exception e) {
			throw new LuceneIndexException("Cannot create the TaxonomyWriter '" + taxIndexName + "'.", e);
		}
		
		addToCache(taxIndexName, writer);
		return writer;
	}
	
	/**
	 * Gets an IndexWriter instance with OpenMode.CREATE_OR_APPEND
	 * @param indexName
	 * @return
	 * @throws LuceneIndexException
	 */
	private IndexWriter getIndexWriter(String indexName) throws LuceneIndexException {
		CacheEntry ce = getCacheEntry(indexName);
		if (ce!=null) return ce.getIndexWriter();
		
		IndexWriter iw;
		Directory dir = getDirectory(indexName);
		IndexWriterConfig conf = getDefaultIndexWriterConfig(indexName);
		try {
			iw = new IndexWriter(dir, conf);
		} catch (Exception e) {
			throw new LuceneIndexException("Cannot create IndexWriter '" + 
					indexName + "' for create or append.", e);
		}
		addToCache(indexName, iw);
		return iw;
	}
	
	/**
	 * Gets an IndexWriter instance with OpenMode.CREATE. Will also create 
	 * an TaxonomyWriter with OpenMode.CREATE and put it into cache.
	 * @param indexName
	 * @return
	 * @throws LuceneIndexException
	 */
	private IndexWriter createNewIndexWriter(String indexName) 
			throws LuceneIndexException {
		CacheEntry ce = getCacheEntry(indexName);
		if (ce!=null) this.closeIndexWriterAndTaxonomyWriter(indexName);
		
		IndexWriter iw;
		Directory dir = getDirectory(indexName);
		IndexWriterConfig conf = this.getCreateNewIndexWriterConfig(indexName);
		try {
			iw = new IndexWriter(dir, conf);
		} catch (Exception e) {
			throw new LuceneIndexException("Cannot create IndexWriter '" + indexName 
					+ "' for new creation.", e);
		}
		addToCache(indexName, iw);
		
		String taxName = getTaxonomyWriterName(indexName);
		Directory taxDir = getDirectory(taxName);
		TaxonomyWriter tw = null;
		try {
			tw = new DirectoryTaxonomyWriter(taxDir, 
					IndexWriterConfig.OpenMode.CREATE);
			
		} catch (Exception e) {
			throw new LuceneIndexException("Cannot create TaxonomyWriter '" + taxName + 
					"' for new creation.", e );
		}
		addToCache(taxName, tw);
		
		return iw;
	}
	
	/**
	 * Adds an IndexWriter or a TaxonomyWriter to Cache. Deletes the first cache
	 * @param indexName
	 * @param writer
	 * @throws LuceneIndexException
	 */
	private void addToCache(String indexName, Closeable writer) throws LuceneIndexException{
		cache.add(new CacheEntry(indexName, writer));
		if (cache.size()>INDEXWRITER_POOLSIZE) {
			CacheEntry ce = cache.remove();
			LOGGER.info("Closing {} for cache.", ce.getName());
			try {
				ce.getWriter().close();
			} catch (Exception e) {
				LOGGER.warn("Exception happend while closing an index '" + ce.getName() + "'.", e);
			}
		}
	}
	/**
	 * Gets the directory for the given name.
	 * @param dirName
	 * @return a directory
	 * @throws LuceneIndexException
	 */
	private Directory getDirectory(String dirName) throws LuceneIndexException {
		Directory dir;
		try {
			dir = dirHolder.getDirectory(dirName);
		} catch (LuceneException e) {
			throw new LuceneIndexException("Cannot obtain a directory for '" + dirName + "'.", e);
		}
		return dir;
	}

	/**
	 * Creates a document with the given information.
	 * @param indexName name of the index
	 * @param docId the id of the index. must not be null and 
	 * @param contents FieldContents
	 * @return a document
	 */
	private Document createDocument(String indexName, String docId,
			List<FieldContent> contents) throws LuceneIndexException {
		IndexSetting is = getIndexSetting(indexName);
		if (is == null)
			throw new LuceneIndexException("Cannot find indexSetting for '" + indexName + "'.");
		Document doc = new Document();
		Field idFd = new StringField(Constants.DOC_ID_DEFAULT_FIELDNAME, docId,
				Store.YES);
		doc.add(idFd);
        Field idFdSorted = new SortedDocValuesField(Constants.DOC_ID_DEFAULT_FIELDNAME, new BytesRef(docId));
        doc.add(idFdSorted);
        Map<String, List<String>> fields = new HashMap<>();
        contents.stream()
                .filter(c -> c!=null && c.getContent()!=null)
                .forEach(content -> {
                    if (fields.get(content.getFieldname())!=null) {
                        fields.get(content.getFieldname()).add(content.getContent());
                    } else {
                        List<String> newList = new ArrayList<>();
                        newList.add(content.getContent());
                        fields.put(content.getFieldname(), newList);
                    }
                });
        for (Map.Entry<String, List<String>> entry: fields.entrySet()) {
            FieldSetting fs = is.getFieldSetting(entry.getKey());
            if (fs==null) continue;
            FieldType ft = IndexingFactory.createFieldType(fs);
            entry.getValue().forEach(string -> {
                Field fd = new Field(entry.getKey(), string, ft);
                doc.add(fd);
            });
            Field docValueField = createDocValueField(fs, entry.getValue());
            if (docValueField!=null) doc.add(docValueField);
        }
//        System.out.println(doc.getFields());
		return doc;
	}



	@Override
	public void doIndex(String indexName, String docId,
			List<FieldContent> contents) throws LuceneIndexException {
		doIndex(indexName, docId, contents, false);
	}
	
	@Override 
	public void doIndexWithFacets(String indexName, String docId, List<FieldContent> contents, List<FacetContent> facets) 
			throws LuceneIndexException {
		doIndexWithFacets(indexName, docId, contents, facets, true);
	}
	
	@Override
	public void doIndexWithFacets(String indexName, String docId, 
			List<FieldContent> contents, List<FacetContent> facets, boolean overwrite) 
					throws LuceneIndexException {
        FacetsConfig facetsConfig = getFacetsConfig(indexName);

		if (facets==null || facets.isEmpty() || facetsConfig == null) {
            LOGGER.warn("No facet information found, do normal indexing.");
			doIndex(indexName, docId, contents, overwrite);
			return;
		}
		IndexWriter iw = getIndexWriter(indexName);
		Document doc = createDocument(indexName, docId, contents);
		try {
			TaxonomyWriter taxoWriter = getTaxonomyWriter(indexName);

			for (FacetContent facet: facets) {
                if (facet.getComponents()==null || facet.getComponents().size()<2) {
                    LOGGER.warn("Cannot add empty component.");
                    continue;
                }
                String dim = facet.getComponents().get(0);
                String[] paths = facet.getPath();
                doc.add(new FacetField(dim, paths));
			}
			if (overwrite) {
				Term idTerm = new Term(Constants.DOC_ID_DEFAULT_FIELDNAME, docId);
				iw.updateDocument(idTerm, facetsConfig.build(taxoWriter,doc));
			} else {
				iw.addDocument(facetsConfig.build(taxoWriter,doc));
			}
			iw.commit();
			taxoWriter.commit();
		} catch (Exception e) {
			throw new LuceneIndexException("Error while updating the index, docID '" + docId + "'.", e);
		}
	}

	@Override
	public void doIndex(String indexName, String docId,
			List<FieldContent> content, boolean overwrite) throws LuceneIndexException {
		IndexWriter iw = getIndexWriter(indexName);
		Document doc = createDocument(indexName, docId, content);
		try {
			if (overwrite) {
				Term idTerm = new Term(Constants.DOC_ID_DEFAULT_FIELDNAME, docId);
				iw.updateDocument(idTerm, doc);
			} else {
				iw.addDocument(doc);
			}
			iw.commit();
		} catch (Exception e) {
			throw new LuceneIndexException("Error while updating the index, docId '" + 
					docId + "'.", e);
		}
	}
	
	@Override
	public void removeDocumentFromIndex(String indexName, String docId) throws LuceneIndexException {
		IndexWriter iw = getIndexWriter(indexName);
		Term idTerm = new Term(Constants.DOC_ID_DEFAULT_FIELDNAME, docId);
		try {
			iw.deleteDocuments(idTerm);
			iw.commit();
		} catch (Exception e) {
			throw new LuceneIndexException("Error while deleting a document with docId '" + docId + "'.", e);
		}
	}
	
	
	@Override
	public void clearIndex(String indexName) throws LuceneIndexException {
		this.closeIndexWriterAndTaxonomyWriter(indexName);
		IndexWriter iw = this.createNewIndexWriter(indexName);
		TaxonomyWriter tw = this.getTaxonomyWriter(indexName);
		try {
			tw.commit();
			iw.commit();
		} catch (Exception e) {
			throw new LuceneIndexException("Exception while committing for the first time.", e);
		}
	}

    private static Field createDocValueField(FieldSetting fs, List<String> values) {
        if (fs==null || values==null || values.isEmpty()) return null;
        if (fs.getNumericType()==null || fs.getNumericType()== Constants.NumericType.NULL) {
            String s = values.stream().reduce((s1, s2) -> s1 + " " + s2).get();
            String val = s.length() > MAX_DOCVALUES_SIZE ? s.substring(0, MAX_DOCVALUES_SIZE) : s;
            return new SortedDocValuesField(fs.getFieldname(), new BytesRef(val));
        }
        long number;
        double factor = Math.pow(10, fs.getPrecisionStep());
        switch(fs.getNumericType()) {
            case FLOAT:
            case DOUBLE:
                try {
                    double parse = Double.parseDouble(values.get(0));
                    number = (long) (parse * factor);
                    break;
                } catch (Exception e) {
                    LOGGER.warn("Cannot parse a double number.", e);
                    return null;
                }
            default:
                try {
                    number = Long.parseLong(values.get(0));
                } catch (Exception e) {
                    LOGGER.warn("Cannot parse a long number.", e);
                    return null;
                }
        }
        return new SortedNumericDocValuesField(fs.getFieldname(), number);
    }


    /**
     *
     * @param indexname the indexname
     * @return a FacetsConfig object according to the configuration. Can be null if there is no
	 * 			FacetsConfig.
     */
    public FacetsConfig getFacetsConfig(String indexname) {
        FacetsConfig fc = facetsConfigHolder.getFacetsConfig(indexname);
        return fc;
    }
	
	/**
	 * 
	 * @param indexName
	 * @return the CacheEntry with the indexName. Or null, if no cache found.
	 */
	private CacheEntry getCacheEntry(String indexName) {
		for (CacheEntry now: this.cache) {
			if (now.getName().equals(indexName)) return now;
		}
		return null;
	}
	
	/**
	 * Closes the indexWriter and the taxonomy writer for the given indexName.
	 * Exception will be written to LOGGER.
	 * @param indexName
	 */
	private void closeIndexWriterAndTaxonomyWriter(String indexName) {
		String taxName = getTaxonomyWriterName(indexName);
		try {
			closeTaxonomyWriter(taxName);
		} catch (Exception e) {
			LOGGER.warn("Exception while closing a TaxonomyWriter '" + taxName + "'.", e);
		}
		try {
			closeIndexWriter(indexName);
		} catch (Exception e) {
			LOGGER.warn("Exception while closing an IndexWriter '" + indexName + "'.", e);
		}
	}
	
	/**
	 * Closes a taxonomyWriter with the TaxonomyName
	 * @param taxName
	 * @throws LuceneIndexException
	 */
	private synchronized void closeTaxonomyWriter(String taxName) 
			throws LuceneIndexException{
		LOGGER.info("closing {},", taxName);
		CacheEntry ce = getCacheEntry(taxName);
		if (ce==null) return;
		TaxonomyWriter tw = ce.getTaxonomyWriter();
		cache.remove(ce);
		try {
			tw.commit();
			tw.close();
		} catch (Exception e) {
			throw new LuceneIndexException("Exception while trying to close a TaxonomyWriter '" + taxName + "'.", e);
		}
		
	}



	/**
	 * Closes the indexWriter.
	 * @param indexName
	 * @throws LuceneIndexException
	 */
	private synchronized void closeIndexWriter(String indexName)  
			throws LuceneIndexException{
		LOGGER.info("closing {},", indexName);
		CacheEntry ce = getCacheEntry(indexName);
		if (ce==null) return;
		IndexWriter toClose = ce.getIndexWriter();
		cache.remove(ce);
		try {
			
			toClose.commit();
			toClose.close();
		} catch (Exception e) {
			throw new LuceneIndexException("Exception while clsoing IndexWriter '" + indexName + "'.", e);
		}
		
	}

	@Override
	public Set<IndexSetting> getIndexSettings() {
		return this.indexSettings;
	}

	@Override
	public void setIndexSettings(Set<IndexSetting> settings) {
		this.indexSettings = settings;
	}

	@Override
	public void addIndexSetting(IndexSetting setting) {

		if (indexSettings.contains(setting))
			indexSettings.remove(setting);
		indexSettings.add(setting);

	}

	@Override
	public void removeIndexSetting(String indexName) {
		IndexSetting toRemove = null;
		for (IndexSetting now : indexSettings)
			if (now.getIndexName().equals(indexName)) {
				toRemove = now;
				break;
			}
		if (toRemove != null)
			indexSettings.remove(toRemove);
	}

	@Override
	public IndexSetting getIndexSetting(String indexName) {
		for (IndexSetting is : indexSettings)
			if (is.getIndexName().equals(indexName))
				return is;
		return null;
	}

	@Override
	public synchronized void removeIndex(String indexName) {
		this.closeIndexWriterAndTaxonomyWriter(indexName);
	}
	
	@Override
	public Set<String> getAvailableTokenFilterFactoryNames() {
		return TokenFilterFactory.availableTokenFilters();
	}
	
	@Override
	public Set<String> getAvailableTokenizerFactoryNames() {
		return TokenizerFactory.availableTokenizers();
	}
	
	/**
	 * returns the default name for the directory storing the taxonomy.
	 * @param indexName
	 * @return
	 */
	private static String getTaxonomyWriterName(String indexName) {
		return indexName + LuceneApplication.TAXONOMY_DIRECTORY_DEFAULT_ENDING;
	}
	
	/**
	 * A cache entry.
	 * @author Apple
	 *
	 */
	private static final class CacheEntry {
		private final String name;
		private final Closeable writer;
		private CacheEntry(String name, Closeable writer) {
			this.name = name;
			this.writer = writer;
		}
		public String getName() {
			return this.name;
		}
		public IndexWriter getIndexWriter() throws LuceneIndexException {
			if (!(writer instanceof IndexWriter))
				throw new LuceneIndexException("Writer is not an IndexWriter: '" + name + "'.");
			return (IndexWriter) this.writer;
		}
		public TaxonomyWriter getTaxonomyWriter() throws LuceneIndexException {
			if (!(writer instanceof TaxonomyWriter))
				throw new LuceneIndexException("Writer is not a TaxonomyWriter: '" + name + "'.");
			return (TaxonomyWriter) writer;
		}
		public Closeable getWriter() {
			return this.writer;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheEntry other = (CacheEntry) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
		
	}
	
}
