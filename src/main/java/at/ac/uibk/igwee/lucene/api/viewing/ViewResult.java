package at.ac.uibk.igwee.lucene.api.viewing;

import at.ac.uibk.igwee.lucene.api.searching.FacetSearchResult;
import at.ac.uibk.igwee.lucene.api.searching.ResultDocument;
import at.ac.uibk.igwee.lucene.api.searching.SearchResult;

import java.util.List;
import java.util.Locale;

public final class ViewResult {
	
	private final SearchResult searchResult;

	private final Locale locale;

	private final List<ViewDocument> translatedFullDocuments;

	private final List<FacetSearchResult> translatedFacetSearchResults;

	public ViewResult(SearchResult searchResult,
					  Locale locale,
					  List<ViewDocument> translatedFullDocuments,
					  List<FacetSearchResult> translatedFacetSearchResults) {
		this.searchResult = searchResult;
		this.locale = locale;
		this.translatedFullDocuments = translatedFullDocuments;
		this.translatedFacetSearchResults = translatedFacetSearchResults;
	}

	public int getTotalHits() {
		return searchResult.getTotalHits();
	}

	public SearchResult getSearchResult() {
		return searchResult;
	}

	public Locale getLocale() {
		return locale;
	}

	public List<ViewDocument> getTranslatedFullDocuments() {
		return translatedFullDocuments;
	}

	public List<FacetSearchResult> getTranslatedFacetSearchResults() {
		return translatedFacetSearchResults;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ViewResult that = (ViewResult) o;

		if (searchResult != null ? !searchResult.equals(that.searchResult) : that.searchResult != null) return false;
		return !(locale != null ? !locale.equals(that.locale) : that.locale != null);

	}

	@Override
	public int hashCode() {
		int result = searchResult != null ? searchResult.hashCode() : 0;
		result = 31 * result + (locale != null ? locale.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ViewResult{" +
				"searchResult=" + searchResult +
				", locale=" + locale +
				", translatedFullDocuments=" + translatedFullDocuments +
				", translatedFacetSearchResults=" + translatedFacetSearchResults +
				'}';
	}
}
