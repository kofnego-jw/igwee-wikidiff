package at.ac.uibk.igwee.lucene.api.viewing;

import java.util.Collections;
import java.util.List;

/**
 * A Field in the view
 * @author Joseph
 *
 */
public final class ViewField {

	/**
	 * Fieldname.
	 */
	private final String fieldname;
	/**
	 * Readable Name.
	 */
	private final String readableName;
	/**
	 * A description for the field.
	 */
	private final String description;
	/**
	 * List of snippets.
	 */
	private final List<String> bodies;
	
	public ViewField(String fieldname, String readableName, String description, List<String> bodies) {
		super();
		this.fieldname = fieldname;
		this.readableName = readableName;
		this.description = description;
		this.bodies = bodies;
	}

	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * @return the readableName
	 */
	public String getReadableName() {
		return readableName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the bodies
	 */
	public List<String> getBodies() {
		return bodies;
	}

	@Override
	public String toString() {
		return "ViewField{" +
				"fieldname='" + fieldname + '\'' +
				", readableName='" + readableName + '\'' +
				", description='" + description + '\'' +
				", bodies=" + bodies +
				'}';
	}

	/**
	 *
	 * @param fieldname fieldname
	 * @param readableName readable name
	 * @param description description
     * @return an empty ViewField
     */
	public static ViewField createEmptyViewField(String fieldname, String readableName, String description) {
		return new ViewField(fieldname, readableName, description, Collections.emptyList());
	}
	
}
