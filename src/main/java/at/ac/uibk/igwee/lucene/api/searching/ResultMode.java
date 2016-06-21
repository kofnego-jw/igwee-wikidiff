package at.ac.uibk.igwee.lucene.api.searching;

/**
 * Modes for viewing results
 * @author Joseph
 *
 */
public enum ResultMode {
	
	/**
	 * This field will never be used for viewing. usual setting for "catch-all" fields
	 */
	NEVER,
	/**
	 * This field will always be included in the result set.
	 */
	ALWAYS,
	/**
	 * Full content of the field will be included if found. 
	 */
	FULLTEXT_WHEN_FOUND,
	/**
	 * Snippet(s) will be included if found.
	 */
	SNIPPET_WHEN_FOUND

}
