package at.ac.uibk.igwee.lucene.json;

/**
 * How the queries are chained.
 * @author totoro
 *
 */
public enum ChainType {
	
	/**
	 * NOT : The clause must not be met.
	 */
	NOT, 
	/**
	 * AND: The clause MUST be fulfilled.
	 */
	AND, 
	/**
	 * OR : The clause should be fulfilled.
	 */
	OR;

}
