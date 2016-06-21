package at.ac.uibk.igwee.lucene.rest.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by joseph on 5/23/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SortSettingFW {

    public String fieldname;

    public boolean ascending = true;

    public SortSettingFW() {

    }

    public SortSettingFW(String fieldname, boolean ascending) {
        this.fieldname = fieldname;
        this.ascending = ascending;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public String toString() {
        return "SortSettingFW{" +
                "fieldname='" + fieldname + '\'' +
                ", ascending=" + ascending +
                '}';
    }
}
