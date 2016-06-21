package at.ac.uibk.igwee.wikidiff.lucene;

/**
 * DataValueType for a LuceneField
 *
 * Created by joseph on 6/6/16.
 */
public class LuceneField {

    /**
     * Field name
     */
    public String fieldname;
    /**
     * content
     */
    public String content;

    public LuceneField() {
    }

    public LuceneField(String fieldname, String content) {
        this.fieldname = fieldname;
        this.content = content;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LuceneField{" +
                "fieldname='" + fieldname + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
