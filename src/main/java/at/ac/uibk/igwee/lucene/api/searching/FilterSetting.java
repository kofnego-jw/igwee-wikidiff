package at.ac.uibk.igwee.lucene.api.searching;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class models the possible filter used for filter the search results.
 * 
 * @author Joseph
 * 
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterSetting {

	/**
	 * Type of the filter
	 */
	private FilterType type;

	/**
	 * Name of the field
	 */
	private String fieldname;
	
	/**
	 * Lower bound or querystring.
	 */
	private String val1;
	/**
	 * Higher bound or querystring.
	 */
	private String val2;
	/**
	 * include the lower bound?
	 */
	private boolean includeLow;
	/**
	 * include the higher bound?
	 */
	private boolean includeHigh;
	
	public FilterSetting() {
		super();
	}
	
	public FilterSetting(FilterType type, String fieldname, String val1, String val2, boolean include1, boolean include2) {
		this();
		this.type = type;
		this.fieldname = fieldname;
		this.val1 = val1;
		this.val2 = val2;
		this.includeLow = include1;
		this.includeHigh = include2;
	}
	
	public FilterSetting(String fieldname, String val1) {
		this(FilterType.EXACT, fieldname, val1, null, true, true);
	}
	
	public FilterSetting(String fieldname, String val1, String val2) {
		this(FilterType.RANGE, fieldname, val1, val2, true, true);
	}

	/**
	 * 
	 * @return val1 if it is neither null nor empty, else val2
	 */
	public String getValue() {
		return val1==null || val1.isEmpty() ? 
				val2 : val1;
	}
	
	/**
	 * @return the type
	 */
	public FilterType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(FilterType type) {
		this.type = type;
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
	 * @return the val1
	 */
	public String getVal1() {
		return val1;
	}

	/**
	 * @param val1 the val1 to set
	 */
	public void setVal1(String val1) {
		this.val1 = val1;
	}

	/**
	 * @return the val2
	 */
	public String getVal2() {
		return val2;
	}

	/**
	 * @param val2 the val2 to set
	 */
	public void setVal2(String val2) {
		this.val2 = val2;
	}

	/**
	 * @return the includeLow
	 */
	public boolean isIncludeLow() {
		return includeLow;
	}

	/**
	 * @param includeLow the includeLow to set
	 */
	public void setIncludeLow(boolean includeLow) {
		this.includeLow = includeLow;
	}

	/**
	 * @return the includeHigh
	 */
	public boolean isIncludeHigh() {
		return includeHigh;
	}

	/**
	 * @param includeHigh the includeHigh to set
	 */
	public void setIncludeHigh(boolean includeHigh) {
		this.includeHigh = includeHigh;
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
		result = prime * result + (includeHigh ? 1231 : 1237);
		result = prime * result + (includeLow ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((val1 == null) ? 0 : val1.hashCode());
		result = prime * result + ((val2 == null) ? 0 : val2.hashCode());
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
		FilterSetting other = (FilterSetting) obj;
		if (fieldname == null) {
			if (other.fieldname != null)
				return false;
		} else if (!fieldname.equals(other.fieldname))
			return false;
		if (includeHigh != other.includeHigh)
			return false;
		if (includeLow != other.includeLow)
			return false;
		if (type != other.type)
			return false;
		if (val1 == null) {
			if (other.val1 != null)
				return false;
		} else if (!val1.equals(other.val1))
			return false;
		if (val2 == null) {
			if (other.val2 != null)
				return false;
		} else if (!val2.equals(other.val2))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FilterSetting [type=" + type + ", fieldname=" + fieldname
				+ ", val1=" + val1 + ", val2=" + val2 + ", includeLow="
				+ includeLow + ", includeHigh=" + includeHigh + "]";
	}

	
	
	

}
