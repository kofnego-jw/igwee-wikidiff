package at.ac.uibk.igwee.lucene.rest.helper;

import java.util.List;

/**
 * Created by joseph on 5/25/16.
 */
public class ResultFieldFW {

    public String fieldname;

    public List<String> contents;

    public ResultFieldFW() {

    }

    public ResultFieldFW(String fieldname, List<String> contents) {
        this.fieldname = fieldname;
        this.contents = contents;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "ResultFieldFW{" +
                "fieldname='" + fieldname + '\'' +
                ", contents=" + contents +
                '}';
    }
}
