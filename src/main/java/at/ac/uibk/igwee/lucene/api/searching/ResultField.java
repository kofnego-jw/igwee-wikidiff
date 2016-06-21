package at.ac.uibk.igwee.lucene.api.searching;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Arrays;
import java.util.List;
/**
 * Models a field to be shown in the search result
 * @author Joseph
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ResultField {
	
	/**
	 * Name of the field, not the readable name.
	 */
	private String fieldname;
	/**
	 * A list of Strings containing the snippet (or the whole field)
	 */
	private List<String> contents;

	public ResultField() {

	}

	public ResultField(String fieldname, List<String> content) {
		super();
		this.fieldname = fieldname;
		this.contents = content;
	}
	
	public ResultField(String fieldname, String... content) {
		this(fieldname, Arrays.asList(content));
	}
	
	public ResultField(String fieldname, String content) {
		this(fieldname, Arrays.asList(content));
	}

	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * @return the contents
	 */
	public List<String> getContents() {
		return contents;
	}

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
	@Override
	public String toString() {
		return "ResultField [fieldname=" + fieldname + ", contents=" + contents
				+ "]";
	}
	
	

}
