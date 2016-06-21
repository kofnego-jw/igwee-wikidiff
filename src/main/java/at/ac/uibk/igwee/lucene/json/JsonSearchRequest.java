package at.ac.uibk.igwee.lucene.json;

import java.util.List;
/**
 * Data object: JsonSearchRequest. Represents a SearchRequest. Can be
 * serialized to JSON using JsonObjectMapper.
 * @author totoro
 *
 */
public class JsonSearchRequest {
	
	/**
	 * Default page number: 0
	 */
	public static final int DEFAULT_PAGENUMBER = 0;
	
	/**
	 * Default page size: 20
	 */
	public static final int DEFAULT_PAGESIZE = 20;
	
	/**
	 * The name of the lucene index
	 */
	private String indexName;
	
	/**
	 * An array of search clauses.
	 */
	private JsonQueryClause[] searches;
	
	/**
	 * Pagenumber
	 */
	private int pageNumber;
	
	/**
	 * Page size
	 */
	private int pageSize;
	
	/**
	 * Sorting clause. Currently there can be only one.
	 */
	private JsonSortingClause sorting;

	public JsonSearchRequest() {
		super();
	}
	
	public JsonSearchRequest(String indexName, List<JsonQueryClause> searches,
			int pageNumber, int pageSize, JsonSortingClause sorting) {
		super();
		this.indexName = indexName;
		this.searches = searches.toArray(new JsonQueryClause[0]);
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.sorting = sorting;
	}
	
	public JsonSearchRequest(String indexName, String fieldname, String queryString) {
		super();
		this.indexName = indexName;
		this.searches = new JsonQueryClause[] {
				new JsonQueryClause(fieldname, queryString, QueryType.QUERYPARSER, ChainType.OR)
		};
		this.pageNumber = DEFAULT_PAGENUMBER;
		this.pageSize = DEFAULT_PAGESIZE;
		this.sorting = null;
	}

	/**
	 * @param indexName the indexName to set
	 */
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	/**
	 * @param searches the searches to set
	 */
	public void setSearches(JsonQueryClause[] searches) {
		this.searches = searches;
	}
	
	public void setSearchesByList(List<JsonQueryClause> searches) {
		this.searches = searches.toArray(new JsonQueryClause[0]);
	}

	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @param sorting the sorting to set
	 */
	public void setSorting(JsonSortingClause sorting) {
		this.sorting = sorting;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return indexName;
	}

	/**
	 * @return the searches
	 */
	public JsonQueryClause[] getSearches() {
		return searches;
	}

	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the sorting
	 */
	public JsonSortingClause getSorting() {
		return sorting;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsonSearchRequest [indexName=" + indexName + ", searches="
				+ searches + ", pageNumber=" + pageNumber + ", pageSize="
				+ pageSize + ", sorting=" + sorting + "]";
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indexName == null) ? 0 : indexName.hashCode());
		result = prime * result + pageNumber;
		result = prime * result + pageSize;
		result = prime * result
				+ ((searches == null) ? 0 : searches.hashCode());
		result = prime * result + ((sorting == null) ? 0 : sorting.hashCode());
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
		JsonSearchRequest other = (JsonSearchRequest) obj;
		if (indexName == null) {
			if (other.indexName != null)
				return false;
		} else if (!indexName.equals(other.indexName))
			return false;
		if (pageNumber != other.pageNumber)
			return false;
		if (pageSize != other.pageSize)
			return false;
		if (searches == null) {
			if (other.searches != null)
				return false;
		} else if (!searches.equals(other.searches))
			return false;
		if (sorting == null) {
			if (other.sorting != null)
				return false;
		} else if (!sorting.equals(other.sorting))
			return false;
		return true;
	}

	/**
	 * 
	 * @param indexName
	 * @param defaultField
	 * @param queryString
	 * @return A JsonSearchRequest use only one clause
	 */
	public static JsonSearchRequest createRequest(String indexName, String defaultField, String queryString) {
		return new JsonSearchRequest(indexName, defaultField, queryString);
	}

}
