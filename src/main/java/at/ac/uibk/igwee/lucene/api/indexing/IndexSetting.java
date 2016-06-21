package at.ac.uibk.igwee.lucene.api.indexing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An value class, containing the setting for a whole index.
 * <p>
 * In a given system, there should be different index for different databases.
 * @author Joseph
 *
 */
public final class IndexSetting implements java.io.Serializable {
	
	private static final long serialVersionUID = 201310211021L;
	/**
	 * indexname
	 */
	private String indexName;
	/**
	 * setting for the default analyzer. should actually never be used, since
	 * every field is mapped to an analyzer.
	 */
	private AnalyzerSetting defaultAnalyzerSetting;
	
	/**
	 * List of AnalyzerSetting.
	 */
	private List<AnalyzerSetting> analyzerSettings = new ArrayList<>();
	
	/**
	 * List of DecoratorSetting
	 */
	private List<FieldSetting> fieldSettings = new ArrayList<>();

	private List<FacetSetting> facetSettings = new ArrayList<>();
		
	public IndexSetting() {
		super();
	}
	
	private Object readResolve() {
		if (analyzerSettings==null) analyzerSettings = new ArrayList<AnalyzerSetting>();
		if (fieldSettings==null) fieldSettings = new ArrayList<FieldSetting>();
		return this;
	}
	
	@Override
	public String toString() {
		return "IndexSetting [indexName=" + indexName
				+ ", defaultAnalyzerSetting=" + defaultAnalyzerSetting
				+ ", analyzerSettings=" + analyzerSettings + ", fieldSettings="
				+ fieldSettings + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((indexName == null) ? 0 : indexName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexSetting other = (IndexSetting) obj;
		if (indexName == null) {
			if (other.indexName != null)
				return false;
		} else if (!indexName.equals(other.indexName))
			return false;
		return true;
	}

	/**
	 * 
	 * @return indexname
	 */
	public String getIndexName() {
		return indexName;
	}

	/**
	 * 
	 * @param indexName
	 */
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	/**
	 * 
	 * @return defaultAnalyzerSetting
	 */
	public AnalyzerSetting getDefaultAnalyzerSetting() {
		return defaultAnalyzerSetting;
	}

	/**
	 * 
	 * @param defaultAnalyzerSetting
	 */
	public void setDefaultAnalyzerSetting(AnalyzerSetting defaultAnalyzerSetting) {
		this.defaultAnalyzerSetting = defaultAnalyzerSetting;
	}

	/**
	 * 
	 * @return analyzerSettings
	 */
	public List<AnalyzerSetting> getAnalyzerSettings() {
		return analyzerSettings;
	}
	
	/**
	 * 
	 * @param analyzerName
	 * @return the AnalyzerSetting with the name, or null, if none can be found
	 */
	public AnalyzerSetting getAnalyzerSetting(String analyzerName) {
		if (analyzerName==null) return null;
		for (AnalyzerSetting as: this.analyzerSettings)
			if (analyzerName.equals(as.getAnalyzerName())) return as;
		return null;
	}

	/**
	 * 
	 * @param analyzerSettings
	 */
	public void setAnalyzerSettings(List<AnalyzerSetting> analyzerSettings) {
		nonNullAnalyzerSettings();
		this.analyzerSettings.clear();
		this.analyzerSettings.addAll(analyzerSettings);
	}
	
	public void addAnalyzerSetting(AnalyzerSetting as) {
		nonNullAnalyzerSettings();
		if (this.analyzerSettings.contains(as))
			this.analyzerSettings.remove(as);
		analyzerSettings.add(as);
	}
	
	public void removeAnalyzerSetting(String name) {
		AnalyzerSetting toRemove = null;
		for (AnalyzerSetting now: this.analyzerSettings)
			if (now.getAnalyzerName().equals(name)) {
				toRemove = now;
				break;
			}
		if (toRemove!=null) 
			removeAnalyzerSetting(toRemove);
	}
	
	public void removeAnalyzerSetting(AnalyzerSetting as) {
		nonNullAnalyzerSettings();
		this.analyzerSettings.remove(as);
	}
	
	private void nonNullAnalyzerSettings() {
		if (this.analyzerSettings==null) this.analyzerSettings = new ArrayList<AnalyzerSetting>();
	}

	/**
	 * 
	 * @return list of DecoratorSetting
	 */
	public List<FieldSetting> getFieldSettings() {
		return fieldSettings;
	}

	/**
	 * 
	 * @param fieldSettings
	 */
	public void setFieldSettings(List<FieldSetting> fieldSettings) {
		nonNullFieldSettings();
		this.fieldSettings.clear();
		this.fieldSettings.addAll(fieldSettings);
	}
	
	public void addFieldSetting(FieldSetting fs) {
		nonNullFieldSettings();
		if (this.fieldSettings.contains(fs))
			this.fieldSettings.remove(fs);
		this.fieldSettings.add(fs);
	}
	
	public void removeFieldSetting(FieldSetting fs) {
		nonNullFieldSettings();
		this.fieldSettings.remove(fs);
	}
	
	public void removeFieldSetting(String fieldname) {
		FieldSetting toRem = null;
		for (FieldSetting now: this.fieldSettings)
			if (now.getFieldname().equals(fieldname)) {
				toRem = now;
				break;
			}
		if (toRem!=null) removeFieldSetting(toRem);
	}

    public List<FacetSetting> getFacetSettings() {
        return facetSettings;
    }

    public void setFacetSettings(List<FacetSetting> facetSettings) {
        this.facetSettings = facetSettings;
    }
    public void addFacetSetting(FacetSetting fs) {
        if (this.facetSettings.contains(fs)) {
            this.facetSettings.remove(fs);
        }
        this.facetSettings.add(fs);
    }
    public void removeFacetSetting(String dimName) {
        Optional<FacetSetting> fs = facetSettings.stream().filter(f -> f.getDim().equals(dimName)).findAny();
        if (fs.isPresent())
            facetSettings.remove(fs.get());
    }

    private void nonNullFieldSettings() {
		if (this.fieldSettings==null) this.fieldSettings = new ArrayList<FieldSetting>();
	}
	
	/**
	 * 
	 * @param fieldname
	 * @return the DecoratorSetting with the given name. null if not found.
	 */
	public FieldSetting getFieldSetting(String fieldname) {
		for (FieldSetting fs: fieldSettings)
			if (fs.getFieldname().equals(fieldname)) return fs;
		return null;
	}
	

}
