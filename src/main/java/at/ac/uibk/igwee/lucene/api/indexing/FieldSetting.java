package at.ac.uibk.igwee.lucene.api.indexing;

import at.ac.uibk.igwee.lucene.api.Constants;
import at.ac.uibk.igwee.lucene.api.Constants.IndexOption;
import at.ac.uibk.igwee.lucene.api.Constants.NumericType;

/**
 * Setting for how to index a field.
 * <p>
 * Basically: A field has a name and is attributed to an analyzer. 
 * You can further determine the FieldType.
 * @author Joseph
 *
 */
public final class FieldSetting implements java.io.Serializable {

	private static final long serialVersionUID = 201310211154L;
	
	/**
	 * fieldname.
	 */
	private String fieldname;
	/**
	 * Analyzername
	 */
	private String analyzername;
	/**
	 * Should it be indexed? default = true.
	 */
	private boolean indexed = true;
	/**
	 * How to index. Default: DOCS_AND_FREQS_AND_POSITIONS.
	 */
	private Constants.IndexOption indexOption = Constants.IndexOption.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
	
	/**
	 * Numeric precision.
	 */
	private int precisionStep = 4;
	
	/**
	 * Numeric type. Default: NULL (not a number)
	 */
	private Constants.NumericType numericType = Constants.NumericType.NULL;
	
	/**
	 * Stored? default = true.
	 */
	private boolean stored = true;
	
	/**
	 * StoreTermVectorOffsets? default = true.
	 */
	private boolean storeTermVectorOffsets = true;
	/**
	 * StoreTermVectorPositions= default = true.
	 */
	private boolean storeTermVectorPositions = true;
	/**
	 * Store termvectors? default = true;
	 */
	private boolean storeTermVectors = true;
	/**
	 * Tokenized? default = true.
	 */
	private boolean tokenized = true;
	
	public FieldSetting() {
		super();
	}
	
	public FieldSetting(String fieldname, String analyzername) {
		this();
		this.fieldname = fieldname;
		this.analyzername = analyzername;
	}
	
	private Object readResolve() {
		if (this.indexOption==null) this.indexOption = IndexOption.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
		if (this.numericType==null) this.numericType = NumericType.NULL;
		return this;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((analyzername == null) ? 0 : analyzername.hashCode());
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
		FieldSetting other = (FieldSetting) obj;
		if (analyzername == null) {
			if (other.analyzername != null)
				return false;
		} else if (!analyzername.equals(other.analyzername))
			return false;
		if (fieldname == null) {
			if (other.fieldname != null)
				return false;
		} else if (!fieldname.equals(other.fieldname))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FieldSetting [fieldname=" + fieldname + ", analyzername="
				+ analyzername + ", indexed=" + indexed + ", indexOption="
				+ indexOption + ", precisionStep=" + precisionStep
				+ ", numericType=" + numericType + ", stored=" + stored
				+ ", storeTermVectorOffsets=" + storeTermVectorOffsets
				+ ", storeTermVectorPositions=" + storeTermVectorPositions
				+ ", storeTermVectors=" + storeTermVectors + ", tokenized="
				+ tokenized + "]";
	}

	/**
	 * 
	 * @return fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * 
	 * @param fieldname fieldname
	 */
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	/**
	 * 
	 * @return analyzername
	 */
	public String getAnalyzername() {
		return analyzername;
	}

	/**
	 * 
	 * @param analyzername
	 */
	public void setAnalyzername(String analyzername) {
		this.analyzername = analyzername;
	}

	/**
	 * 
	 * @return indexed
	 */
	public boolean isIndexed() {
		return indexed;
	}

	/**
	 * 
	 * @param indexed?
	 */
	public void setIndexed(boolean index) {
		this.indexed = index;
	}

	/**
	 * @return indexOption
	 */
	public Constants.IndexOption getIndexOption() {
		return indexOption;
	}

	/**
	 * 
	 * @param indexOption
	 */
	public void setIndexOption(Constants.IndexOption indexOption) {
		this.indexOption = indexOption;
	}

	/**
	 * 
	 * @return
	 */
	public int getPrecisionStep() {
		return precisionStep;
	}

	/**
	 * 
	 * @param precisionStep
	 */
	public void setPrecisionStep(int precisionStep) {
		this.precisionStep = precisionStep;
	}

	/**
	 * 
	 * @return
	 */
	public Constants.NumericType getNumericType() {
		return numericType;
	}

	/**
	 * 
	 * @param numericType
	 */
	public void setNumericType(Constants.NumericType numericType) {
		this.numericType = numericType;
	}

	/**
	 * 
	 * @return stored?
	 */
	public boolean isStored() {
		return stored;
	}

	/**
	 * 
	 * @param stored
	 */
	public void setStored(boolean stored) {
		this.stored = stored;
	}

	/**
	 * 
	 * @return storeTermVectorOffests?
	 */
	public boolean isStoreTermVectorOffsets() {
		return storeTermVectorOffsets;
	}

	/**
	 * 
	 * @param storeTermVectorOffsets
	 */
	public void setStoreTermVectorOffsets(boolean storeTermVectorOffsets) {
		this.storeTermVectorOffsets = storeTermVectorOffsets;
	}

	/**
	 * 
	 * @return storeTermVectorPositions?
	 */
	public boolean isStoreTermVectorPositions() {
		return storeTermVectorPositions;
	}

	/**
	 * 
	 * @param storeTermVectorPositions
	 */
	public void setStoreTermVectorPositions(boolean storeTermVectorPositions) {
		this.storeTermVectorPositions = storeTermVectorPositions;
	}

	/**
	 * 
	 * @return storeTermVectors?
	 */
	public boolean isStoreTermVectors() {
		return storeTermVectors;
	}

	/**
	 * 
	 * @param storeTermVectors
	 */
	public void setStoreTermVectors(boolean storeTermVectors) {
		this.storeTermVectors = storeTermVectors;
	}

	/**
	 * 
	 * @return tokenized?
	 */
	public boolean isTokenized() {
		return tokenized;
	}

	/**
	 * 
	 * @param tokenized
	 */
	public void setTokenized(boolean tokenized) {
		this.tokenized = tokenized;
	}
	
	
	
}
