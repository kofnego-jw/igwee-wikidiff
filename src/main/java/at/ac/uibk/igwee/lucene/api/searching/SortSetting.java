package at.ac.uibk.igwee.lucene.api.searching;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Sort setting.
 * @author Joseph
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
		getterVisibility = JsonAutoDetect.Visibility.NONE,
		isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class SortSetting implements java.io.Serializable {
	
	private static final long serialVersionUID = 2013012091227L;
	
	/**
	 * Name of the field
	 */
	private String fieldname;
	/**
	 * Ascending sorting? default is true
	 */
	private boolean ascending;

	public SortSetting() {

    }
	
	public SortSetting(String fieldname) {
		this(fieldname, true);
	}
	
	public SortSetting(String fieldname, boolean asc) {
		super();
		this.fieldname = fieldname;
		this.ascending = asc;
	}

	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * @return the ascending
	 */
	public boolean isAscending() {
		return ascending;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SortSetting [fieldname=" + fieldname + ", ascending="
				+ ascending + "]";
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
		SortSetting other = (SortSetting) obj;
		if (fieldname == null) {
			if (other.fieldname != null)
				return false;
		} else if (!fieldname.equals(other.fieldname))
			return false;
		return true;
	}
	
	

}
