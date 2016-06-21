package at.ac.uibk.igwee.lucene.api.viewing;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class models the setting for a view
 * @author Joseph
 *
 */
public final class DecoratorSetting implements java.io.Serializable {
	
	private static final long serialVersionUID = 201312111217L;
	
	/**
	 * Name of a field
	 */
	private String fieldname;
	
	/**
	 * 
	 */
	private String readableName;
	
	private Map<Locale,String> readableNames = new LinkedHashMap<>();
	
	private String description;
	
	private Map<Locale, String> descriptions = new LinkedHashMap<>();
	
	private boolean partOfAbstract = false;
	
	public DecoratorSetting() {
		super();
	}
	
	public DecoratorSetting(String fieldname, String readableName) {
		this();
		this.fieldname = fieldname;
		this.readableName = readableName;
	}

	public DecoratorSetting(String fieldname, String readableName, String description) {
        this(fieldname, readableName);
        this.description = description;
    }

	public String getReadableName(Locale locale) {
		
		return getTranslation(locale, readableNames, readableName);
	}
	
	public String getDescription(Locale locale) {
		return getTranslation(locale, descriptions, description);
	}
	
	public void addReadableName(Locale locale, String translation) {
		nonNullReadableNames();
		this.readableNames.put(locale, translation);
	}
	
	public void removeReadableName(Locale locale) {
		nonNullReadableNames();
		this.readableNames.remove(locale);
	}
	
	public void addDescription(Locale loc, String desc) {
		nonNullDescriptions();
		this.descriptions.put(loc, desc);
	}
	
	public void removeDescription(Locale loc) {
		nonNullDescriptions();
		this.descriptions.remove(loc);
	}
	
	private void nonNullDescriptions() {
		nonNull(descriptions);
	}
	
	private void nonNullReadableNames() {
		nonNull(readableNames);
	}
	
	private void nonNull(Map<Locale,String> map) {
		if (map==null) map = new HashMap<Locale, String>();
	}
	
	
	private static String getTranslation(Locale lang, Map<Locale,String> translations, String defaultText) {
		if (lang==null) lang = Locale.getDefault();
		if (defaultText==null) defaultText= "";
		if (translations==null || translations.isEmpty()) return defaultText;
		String translation = translations.get(lang);
		if (translation!=null) return translation;
		String language = lang.getLanguage();
		for (Map.Entry<Locale, String> now: translations.entrySet()) {
			if (now.getKey().getLanguage().equals(language))
				return now.getValue();
		}
		return defaultText;
	}
	
	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * @param fieldname the fieldname to set
	 */
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	/**
	 * @return the readableName
	 */
	public String getReadableName() {
		return readableName;
	}

	/**
	 * @param readableName the readableName to set
	 */
	public void setReadableName(String readableName) {
		this.readableName = readableName;
	}

	/**
	 * @return the readableNames
	 */
	public Map<Locale, String> getReadableNames() {
		return readableNames;
	}

	/**
	 * @param readableNames the readableNames to set
	 */
	public void setReadableNames(Map<Locale, String> readableNames) {
		this.readableNames = readableNames;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the descriptions
	 */
	public Map<Locale, String> getDescriptions() {
		return descriptions;
	}

	/**
	 * @param descriptions the descriptions to set
	 */
	public void setDescriptions(Map<Locale, String> descriptions) {
		this.descriptions = descriptions;
	}

	/**
	 * @return the partOfAbstract
	 */
	public boolean isPartOfAbstract() {
		return partOfAbstract;
	}

	/**
	 * @param partOfAbstract the partOfAbstract to set
	 */
	public void setPartOfAbstract(boolean partOfAbstract) {
		this.partOfAbstract = partOfAbstract;
	}
	
	private Object readResolve() {
		if (this.descriptions==null) 
			this.descriptions = new LinkedHashMap<Locale,String>();
		if (this.readableNames==null) 
			this.readableNames = new LinkedHashMap<Locale,String>();
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fieldname == null) ? 0 : fieldname.hashCode());
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
		DecoratorSetting other = (DecoratorSetting) obj;
		if (fieldname == null) {
			if (other.fieldname != null)
				return false;
		} else if (!fieldname.equals(other.fieldname))
			return false;
		return true;
	}
	
	
	

}
