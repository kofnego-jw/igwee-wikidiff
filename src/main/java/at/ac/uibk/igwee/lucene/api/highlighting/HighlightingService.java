package at.ac.uibk.igwee.lucene.api.highlighting;

import at.ac.uibk.igwee.lucene.api.searching.SearchRequest;

/**
 * Highlighting service.
 * @author Joseph
 *
 */
public interface HighlightingService {
	
	/**
	 *
	 * @param request 
	 * @param textToHighlight the text that should be highlighted
	 * @return the highlighted text, using the settings in the searchsetting. it always returns the whole text, even if there is no 
	 * 		   word highlighted. Never returns null, if the textToHighlight is null, it will return an empty string.
	 */
	String highlightText(SearchRequest request, String textToHighlight) throws LuceneHighlightException;
	
	/**
	 * 
	 * @param request
	 * @param textToHighlight
	 * @param startTag
	 * @param endTag
	 * @return
	 * @throws LuceneHighlightException
	 */
	String highlightText(SearchRequest request, String textToHighlight, String startTag, String endTag)
			throws LuceneHighlightException;
	
	/**
	 * Same as highlightText with a search request.
	 * @param indexName
	 * @param queryString
	 * @param textToHighlight
	 * @return
	 */
	String highlightText(String indexName, String queryString, String textToHighlight)
			throws LuceneHighlightException;
	
	/**
	 * 
	 * @param indexName
	 * @param queryString
	 * @param textToHighlight
	 * @param startTag
	 * @param endTag
	 * @return
	 * @throws LuceneHighlightException
	 */
	String highlightText(String indexName, String queryString, String textToHighlight,
			String startTag, String endTag) throws LuceneHighlightException;
	
	/**
	 * 
	 * @param sr
	 * @return the default starting tag or empty string
	 */
	String getStartTag(SearchRequest sr);
	
	/**
	 * 
	 * @param indexName
	 * @return the default starting tag or empty string
	 */
	String getStartTag(String indexName);
	
	/**
	 * 
	 * @param sr
	 * @return the default end tag or empty string
	 */
	String getEndTag(SearchRequest sr);
	/**
	 * 
	 * @param indexName
	 * @return the default endtag or empty string
	 */
	String getEndTag(String indexName);
	

}
