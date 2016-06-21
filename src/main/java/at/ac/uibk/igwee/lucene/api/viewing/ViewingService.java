package at.ac.uibk.igwee.lucene.api.viewing;

import at.ac.uibk.igwee.lucene.api.searching.SearchResult;

public interface ViewingService {
	
	public ViewResult decorate(SearchResult searchResult);

}
