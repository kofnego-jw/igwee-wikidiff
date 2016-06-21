package at.ac.uibk.igwee.lucene.impl.viewing;

import java.util.HashSet;
import java.util.Set;

import at.ac.uibk.igwee.lucene.api.Constants;
import at.ac.uibk.igwee.lucene.api.searching.SearchResult;
import at.ac.uibk.igwee.lucene.api.viewing.*;
import at.ac.uibk.igwee.lucene.impl.searching.SearchResultFactory;

public class ViewingServiceImpl implements ViewingService {

	private Set<ViewSetting> viewSettings;
	
	public ViewingServiceImpl() {
		super();
	}
	
	public ViewingServiceImpl(Set<ViewSetting> settings) {
		this();
		this.viewSettings = settings;
	}
	
	
	
	@Override
	public ViewResult decorate(SearchResult searchResult) {
		
		ViewSetting vs = getViewSetting(searchResult.getRequest().getIndexName());
		if (vs==null) 
			return ViewResultFactory.createViewResult(searchResult, Constants.DEFAULT_LOCALE, new ViewSetting());
		return ViewResultFactory.createViewResult(searchResult, vs.getDefaultLocale(), vs);
	}

	/**
	 * @return the viewSettings
	 */
	public Set<ViewSetting> getViewSettings() {
		return viewSettings;
	}

	/**
	 * @param viewSettings the viewSettings to set
	 */
	public void setViewSettings(Set<ViewSetting> viewSettings) {
		this.viewSettings = viewSettings;
	}
	
	public ViewSetting getViewSetting(String indexName) {
		for (ViewSetting vs: viewSettings)
			if (vs.getIndexName().equals(indexName)) return vs;
		return null;
	}
	
	public Set<DecoratorSetting> getDecoratorSettings(String indexName) {
		ViewSetting vs = getViewSetting(indexName);
		if (vs!=null) return vs.getDecoratorSettings();
		return new HashSet<DecoratorSetting>();
	}
	
	public Set<FacetTranslation> getFacetTranslations(String indexName) {
		ViewSetting vs = getViewSetting(indexName);
		if (vs!=null) return vs.getFacetTranslations();
		return new HashSet<FacetTranslation>();
	}

	

}
