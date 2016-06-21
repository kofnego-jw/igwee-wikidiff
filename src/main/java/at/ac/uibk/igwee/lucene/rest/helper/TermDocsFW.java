package at.ac.uibk.igwee.lucene.rest.helper;

import java.util.List;

/**
 * Created by joseph on 5/25/16.
 */
public class TermDocsFW {

    public String fieldname;

    public String term;

    public List<String> docIds;

    public TermDocsFW() {

    }

    public TermDocsFW(String fieldname, String term, List<String> docIds) {
        this.fieldname = fieldname;
        this.term = term;
        this.docIds = docIds;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<String> getDocIds() {
        return docIds;
    }

    public void setDocIds(List<String> docIds) {
        this.docIds = docIds;
    }

    @Override
    public String toString() {
        return "TermDocsFW{" +
                "fieldname='" + fieldname + '\'' +
                ", term='" + term + '\'' +
                ", docIds=" + docIds +
                '}';
    }
}
