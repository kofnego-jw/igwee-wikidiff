package at.ac.uibk.igwee.lucene.json;

/**
 * Data object for a query clause
 * @author totoro
 *
 */
public class JsonQueryClause {

	/**
	 * The fieldname
	 */
	private String fieldname;
	/**
	 * The query string
	 */
	private String queryString;
	/**
	 * The query type. Default: QUERYPARSER
	 */
	private QueryType queryType = QueryType.QUERYPARSER;
	/**
	 * The chain type. Default: OR
	 */
	private ChainType chainType = ChainType.OR;
	
	
	public JsonQueryClause() {
		super();
	}
	
	/**
	 * 
	 * @param fieldname
	 * @param queryString
	 * @param queryType
	 * @param chainType
	 */
	public JsonQueryClause(String fieldname, String queryString, QueryType queryType, ChainType chainType) {
		super();
		this.fieldname = fieldname;
		this.queryString = queryString;
		this.queryType = queryType;
		this.chainType = chainType;
	}
	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}
	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}
	
	/**
	 * @return the queryType
	 */
	public QueryType getQueryType() {
		return queryType;
	}
	/**
	 * @return the chainType
	 */
	public ChainType getChainType() {
		return chainType;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsonQueryClause [fieldname=" + fieldname + ", queryString="
				+ queryString + "]";
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fieldname == null) ? 0 : fieldname.hashCode());
		result = prime * result
				+ ((queryString == null) ? 0 : queryString.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JsonQueryClause other = (JsonQueryClause) obj;
		if (fieldname == null) {
			if (other.fieldname != null)
				return false;
		} else if (!fieldname.equals(other.fieldname))
			return false;
		if (queryString == null) {
			if (other.queryString != null)
				return false;
		} else if (!queryString.equals(other.queryString))
			return false;
		return true;
	}
	
	
	
	
}
