package at.ac.uibk.igwee.lucene.api.viewing;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import at.ac.uibk.igwee.lucene.api.Constants;

public class ViewSetting implements java.io.Serializable {
	
	private static final long serialVersionUID = 201312121614L;
	
	private String indexName;
	
	private Set<DecoratorSetting> decoratorSettings = new HashSet<>();
	
	private Set<FacetTranslation> facetTranslations = new HashSet<>();
	
	private Locale defaultLocale;
	
	public ViewSetting() {
		super();
	}
	
	public ViewSetting(String indexName) {
		this();
		this.indexName = indexName;
	}
	
	public ViewSetting(String indexName, Set<DecoratorSetting> decoratorSettings) {
		this(indexName);
		this.decoratorSettings = decoratorSettings;
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
	 * @return the decoratorSettings
	 */
	public Set<DecoratorSetting> getDecoratorSettings() {
		return decoratorSettings;
	}

	/**
	 * @param decoratorSettings the decoratorSettings to set
	 */
	public void setDecoratorSettings(Set<DecoratorSetting> decoratorSettings) {
		this.decoratorSettings = decoratorSettings;
	}
	
	public void addDecoratorSetting(DecoratorSetting ds) {
		if (this.decoratorSettings.contains(ds)) 
			removeDecoratorSetting(ds);
		this.decoratorSettings.add(ds);
	}
	
	public void removeDecoratorSetting(DecoratorSetting ds) {
		this.decoratorSettings.remove(ds);
	}

	/**
	 * @return the facetTranslations
	 */
	public Set<FacetTranslation> getFacetTranslations() {
		return facetTranslations;
	}

	/**
	 * @param facetTranslations the facetTranslations to set
	 */
	public void setFacetTranslations(Set<FacetTranslation> facetTranslations) {
		this.facetTranslations = facetTranslations;
	}
	
	public void addFacetTranslation(FacetTranslation ft) {
		this.facetTranslations.add(ft);
	}
	
	public void removeFacetTranslation(FacetTranslation ft) {
		this.facetTranslations.remove(ft);
	}
	
	

	/**
	 * @return the defaultLocale
	 */
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * @param defaultLocale the defaultLocale to set
	 */
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
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
		ViewSetting other = (ViewSetting) obj;
		if (indexName == null) {
			if (other.indexName != null)
				return false;
		} else if (!indexName.equals(other.indexName))
			return false;
		return true;
	}
	
	private Object readResolve() {
		if (this.decoratorSettings==null) this.decoratorSettings = 
				new HashSet<DecoratorSetting>();
		if (this.facetTranslations==null) this.facetTranslations = new HashSet<FacetTranslation>();
		if (this.defaultLocale==null) this.defaultLocale = Constants.DEFAULT_LOCALE;
		return this;
	}

	public void removeDecoratorSetting(String fn) {
		DecoratorSetting ds = null;
		for (DecoratorSetting now: this.decoratorSettings)
			if (now.getFieldname().equals(fn)) {
				ds = now;
				break;
			}
		if (ds!=null)
			removeDecoratorSetting(ds);
	}
	

}
