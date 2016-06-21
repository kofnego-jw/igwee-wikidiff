package at.ac.uibk.igwee.lucene.rest.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models the search request flyweight. Should be used between
 * a web client (browser side javascript) and a sling servlet.
 *
 * This class
 *
 * Created by joseph on 5/23/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequestFW {

    public int pageNumber;

    public int pageSize;

    public List<QuerySettingFW> querySettings = new ArrayList<>();

    public List<SortSettingFW> sortSettings = new ArrayList<>();

    public List<FilterSettingFW> filterSettings = new ArrayList<>();

    public int filterChainingMode;

    public List<FacetFW> facets = new ArrayList<>();

    public SearchRequestFW() {

    }

    public SearchRequestFW(int pageNumber, int pageSize, List<QuerySettingFW> querySettings, List<SortSettingFW> sortSettings, List<FilterSettingFW> filterSettings, int filterChainingMode, List<FacetFW> facets) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.querySettings = querySettings;
        this.sortSettings = sortSettings;
        this.filterSettings = filterSettings;
        this.filterChainingMode = filterChainingMode;
        this.facets = facets;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<QuerySettingFW> getQuerySettings() {
        return querySettings;
    }

    public void setQuerySettings(List<QuerySettingFW> querySettings) {
        this.querySettings = querySettings;
    }

    public List<SortSettingFW> getSortSettings() {
        return sortSettings;
    }

    public void setSortSettings(List<SortSettingFW> sortSettings) {
        this.sortSettings = sortSettings;
    }

    public List<FilterSettingFW> getFilterSettings() {
        return filterSettings;
    }

    public void setFilterSettings(List<FilterSettingFW> filterSettings) {
        this.filterSettings = filterSettings;
    }

    public int getFilterChainingMode() {
        return filterChainingMode;
    }

    public void setFilterChainingMode(int filterChainingMode) {
        this.filterChainingMode = filterChainingMode;
    }

    public List<FacetFW> getFacets() {
        return facets;
    }

    public void setFacets(List<FacetFW> facets) {
        this.facets = facets;
    }

    @Override
    public String toString() {
        return "SearchRequestFW{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", querySettings=" + querySettings +
                ", sortSettings=" + sortSettings +
                ", filterSettings=" + filterSettings +
                ", filterChainingMode=" + filterChainingMode +
                ", facets=" + facets +
                '}';
    }
}
