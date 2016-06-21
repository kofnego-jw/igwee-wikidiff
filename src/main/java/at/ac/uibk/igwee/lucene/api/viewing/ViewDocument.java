package at.ac.uibk.igwee.lucene.api.viewing;

import java.util.List;

/**
 *
 * This models the result of a view document.
 *
 * @author joseph
 */
public class ViewDocument {

    /**
     * The index name
     */
	private final String indexName;

    /**
     * The id of the document
     */
	private final String id;

    /**
     * A list of fullFields
     */
	private final List<ViewField> fullFields;

    /**
     * A list of abstracts
     */
	private final List<ViewField> abstracts;
	
	public ViewDocument(String indexname, String id, List<ViewField> fullFields, List<ViewField> abstracts) {
		this.id = id;
		this.indexName = indexname;
		this.fullFields = fullFields;
		this.abstracts = abstracts;
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
	 * @return the fullFields
	 */
	public List<ViewField> getFullFields() {
		return fullFields;
	}

	/**
	 *
	 * @return the abstracts
     */
	public List<ViewField> getAbstracts() {
		return abstracts;
	}

	@Override
	public String toString() {
		return "ViewDocument{" +
				"indexName='" + indexName + '\'' +
				", id='" + id + '\'' +
				", fullFields=" + fullFields +
				", abstracts=" + abstracts +
				'}';
	}
}
