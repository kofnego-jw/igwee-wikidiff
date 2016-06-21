package at.ac.uibk.igwee.lucene.api.searching;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models the result of a search.
 * @author Joseph
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class SearchResult {
	
	/**
	 * The search request used for creating this result.
	 */
	private SearchRequest request;
	
	/**
	 * list of documents
	 */
	private List<ResultDocument> resultDocuments;
	
	/**
	 * total hits, could be 0
	 */
	private int totalHits;
	
	/**
	 * Facet results
	 */
	private List<FacetSearchResult> facetResults;

    public SearchResult() {
        this.facetResults = new ArrayList<>();
        this.resultDocuments = new ArrayList<>();
    }
	
	public SearchResult(SearchRequest req, List<ResultDocument> resDocs, int totalHits, List<FacetSearchResult> facetResults) {
		this.request = req;
		this.resultDocuments = resDocs;
		this.totalHits = totalHits;
		this.facetResults = facetResults;
	}
	
	public SearchResult(SearchRequest request, List<ResultDocument> resultDocuments, int totalHits) {
		this(request, resultDocuments, totalHits, new ArrayList<FacetSearchResult>());
	}
	

	/**
	 * @return the resultDocuments
	 */
	public List<ResultDocument> getResultDocuments() {
		return resultDocuments;
	}
	
	public void addResultDocument(ResultDocument doc) {
		this.resultDocuments.add(doc);
	}

	/**
	 * @return the totalHits
	 */
	public int getTotalHits() {
		return totalHits;
	}

	/**
	 * @return the request
	 */
	public SearchRequest getRequest() {
		return request;
	}

	/**
	 * @return the facetResults
	 */
	public List<FacetSearchResult> getFacetResults() {
		return facetResults;
	}
	
	public void setFacetResults(List<FacetSearchResult> list) {
		this.facetResults.clear();
		this.facetResults.addAll(list);
	}
	
	public void addFacetResults(List<FacetSearchResult> results) {
		this.facetResults.addAll(results);
	}
	
	public void addFacetResult(FacetSearchResult facetRes) {
		this.facetResults.add(facetRes);
	}

    public void setRequest(SearchRequest request) {
        this.request = request;
    }

    public void setResultDocuments(List<ResultDocument> resultDocuments) {
        this.resultDocuments = resultDocuments;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    private Object readResolve() {
        if (this.resultDocuments==null)
            this.resultDocuments = new ArrayList<>();
        if (this.facetResults==null)
            this.facetResults = new ArrayList<>();
        return this;
    }

    @Override
	public String toString() {
		return "SearchResult{" +
				"request=" + request +
				", resultDocuments=" + resultDocuments +
				", totalHits=" + totalHits +
				", facetResults=" + facetResults +
				'}';
	}
}
