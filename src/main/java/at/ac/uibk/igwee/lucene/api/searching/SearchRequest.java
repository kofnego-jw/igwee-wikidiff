package at.ac.uibk.igwee.lucene.api.searching;

import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.LinkedList;
import java.util.List;

/**
 * Value object, carrying all the parameters needed for a search request.
 * @author Joseph
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class SearchRequest {

    /**
     * Default page size 20
     */
	public static final int DEFAULT_PAGESIZE = 20;

    /**
     * Default max facet list size = 10
     */
    public static final int DEFAULT_MAX_FACETLISTSIZE = 10;
	
	/**
	 * Name of the index queried
	 */
	private String indexName;
	
	/**
	 * List of query setting.
	 */
	private List<QuerySetting> querySettings = new LinkedList<>();
	/**
	 * Sorting setting
	 */
	private List<SortSetting> sortRules = new LinkedList<>();
	/**
	 * Filter settings.
	 */
	private List<FilterSetting> filterSettings = new LinkedList<>();
	/**
	 * Mode of Filter Chaining
	 */
	private FilterChainingMode filterChainingMode = FilterChainingMode.OR;
	
	/**
	 * List of facetSetting used for drill down facets. Can be empty.
	 */
	private List<FacetContent> drillDownFacets = new LinkedList<>();

	/**
	 * Page number, starting with 0.
	 */
	private int pageNumber = 0;
	/**
	 * Page size, should be at least 1. Default 20.
	 */
	private int pageSize = DEFAULT_PAGESIZE;
	
	private int maxFacetListSize = DEFAULT_MAX_FACETLISTSIZE;

	public SearchRequest() {

    }
	
	public SearchRequest(String indexname) {
		super();
		setIndexName(indexname);
	}
	
	public SearchRequest(String indexName, String fieldname, String qs) {
		this(indexName);
		QuerySetting q = new QuerySetting(fieldname, qs);
		addQuerySetting(q);
	}
	
	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return indexName;
	}
	/**
	 * @param indexName the indexName to set
	 */
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	/**
	 * @return the querySettings
	 */
	public List<QuerySetting> getQuerySettings() {
		return querySettings;
	}
	/**
	 * @param querySettings the querySettings to set
	 */
	public void setQuerySettings(List<QuerySetting> querySettings) {
		this.querySettings = querySettings;
	}
	
	public void addQuerySetting(QuerySetting q) {
		this.querySettings.add(q);
	}
	
	public void addQuerySetting(String fieldname, String qs) {
		this.querySettings.add(new QuerySetting(fieldname, qs));
	}
	
	/**
	 * @return the sortRules
	 */
	public List<SortSetting> getSortRules() {
		return sortRules;
	}
	/**
	 * @param sortRules the sortRules to set
	 */
	public void setSortRules(List<SortSetting> sortRules) {
		this.sortRules = sortRules;
	}
	/**
	 * @return the filterSettings
	 */
	public List<FilterSetting> getFilterSettings() {
		return filterSettings;
	}
	/**
	 * @param filterSettings the filterSettings to set
	 */
	public void setFilterSettings(List<FilterSetting> filterSettings) {
		this.filterSettings = filterSettings;
	}
	/**
	 * @return the drillDownFacets
	 */
	public List<FacetContent> getDrillDownFacets() {
		return drillDownFacets;
	}

	/**
	 * @param drillDownFacets the drillDownFacets to set
	 */
	public void setDrillDownFacets(List<FacetContent> drillDownFacets) {
		this.drillDownFacets = drillDownFacets;
	}
	/**
	 * Adds a pointer and removes its parent. Will do nothing if the pointer is root.
	 * @param pointer
	 */
	public void addDrillDownFacet(FacetContent pointer) {
		if (pointer.getComponents().size()<=1) return;
		removeDrillDownFacet(pointer.getParentNode());
		this.drillDownFacets.add(pointer);
	}
	/**
	 * Removes a pointer. Will do nothing if the pointer is root.
	 * @param pointer
	 */
	public void removeDrillDownFacet(FacetContent pointer) {
		if (pointer.isRoot()) return;
		List<FacetContent> toRem = new LinkedList<>();
		for (FacetContent fp: drillDownFacets) {
			if (fp.equals(pointer)) toRem.add(fp);
		}
		
		for (FacetContent fp: toRem)
			drillDownFacets.remove(fp);
		
	}

	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}
	/**
	 * @param pageNumber the pageNumber to set
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the filterChainingMode
	 */
	public FilterChainingMode getFilterChainingMode() {
		return filterChainingMode;
	}

	/**
	 * @param filterChainingMode the filterChainingMode to set
	 */
	public void setFilterChainingMode(FilterChainingMode filterChainingMode) {
		this.filterChainingMode = filterChainingMode;
	}

	/**
	 * @return the maxFacetListSize
	 */
	public int getMaxFacetListSize() {
		return maxFacetListSize;
	}

	/**
	 * @param maxFacetListSize the maxFacetListSize to set
	 */
	public void setMaxFacetListSize(int maxFacetListSize) {
		this.maxFacetListSize = maxFacetListSize;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SearchRequest that = (SearchRequest) o;

		if (pageNumber != that.pageNumber) return false;
		if (pageSize != that.pageSize) return false;
		if (maxFacetListSize != that.maxFacetListSize) return false;
		if (indexName != null ? !indexName.equals(that.indexName) : that.indexName != null) return false;
		if (querySettings != null ? !querySettings.equals(that.querySettings) : that.querySettings != null)
			return false;
		if (sortRules != null ? !sortRules.equals(that.sortRules) : that.sortRules != null) return false;
		if (filterSettings != null ? !filterSettings.equals(that.filterSettings) : that.filterSettings != null)
			return false;
		if (filterChainingMode != that.filterChainingMode) return false;
		return drillDownFacets != null ? drillDownFacets.equals(that.drillDownFacets) : that.drillDownFacets == null;

	}

	@Override
	public int hashCode() {
		int result = indexName != null ? indexName.hashCode() : 0;
		result = 31 * result + (querySettings != null ? querySettings.hashCode() : 0);
		result = 31 * result + (sortRules != null ? sortRules.hashCode() : 0);
		result = 31 * result + (filterSettings != null ? filterSettings.hashCode() : 0);
		result = 31 * result + (filterChainingMode != null ? filterChainingMode.hashCode() : 0);
		result = 31 * result + (drillDownFacets != null ? drillDownFacets.hashCode() : 0);
		result = 31 * result + pageNumber;
		result = 31 * result + pageSize;
		result = 31 * result + maxFacetListSize;
		return result;
	}

	@Override
	public String toString() {
		return "SearchRequest{" +
				"indexName='" + indexName + '\'' +
				", querySettings=" + querySettings +
				", sortRules=" + sortRules +
				", filterSettings=" + filterSettings +
				", filterChainingMode=" + filterChainingMode +
				", drillDownFacets=" + drillDownFacets +
				", pageNumber=" + pageNumber +
				", pageSize=" + pageSize +
				", maxFacetListSize=" + maxFacetListSize +
				'}';
	}
}
