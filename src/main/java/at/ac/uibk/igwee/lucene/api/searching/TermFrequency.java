package at.ac.uibk.igwee.lucene.api.searching;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * 
 * TermFrequency statistics
 * Implements the result of a Term Enumeration
 * 
 * @author Joseph
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class TermFrequency implements java.io.Serializable, Comparable<TermFrequency> {
	
	private static final long serialVersionUID = 201410210946L;
	
	/**
	 * Index name
	 */
	private String index;
	/**
	 * Field name
	 */
	private String field;
	/**
	 * The term indexed.
	 */
	private String term;
	/**
	 * Document count, should always be more than 0
	 */
	private int documentCount;
	/**
	 * Total count, is -1 if the total count is not supported
	 */
	private long totalCount;

	public TermFrequency() {

	}
	
	public TermFrequency(String index, String field, String term,
			int documentCount, long totalCount) {
		super();
		this.index = index;
		this.field = field;
		this.term = term;
		this.documentCount = documentCount;
		this.totalCount = totalCount;
	}
	
	@Override
	public int compareTo(TermFrequency o) {
		
		if (index!=null && !index.equals(o.index))
			return index.compareTo(o.index);
		
		if (field!=null && !field.equals(o.field))
			return field.compareTo(o.field);
		
		if (term!=null)
			return term.compareTo(o.term);
		
		return this.documentCount - o.documentCount;
	}
	
	/**
	 * @return the index
	 */
	public String getIndex() {
		return index;
	}
	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	/**
	 * @return the documentCount
	 */
	public int getDocumentCount() {
		return documentCount;
	}
	/**
	 * @return the totalCount
	 */
	public long getTotalCount() {
		return totalCount;
	}

    public void setIndex(String index) {
        this.index = index;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setDocumentCount(int documentCount) {
        this.documentCount = documentCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
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
		TermFrequency other = (TermFrequency) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TermFrequency [index=" + index + ", field=" + field + ", term="
				+ term + ", documentCount=" + documentCount + ", totalCount="
				+ totalCount + "]";
	}
	
	
	

}
