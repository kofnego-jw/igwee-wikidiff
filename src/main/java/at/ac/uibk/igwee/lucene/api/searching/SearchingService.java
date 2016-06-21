package at.ac.uibk.igwee.lucene.api.searching;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;

/**
 * Searching Service: Used to search within a Lucene Index
 * @author Joseph
 *
 */
public interface SearchingService {
	
	/**
	 * 
	 * @param indexName
	 * @return the searchSetting for the given indexName.
	 * @throws LuceneSearchException
	 */
	SearchSetting getSearchSetting(String indexName) throws LuceneSearchException;
	
	/**
	 * Do a search
	 * @param request The SearchRequest object
	 * @return a SearchResult, it can be empty, but should never be null.
	 * @throws LuceneSearchException
	 */
	SearchResult search(SearchRequest request) throws LuceneSearchException;
	
	/**
	 * Helper method for searching
	 * @param indexName
	 * @param queryString
	 * @return a SearchResult, it can be empty, but should never be null.
	 * @throws LuceneSearchException
	 */
	SearchResult search(String indexName, String queryString) throws LuceneSearchException;
	
	/**
	 * 
	 * @return a list all index names.
	 */
	Set<String> getAllIndexNames();
	
	/**
	 * 
	 * @param indexName
	 * @return a list of searchable fields in the given index.
	 */
	Set<String> getSearchableFields(String indexName);
	
	/**
	 * 
	 * @param indexName
	 * @param field
	 * @return A List of indexed strings in the specific field.
	 */
	List<TermFrequency> getIndexedTerms(String indexName, String field) throws LuceneSearchException;
	
	/**
	 * 
	 * @param indexName
	 * @param field
	 * @return A list of TermDocs containing the term and the ids of the docs, as stored in Constants.DOC_ID_DEFAULT_FIELDNAME
	 * @throws LuceneSearchException
	 */
	List<TermDocs> getTermDocs(String indexName, String field) throws LuceneSearchException;

    /**
     *
     * @param indexName the name of the index
     * @return a FacetSearchResult containing the DEFAULT_FACET_ROOT as Base and the index base facets as
     * its chidren. Note, the value should be set to -1 to make clear that no document counts were made.
     * @throws LuceneSearchException
     */
	FacetSearchResult listBaseFacets(String indexName) throws LuceneSearchException;
	
	/**
	 * 
	 * @param indexName
	 * @param baseFacet
	 * @return FacetSearchResult containing all children facets. one level
	 * @throws LuceneSearchException
	 */
	default FacetSearchResult listSubCategories(String indexName, FacetContent baseFacet) throws LuceneSearchException {
		return listSubCategories(indexName, baseFacet, 1);
	}
	
	/**
	 * CAVE: subcategories already in baseFacet will be cleared first.
	 * 
	 * @param indexName
	 * @param baseFacet
	 * @param level level of children: 1 = children, 2=grandchildren 3=grand-grand-children.
	 * @return the baseFacet, enriched with subcategories.
	 * @throws LuceneSearchException
	 */
	FacetSearchResult listSubCategories(String indexName, FacetContent baseFacet, int level) throws LuceneSearchException;
		
}
