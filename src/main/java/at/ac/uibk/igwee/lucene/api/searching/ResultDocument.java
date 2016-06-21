package at.ac.uibk.igwee.lucene.api.searching;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

/**
 * This class models one hit (in document)
 * @author Joseph
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ResultDocument {
	
	/**
	 * The database index
	 */
	private String indexName;
	/**
	 * The id of the document, should be the value indexed in IndexingService.DOC_ID_FIELDNAME
	 */
	private String id;
	/**
	 * Fields that are displayed to the users.
	 */
	private List<ResultField> fields;

    public ResultDocument() {

    }
	
	public ResultDocument(String indexName, String id, List<ResultField> fields) {
		super();
		this.indexName = indexName;
		this.id = id;
		this.fields = fields;
	}

	/**
	 * @return the indexName
	 */
	public String getIndexName() {
		return indexName;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the fields
	 */
	public List<ResultField> getFields() {
		return fields;
	}

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFields(List<ResultField> fields) {
        this.fields = fields;
    }

    @Override
	public String toString() {
		return "ResultDocument{" +
				"indexName='" + indexName + '\'' +
				", id='" + id + '\'' +
				", fields=" + fields +
				'}';
	}
}
