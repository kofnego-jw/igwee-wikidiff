package at.ac.uibk.igwee.lucene.rest.helper;

import java.util.List;

/**
 * Created by joseph on 5/25/16.
 */
public class SearchResultFW {

    public SearchRequestFW searchRequest;

    public int totalHits;

    public List<FacetSearchResultFW> facetSearchResults;

    public List<ResultDocumentFW> resultDocuments;

    public SearchResultFW() {

    }

    public SearchResultFW(SearchRequestFW searchRequest, int totalHits, List<FacetSearchResultFW> facetSearchResults, List<ResultDocumentFW> resultDocuments) {
        this.searchRequest = searchRequest;
        this.totalHits = totalHits;
        this.facetSearchResults = facetSearchResults;
        this.resultDocuments = resultDocuments;
    }

    public SearchRequestFW getSearchRequest() {
        return searchRequest;
    }

    public void setSearchRequest(SearchRequestFW searchRequest) {
        this.searchRequest = searchRequest;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public List<FacetSearchResultFW> getFacetSearchResults() {
        return facetSearchResults;
    }

    public void setFacetSearchResults(List<FacetSearchResultFW> facetSearchResults) {
        this.facetSearchResults = facetSearchResults;
    }

    public List<ResultDocumentFW> getResultDocuments() {
        return resultDocuments;
    }

    public void setResultDocuments(List<ResultDocumentFW> resultDocuments) {
        this.resultDocuments = resultDocuments;
    }

    @Override
    public String toString() {
        return "SearchResultFW{" +
                "searchRequest=" + searchRequest +
                ", totalHits=" + totalHits +
                ", facetSearchResults=" + facetSearchResults +
                ", resultDocuments=" + resultDocuments +
                '}';
    }
}
