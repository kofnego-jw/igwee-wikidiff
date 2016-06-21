package at.ac.uibk.igwee.wikidiff.lucene;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Data Object for indexing purposes.
 *
 * Created by joseph on 6/6/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LuceneDoc {

    /**
     * Document ID
     */
    public String docId;
    /**
     * List of contents
     */
    public List<LuceneField> contents;

    public LuceneDoc() {
    }

    public LuceneDoc(String docId, List<LuceneField> contents) {
        this.docId = docId;
        this.contents = contents;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public List<LuceneField> getContents() {
        return contents;
    }

    public void setContents(List<LuceneField> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "LuceneDoc{" +
                "docId='" + docId + '\'' +
                ", contents=" + contents +
                '}';
    }
}
