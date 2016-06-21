package at.ac.uibk.igwee.lucene.json;

/**
 * The query type
 * @author totoro
 *
 */
public enum QueryType {
	/**
	 * Use a term
	 */
	TERM, 
	/**
	 * Use a query parser
	 */
	QUERYPARSER, 
	/**
	 * The query is of a range.
	 */
	RANGE;

}
