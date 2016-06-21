package at.ac.uibk.igwee.lucene.api.searching;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Collection;
import java.util.TreeSet;

/**
 * This is basically an implementation of the triple: fieldname, term, Set of Strings while 
 * the Set contains the ID of each Lucene documents.
 * @author Joseph
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class TermDocs implements Comparable<TermDocs> {
	
	/**
	 * The fieldname
	 */
	private String fieldname;
	/**
	 * The term, as indexed in Lucene (after Anaylzing)
	 */
	private String term;
	/**
	 * A Set of DocId Strings, implemented as TreeSet to ensure the order of the docID
	 */
	private TreeSet<String> docIds;

	public TermDocs() {
        super();
        this.docIds = new TreeSet<>();
    }
	/**
	 * 
	 * @param fieldname
	 * @param term
	 * @param docIds
	 */
	public TermDocs(String fieldname, String term, Collection<String> docIds) {
		super();
		this.fieldname = fieldname;
		this.term = term;
		this.docIds = new TreeSet<>();
		this.docIds.addAll(docIds);
	}
	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	/**
	 * @return the docIds
	 */
	public TreeSet<String> getDocIds() {
		return docIds;
	}

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setDocIds(TreeSet<String> docIds) {
        this.docIds = docIds;
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
		TermDocs other = (TermDocs) obj;
		if (fieldname == null) {
			if (other.fieldname != null)
				return false;
		} else if (!fieldname.equals(other.fieldname))
			return false;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(TermDocs o) {
		
		if (!fieldname.equals(o.fieldname))
			return fieldname.compareTo(o.fieldname);
		
		return term.compareTo(o.term);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TermDocs [fieldname=" + fieldname + ", term=" + term
				+ ", docIds=" + docIds + "]";
	}
	
	
	

}
