package at.ac.uibk.igwee.lucene.json;

/**
 * Data object: Sorting clause.
 * @author totoro
 *
 */
public class JsonSortingClause {
	
	/**
	 * name of the field
	 */
	private String sortField;
	
	/**
	 * sorting order
	 */
	private boolean ascending;

	public JsonSortingClause(String sortField, boolean ascending) {
		super();
		this.sortField = sortField;
		this.ascending = ascending;
	}
	
	public JsonSortingClause() {
		super();
	}
	

	/**
	 * @return the sortField
	 */
	public String getSortField() {
		return sortField;
	}

	/**
	 * @return the ascending
	 */
	public boolean isAscending() {
		return ascending;
	}
	
	

	/**
	 * @param sortField the sortField to set
	 */
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	/**
	 * @param ascending the ascending to set
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sortField == null) ? 0 : sortField.hashCode());
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
		JsonSortingClause other = (JsonSortingClause) obj;
		if (sortField == null) {
			if (other.sortField != null)
				return false;
		} else if (!sortField.equals(other.sortField))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsonSortingClause [sortField=" + sortField + ", ascending="
				+ ascending + "]";
	}
	
	

}
