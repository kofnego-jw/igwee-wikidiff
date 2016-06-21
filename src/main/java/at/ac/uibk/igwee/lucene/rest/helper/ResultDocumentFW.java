package at.ac.uibk.igwee.lucene.rest.helper;

import java.util.List;

/**
 * Created by joseph on 5/25/16.
 */
public class ResultDocumentFW {

    public String id;

    public List<ResultFieldFW> fields;

    public ResultDocumentFW() {

    }

    public ResultDocumentFW(String id, List<ResultFieldFW> fields) {
        this.id = id;
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ResultFieldFW> getFields() {
        return fields;
    }

    public void setFields(List<ResultFieldFW> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "ResultDocumentFW{" +
                "id='" + id + '\'' +
                ", fields=" + fields +
                '}';
    }
}
