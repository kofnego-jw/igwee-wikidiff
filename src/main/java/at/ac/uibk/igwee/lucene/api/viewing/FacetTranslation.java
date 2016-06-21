package at.ac.uibk.igwee.lucene.api.viewing;

import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FacetTranslation implements java.io.Serializable {
	
	private static final long serialVersionUID = 201401201017L;
	
	private FacetContent indexedFacet;
	
	private FacetContent defaultReadableFacet;
	
	private Map<Locale, FacetContent> translations = new HashMap<Locale, FacetContent>();
	
	public FacetTranslation() {
		super();
	}
	
	public FacetTranslation(FacetContent base, FacetContent defaultTranslation) {
		this();
		this.indexedFacet = base;
		this.defaultReadableFacet = defaultTranslation;
	}
	
	public FacetTranslation(FacetContent base, FacetContent defaultTranslation, Map<Locale, FacetContent> translations) {
		this(base, defaultTranslation);
		this.translations = translations;
	}
	
	/**
	 * 
	 * @param indexedFp
	 * @return true if the indexedFacetPointer is the same as the argument.
	 */
	public boolean isTranslationOf(FacetContent indexedFp) {
		return this.indexedFacet.equals(indexedFp);
	}
	
	/**
	 * 
	 * @param indexedFp
	 * @return true if the indexFacetPointer is parent of the argument
	 */
	public boolean isParentOf(FacetContent indexedFp) {
		return this.indexedFacet.isParentOf(indexedFp);
	}
	
	/**
	 * 
	 * @param indexedFp
	 * @return true if this is the ancestor of argument, also true if it is the parent of argument. 
	 */
	public boolean isAncestorOf(FacetContent indexedFp) {
		return this.indexedFacet.isAncestorOf(indexedFp);
	}
	
	public FacetContent getTranslation() {
		return this.defaultReadableFacet;
	}
	
	public FacetContent getTranslation(Locale locale) {
		FacetContent result = this.translations.get(locale);
		return (result==null) ? this.defaultReadableFacet : result;
	}

	/**
	 * @return the indexedFacet
	 */
	public FacetContent getIndexedFacet() {
		return indexedFacet;
	}

	/**
	 * @param indexedFacet the indexedFacet to set
	 */
	public void setIndexedFacet(FacetContent indexedFacet) {
		this.indexedFacet = indexedFacet;
	}

	/**
	 * @return the defaultReadableFacet
	 */
	public FacetContent getDefaultReadableFacet() {
		return defaultReadableFacet;
	}

	/**
	 * @param defaultReadableFacet the defaultReadableFacet to set
	 */
	public void setDefaultReadableFacet(FacetContent defaultReadableFacet) {
		this.defaultReadableFacet = defaultReadableFacet;
	}

	/**
	 * @return the translations
	 */
	public Map<Locale, FacetContent> getTranslations() {
		return translations;
	}

	/**
	 * @param translations the translations to set
	 */
	public void setTranslations(Map<Locale, FacetContent> translations) {
		this.translations = translations;
	}
	
	public void addTranslation(Locale locale, FacetContent translation) {
		this.translations.put(locale, translation);
	}
	
	public FacetContent removeTranslation(Locale locale) {
		return this.translations.remove(locale);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indexedFacet == null) ? 0 : indexedFacet.hashCode());
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
		FacetTranslation other = (FacetTranslation) obj;
		if (indexedFacet == null) {
			if (other.indexedFacet != null)
				return false;
		} else if (!indexedFacet.equals(other.indexedFacet))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FacetTranslation [indexedFacet=" + indexedFacet
				+ ", defaultReadableFacet=" + defaultReadableFacet
				+ ", translations=" + translations + "]";
	}
	
	

}
