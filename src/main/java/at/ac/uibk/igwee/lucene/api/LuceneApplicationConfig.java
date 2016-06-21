package at.ac.uibk.igwee.lucene.api;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import at.ac.uibk.igwee.lucene.api.indexing.IndexSetting;
import at.ac.uibk.igwee.lucene.api.searching.SearchSetting;
import at.ac.uibk.igwee.lucene.api.viewing.ViewSetting;
/**
 * Value class for all configurations needed for an application.
 * @author Joseph
 *
 */
public class LuceneApplicationConfig implements java.io.Serializable {
	
	private static final long serialVersionUID = 201310271925L;
	
	private static final String CONFIGURATION_DIR = ".config";
	
	/**
	 * BaseDir for all operations.
	 */
	private String indexBaseDir;
	/**
	 * A set of indexSettings.
	 */
	private Set<IndexSetting> indexSettings = new HashSet<IndexSetting>();
	
	private Set<SearchSetting> searchSettings = new HashSet<SearchSetting>();
	
	private Set<ViewSetting> viewSettings = new HashSet<ViewSetting>();
	
	public LuceneApplicationConfig() {
		super();
	}
	
	/**
	 * 
	 * @return a Set of String containing names of indexes.
	 */
	public Set<String> getAllIndexNames() {
		Set<String> result = new LinkedHashSet<>();
		for (IndexSetting is: indexSettings)
			result.add(is.getIndexName());
		for (SearchSetting ss: searchSettings)
			result.add(ss.getIndexName());
		// TODO add indexNames from all other configurations
		return result;
	}

	/**
	 * 
	 * @return indexBaseDir
	 */
	public String getIndexBaseDir() {
		return indexBaseDir;
	}

	/**
	 * Sets the indexBaseDir. The File(indexBaseDir) is the base
	 * containing all indexes.
	 * @param indexBaseDir
	 */
	public void setIndexBaseDir(String indexBaseDir) {
		this.indexBaseDir = indexBaseDir;
	}

	/**
	 * 
	 * @return All IndexSettings
	 */
	public Set<IndexSetting> getIndexSettings() {
		return indexSettings;
	}

	/**
	 * Sets the IndexSettings
	 * @param indexSettings
	 */
	public void setIndexSettings(Set<IndexSetting> indexSettings) {
		this.indexSettings = indexSettings;
	}
	
	/**
	 * Adds an IndexSetting
	 * @param is
	 */
	public void addIndexSetting(IndexSetting is) {
		indexSettings.add(is);
	}
	
	
	
	/**
	 * @return the searchSettings
	 */
	public Set<SearchSetting> getSearchSettings() {
		return searchSettings;
	}

	/**
	 * @param searchSettings the searchSettings to set
	 */
	public void setSearchSettings(Set<SearchSetting> searchSettings) {
		this.searchSettings = searchSettings;
	}
	
	public void addSearchSetting(SearchSetting ss) {
		if (this.searchSettings.contains(ss)) this.searchSettings.remove(ss);
		this.searchSettings.add(ss);
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
	
	public void addViewSetting(ViewSetting viewSetting) {
		if (this.viewSettings.contains(viewSetting))
			viewSettings.remove(viewSetting);
		viewSettings.add(viewSetting);
	}

	/**
	 * 
	 * @return the directory containing all configuration files.
	 */
	public String getConfigurationDir() {
		return indexBaseDir + (indexBaseDir.endsWith("/") ? "" : "/") + CONFIGURATION_DIR;
	}

	
	
}
