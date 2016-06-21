package at.ac.uibk.igwee.lucene.impl;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.Version;

import at.ac.uibk.igwee.lucene.api.Constants;

/**
 * Constants for the implementation.
 * @author Joseph
 *
 */
public class ImplConstants {

	/**
	 * "luceneMatchVersion" used for the factory parameters.
	 */
	public static final String LUCENE_MATCH_VERSION_KEY = "luceneMatchVersion";
	/**
	 * "LUCENE_46" used for the factory parameters.
	 */
	public static final String LUCENE_MATCH_VERSION_VALUE = "LUCENE_55";

	/**
	 * LUCENE_46
	 */
	public static final Version USED_LUCENE_VERSION = Version.LUCENE_5_5_0;

	/**
	 * IndexOptions translator
	 * 
	 * @param opt
	 * @return
	 */
	public static IndexOptions getIndexOptions(Constants.IndexOption opt) {
		switch (opt) {
		case DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS:
			return IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
		case DOCS_AND_FREQS_AND_POSITIONS:
			return IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
		case DOCS_AND_FREQS:
			return IndexOptions.DOCS_AND_FREQS;
		case DOCS_ONLY:
		default:
			return IndexOptions.DOCS;
		}
	}

	/**
	 * FieldType.NumericType translator
	 * @param opt
	 * @return
	 */
	public static FieldType.NumericType getNumericType(Constants.NumericType opt) {
		switch (opt) {
		case DOUBLE:
			return FieldType.NumericType.DOUBLE;
		case FLOAT:
			return FieldType.NumericType.FLOAT;
		case INT:
			return FieldType.NumericType.INT;
		case LONG:
			return FieldType.NumericType.LONG;
		default:
			return null;
		}
	}

}
