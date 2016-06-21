package at.ac.uibk.igwee.lucene.api;

import java.util.Locale;

/**
 * Constants for the at.ac.uibk.igwee.lucene.api.indexing package.
 * @author Joseph
 *
 */
public class Constants {
	
	/**
	 * Default name for id field
	 */
	public static final String DOC_ID_DEFAULT_FIELDNAME = "_id";
	
	/**
	 * Default locale
	 */
	public static final Locale DEFAULT_LOCALE = Locale.GERMAN;
	
	/**
	 * Character used to separate each facet.
	 */
	public static final char FACET_SEPARATOR = '|';
	
	/**
	 * Mapping Option for FieldInfo.IndexOptions. Necessary to get rid of
	 * the dependency.
	 * @author Joseph
	 *
	 */
	public enum IndexOption {
		DOCS_AND_FREQS,
		DOCS_AND_FREQS_AND_POSITIONS,
		DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS,
		DOCS_ONLY;
	}
	
	/**
	 * Maps to FieldType.NumericType. Please use NULL for all Non-numeric fields.
	 * @author Joseph
	 *
	 */
	public enum NumericType {
		DOUBLE,
		FLOAT,
		INT,
		LONG,
		NULL;
		
	}
	
	
}
