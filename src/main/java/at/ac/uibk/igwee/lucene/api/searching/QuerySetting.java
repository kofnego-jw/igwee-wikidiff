package at.ac.uibk.igwee.lucene.api.searching;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Value object class for a query.
 * @author Joseph
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class QuerySetting implements java.io.Serializable {
	
	private static final long serialVersionUID = 201312091238L;
	
	/**
	 * Name of a field
	 */
	private String fieldname;
	/**
	 * the query string
	 */
	private String queryString;
	/**
	 * The type of chaining. Default: AND
	 */
	private ChainType chainType = ChainType.AND;
	/**
	 * The type of query: default: QUERYPARSER
	 */
	private QueryType queryType = QueryType.QUERYPARSER;
	
	public QuerySetting() {
		super();
	}
	
	public QuerySetting(String fieldname, String queryString) {
		this();
		setFieldname(fieldname);
		setQueryString(queryString);
	}

	public QuerySetting(String fieldname, String queryString, ChainType chainType, QueryType queryType) {
		this.fieldname = fieldname;
		this.queryString = queryString;
		this.chainType = chainType;
		this.queryType = queryType;
	}

	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * @param fieldname the fieldname to set
	 */
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @return the queryType
	 */
	public QueryType getQueryType() {
		return queryType;
	}

	/**
	 * @param queryType the queryType to set
	 */
	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}
	
	


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((chainType == null) ? 0 : chainType.hashCode());
		result = prime * result
				+ ((fieldname == null) ? 0 : fieldname.hashCode());
		result = prime * result
				+ ((queryString == null) ? 0 : queryString.hashCode());
		result = prime * result
				+ ((queryType == null) ? 0 : queryType.hashCode());
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
		QuerySetting other = (QuerySetting) obj;
		if (chainType != other.chainType)
			return false;
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
		if (queryType != other.queryType)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QuerySetting [fieldname=" + fieldname + ", queryString="
				+ queryString + ", chainType=" + chainType + ", queryType="
				+ queryType + "]";
	}

	/**
	 * @return the chainType
	 */
	public ChainType getChainType() {
		return chainType;
	}

	/**
	 * @param chainType the chainType to set
	 */
	public void setChainType(ChainType chainType) {
		this.chainType = chainType;
	}




	/**
	 * Enum for chaining type.
	 * @author Joseph
	 *
	 */
	public enum ChainType {
		NOT,
		AND,
		OR
	}

	/**
	 * Enum for query type.
	 * @author Joseph
	 *
	 */
	public enum QueryType {
		TERM,
		RANGE,
		QUERYPARSER
	}

}
