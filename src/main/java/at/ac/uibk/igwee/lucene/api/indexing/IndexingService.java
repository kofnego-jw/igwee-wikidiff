package at.ac.uibk.igwee.lucene.api.indexing;

import java.util.List;
import java.util.Set;

/**
 * This is the low level interface every indexing service should implement.
 * @author Joseph
 *
 */
public interface IndexingService {
	
	/**
	 * Put some fields into the index. Default: Delete the old document and put the new one instead.
	 * @param indexName The name of the index.
	 * @param docId The id of the document.
	 * @param contents A list of FieldContent. 
	 */
	void doIndex(String indexName, String docId, List<FieldContent> contents)
			throws LuceneIndexException;
	
	/**
	 * Put some fields into the index with some facets.
	 * @param indexName 
	 * @param docId
	 * @param contentx
	 * @param facets list of facets, eg. {"author|Einstein, Albert", "year|2001"}
	 * @throws LuceneIndexException
	 */
	void doIndexWithFacets(String indexName, String docId, List<FieldContent> contentx, List<FacetContent> facets)
			throws LuceneIndexException;
	
	/**
	 * Put some fields into the index.
	 * @param indexName name of the index
	 * @param docId id of the document
	 * @param content contents
	 * @param overwrite should overwrite the old content?
	 */
	void doIndex(String indexName, String docId, List<FieldContent> content,
			boolean overwrite) throws LuceneIndexException;
	
	/**
	 * The same as doIndex(String,String,List<FieldContent,boolean), but only adds Facets.
	 * Cave: obsolete facets will not be deleted. 
	 * @param indexName
	 * @param docId
	 * @param content
	 * @param facets
	 * @param overwrite
	 * @throws LuceneIndexException
	 */
	void doIndexWithFacets(String indexName, String docId, List<FieldContent> content, List<FacetContent> facets,
			boolean overwrite) throws LuceneIndexException;
	
	/**
	 * Removes a document form an index.
	 * @param indexName
	 * @param docId
	 * @throws LuceneIndexException
	 */
	void removeDocumentFromIndex(String indexName, String docId) throws LuceneIndexException;
	
	/**
	 * Clears the whole index. The index must be recreated.
	 * @param indexName
	 * @throws LuceneIndexException
	 */
	void clearIndex(String indexName) throws LuceneIndexException;
	
	/**
	 * 
	 * @return the indexSettings.
	 */
	Set<IndexSetting> getIndexSettings();
	
	/**
	 * @param settings
	 */
	void setIndexSettings(Set<IndexSetting> settings);
	
	/**
	 * Adds an indexSetting.
	 * @param setting
	 */
	void addIndexSetting(IndexSetting setting);
	
	/**
	 * Removes an indexSetting.
	 * @param indexName
	 */
	void removeIndexSetting(String indexName);
	
	/**
	 * @param indexName
	 * @return the indexSetting for the indexName or null.
	 */
	IndexSetting getIndexSetting(String indexName);
	
	/**
	 * Removes an index totally.
	 * @param indexName
	 * @throws LuceneIndexException if any things goes wrong.
	 */
	void removeIndex(String indexName);
	
	/**
	 * 
	 * @return a Set of String containing all names for TokenFilterFactory
	 */
	Set<String> getAvailableTokenFilterFactoryNames();
	
	/**
	 * 
	 * @return a Set of String containing all names for TokenizerFactory
	 */
	Set<String> getAvailableTokenizerFactoryNames();
	
}
