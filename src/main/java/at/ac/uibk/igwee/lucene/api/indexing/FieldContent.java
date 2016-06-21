package at.ac.uibk.igwee.lucene.api.indexing;

/**
 * The FieldContent class is a class for value objects. A field content has a fieldname and the content to be indexed.
 * <p>
 * Cave: All fields are final.
 * <p>
 * A document can have several FieldContents with the same fieldname. The indexing service should append additional contents
 * to the same field.
 * @author Joseph
 *
 */
public final class FieldContent {
	
	/**
	 * fieldname
	 */
	private final String fieldname;
	
	/**
	 * The content of the field.
	 */
	private final String content;
	
	
	/**
	 * Default constructor. Not a facet.
	 * @param fieldname
	 * @param content
	 */
	public FieldContent(String fieldname, String content) {
		super();
		this.fieldname= fieldname;
		this.content = content;
	}
	
	
	/**
	 * @return the fieldname.
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FieldContent [fieldname=" + fieldname + ", content=" + content
				+ "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result
				+ ((fieldname == null) ? 0 : fieldname.hashCode());
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
		FieldContent other = (FieldContent) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (fieldname == null) {
			if (other.fieldname != null)
				return false;
		} else if (!fieldname.equals(other.fieldname))
			return false;
		return true;
	}


}
