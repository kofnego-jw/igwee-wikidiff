package at.ac.uibk.igwee.lucene.rest.helper;

import at.ac.uibk.igwee.lucene.api.searching.FilterSetting;

/**
 * Created by joseph on 5/23/16.
 */
public class FilterSettingFW {

    public int filterType;

    public String fieldname;

    public String qs1;

    public String qs2;

    public boolean includeLow = true;

    public boolean includeHigh = true;

    public FilterSettingFW() {

    }

    public FilterSettingFW(int filterType, String fieldname, String qs1, String qs2, boolean includeLow, boolean includeHigh) {
        this.filterType = filterType;
        this.fieldname = fieldname;
        this.qs1 = qs1;
        this.qs2 = qs2;
        this.includeLow = includeLow;
        this.includeHigh = includeHigh;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getQs1() {
        return qs1;
    }

    public void setQs1(String qs1) {
        this.qs1 = qs1;
    }

    public String getQs2() {
        return qs2;
    }

    public void setQs2(String qs2) {
        this.qs2 = qs2;
    }

    public boolean isIncludeLow() {
        return includeLow;
    }

    public void setIncludeLow(boolean includeLow) {
        this.includeLow = includeLow;
    }

    public boolean isIncludeHigh() {
        return includeHigh;
    }

    public void setIncludeHigh(boolean includeHigh) {
        this.includeHigh = includeHigh;
    }

    @Override
    public String toString() {
        return "FilterSettingFW{" +
                "filterType=" + filterType +
                ", fieldname='" + fieldname + '\'' +
                ", qs1='" + qs1 + '\'' +
                ", qs2='" + qs2 + '\'' +
                ", includeLow=" + includeLow +
                ", includeHigh=" + includeHigh +
                '}';
    }
}
