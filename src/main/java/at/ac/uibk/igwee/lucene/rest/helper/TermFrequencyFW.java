package at.ac.uibk.igwee.lucene.rest.helper;

/**
 * Created by joseph on 5/25/16.
 */
public class TermFrequencyFW {

    public int docCount;

    public long totalCount;

    public String fieldname;

    public String term;

    public TermFrequencyFW() {

    }

    public TermFrequencyFW(int docCount, long totalCount, String fieldname, String term) {
        this.docCount = docCount;
        this.totalCount = totalCount;
        this.fieldname = fieldname;
        this.term = term;
    }

    public int getDocCount() {
        return docCount;
    }

    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
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

    @Override
    public String toString() {
        return "TermFrequencyFW{" +
                "docCount=" + docCount +
                ", totalCount=" + totalCount +
                ", fieldname='" + fieldname + '\'' +
                ", term='" + term + '\'' +
                '}';
    }
}
