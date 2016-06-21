package at.ac.uibk.igwee.lucene.api.searching;


/**
 * This class models the setting for result viewing
 * @author Joseph
 *
 */
public class ResultSetting implements java.io.Serializable {
	
	private static final long serialVersionUID = 201410061423L;
	
	/**
	 * name of the field
	 */
	private String fieldname;
	
	/**
	 * The mode, how to include the result in the result document
	 */
	private ResultMode mode;
	/**
	 * highlight the result?
	 */
	private boolean highlight = true;
	/**
	 * Fragment size for the fragmenter, will be ignored if mode is not SNIPPET. Default = 20.
	 */
	private int fragmentSize = 20;
	/**
	 * Maximal number of fragments in this field. Default is 50.
	 */
	private int maxFragNumber = 50;
	/**
	 * Name of the fragmenter, can be NULL, SIMPLE, SIMPLESPAN or TOKENGROUP
	 */
	private FragmenterName fragmenterName = FragmenterName.SIMPLE;
	
	/**
	 * Merge the contiguous fragments
	 */
	private boolean mergeContiguous = true;
	
	public ResultSetting() {
		super();
	}
	
	public ResultSetting(String fieldname, ResultMode mode) {
		this();
		this.fieldname = fieldname;
		this.mode = mode;
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
	 * @return the mode
	 */
	public ResultMode getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(ResultMode mode) {
		this.mode = mode;
	}
	
	

	/**
	 * @return the highlight
	 */
	public boolean isHighlight() {
		return highlight;
	}

	/**
	 * @param highlight the highlight to set
	 */
	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}
	
	/**
	 * @return the fragmentSize
	 */
	public int getFragmentSize() {
		return fragmentSize;
	}

	/**
	 * @param fragmentSize the fragmentSize to set
	 */
	public void setFragmentSize(int fragmentSize) {
		this.fragmentSize = fragmentSize;
	}
	
	
	/**
	 * @return the maxFragNumber
	 */
	public int getMaxFragNumber() {
		return maxFragNumber;
	}

	/**
	 * @param maxFragNumber the maxFragNumber to set
	 */
	public void setMaxFragNumber(int maxFragNumber) {
		this.maxFragNumber = maxFragNumber;
	}
	

	/**
	 * @return the fragmenterName
	 */
	public FragmenterName getFragmenterName() {
		return fragmenterName;
	}

	/**
	 * @param fragmenterName the fragmenterName to set
	 */
	public void setFragmenterName(FragmenterName fragmenterName) {
		this.fragmenterName = fragmenterName;
	}

	
	
	/**
	 * @return the mergeContiguous
	 */
	public boolean isMergeContiguous() {
		return mergeContiguous;
	}

	/**
	 * @param mergeContiguous the mergeContiguous to set
	 */
	public void setMergeContiguous(boolean mergeContiguous) {
		this.mergeContiguous = mergeContiguous;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResultSetting [fieldname=" + fieldname + ", mode=" + mode
				+ ", highlight=" + highlight + ", fragmentSize=" + fragmentSize
				+ ", maxFragNumber=" + maxFragNumber + "]";
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
		ResultSetting other = (ResultSetting) obj;
		if (fieldname == null) {
			if (other.fieldname != null)
				return false;
		} else if (!fieldname.equals(other.fieldname))
			return false;
		return true;
	}
	
	private Object readResolve() {
		if (this.getFragmenterName() == null)
			this.fragmenterName = FragmenterName.TOKENGROUP;
		return this;
	}
	
	

}
