package at.ac.uibk.igwee.lucene.rest.helper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by joseph on 5/23/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuerySettingFW {

    public String fieldname;

    public String queryString;

    public int chainType;

    public int queryType;

    public QuerySettingFW() {
    }

    public QuerySettingFW(String fieldname, String queryString, int chainType, int queryType) {
        this.fieldname = fieldname;
        this.queryString = queryString;
        this.chainType = chainType;
        this.queryType = queryType;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public int getChainType() {
        return chainType;
    }

    public void setChainType(int chainType) {
        this.chainType = chainType;
    }

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    @Override
    public String toString() {
        return "QuerySettingFW{" +
                "fieldname='" + fieldname + '\'' +
                ", queryString='" + queryString + '\'' +
                ", chainType=" + chainType +
                ", queryType=" + queryType +
                '}';
    }
}
