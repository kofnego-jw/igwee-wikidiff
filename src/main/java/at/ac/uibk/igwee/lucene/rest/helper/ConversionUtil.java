package at.ac.uibk.igwee.lucene.rest.helper;

import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;
import at.ac.uibk.igwee.lucene.api.searching.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts between SearchRequest and SearchRequestFW
 *
 * Converts from SearchResult to SearchResultFW
 *
 * Converts from TermFrequency to TermFreqFW
 *
 * Converts from TermDocx to TermDocsFW
 *
 * Created by joseph on 5/23/16.
 */
public class ConversionUtil {

    private static final int DEFAULT_PAGESIZE = 20;
    private static final int DEFAULT_PAGENUMBER = 0;


    public static List<TermDocsFW> toTermDocsFWList(List<TermDocs> list) {
        if (list==null) return Collections.emptyList();
        return list.stream()
                .map(x -> toTermDocsFW(x))
                .filter(x -> x!=null)
                .collect(Collectors.toList());
    }

    public static TermDocsFW toTermDocsFW(TermDocs td) {
        if (td==null) return null;
        List<String> docIds = td.getDocIds()==null ? Collections.emptyList() :
                td.getDocIds().stream().collect(Collectors.toList());
        return new TermDocsFW(td.getFieldname(), td.getTerm(), docIds);
    }

    public static List<TermFrequencyFW> toTermFrequencyFWList(List<TermFrequency> list) {
        if (list==null) return Collections.emptyList();
        return list.stream()
                .map(x -> toTermFrequencyFW(x))
                .filter(x -> x!=null)
                .collect(Collectors.toList());
    }

    public static TermFrequencyFW toTermFrequencyFW(TermFrequency tf) {
        if (tf==null) return null;
        return new TermFrequencyFW(tf.getDocumentCount(), tf.getTotalCount(), tf.getField(), tf.getTerm());
    }

    public static SearchRequestFW toSearchRequestFW(SearchRequest sr) {
        if (sr==null) return null;

        List<QuerySettingFW> qss = sr.getQuerySettings()==null ?
                Collections.emptyList() :
                sr.getQuerySettings().stream()
                .map(x -> toQuerySettingFW(x))
                .filter(x -> x!=null)
                .collect(Collectors.toList());

        List<SortSettingFW> sss = sr.getSortRules() == null ? Collections.emptyList() :
                sr.getSortRules().stream()
                        .map(x -> toSortSettingFW(x))
                        .filter(x -> x != null)
                        .collect(Collectors.toList());

        List<FilterSettingFW> fss = sr.getFilterSettings() == null ?
                Collections.emptyList() :
                sr.getFilterSettings().stream()
                .map(x -> toFilterSettingFW(x))
                .filter(x -> x!=null)
                .collect(Collectors.toList());

        List<FacetFW> fcs = sr.getDrillDownFacets()==null ?
                Collections.emptyList() :
                sr.getDrillDownFacets().stream()
                .map(x -> toFacetFW(x))
                .filter(x -> x!=null)
                .collect(Collectors.toList());

        return new SearchRequestFW(sr.getPageNumber(), sr.getPageSize(), qss, sss, fss, toFilterChainingModeInt(sr.getFilterChainingMode()), fcs);
    }

    public static SortSettingFW toSortSettingFW(SortSetting ss) {
        if (ss==null) return null;
        return new SortSettingFW(ss.getFieldname(), ss.isAscending());
    }

    public static QuerySettingFW toQuerySettingFW(QuerySetting qs) {
        if (qs==null) return null;
        return new QuerySettingFW(qs.getFieldname(), qs.getQueryString(),
                toChainTypeInt(qs.getChainType()), toQueryTypeInt(qs.getQueryType()));
    }


    public static FilterSettingFW toFilterSettingFW(FilterSetting fs) {
        if (fs==null) return null;
        return new FilterSettingFW(toFilterTypeInt(fs.getType()), fs.getFieldname(),
                fs.getVal1(), fs.getVal2(),
                fs.isIncludeLow(), fs.isIncludeHigh());
    }

    public static FacetFW toFacetFW(FacetContent fc) {
        if (fc==null || fc.getComponents()==null) return  null;
        return new FacetFW(fc.getComponents());
    }

    public static SearchRequest toSearchRequest(String indexName, SearchRequestFW fw) {
        if (fw==null) return null;
        SearchRequest sr = new SearchRequest();
        sr.setIndexName(indexName);
        List<QuerySetting> qsList = fw.querySettings
                .stream()
                .map(x -> toQuerySetting(x))
                .filter(x -> x != null)
                .collect(Collectors.toList());
        sr.setQuerySettings(qsList);
        sr.setPageNumber(fw.pageNumber<0 ? DEFAULT_PAGENUMBER : fw.pageNumber);
        sr.setPageSize(fw.pageSize<5 ? DEFAULT_PAGESIZE : fw.pageSize);
        List<SortSetting> ssList = fw.sortSettings.stream()
                .map(x -> toSortSetting(x))
                .filter(x -> x != null)
                .collect(Collectors.toList());
        sr.setSortRules(ssList);
        List<FilterSetting> fsList = fw.filterSettings.stream()
                .map(x -> toFilterSetting(x))
                .filter(x -> x!=null)
                .collect(Collectors.toList());
        sr.setFilterSettings(fsList);
        sr.setFilterChainingMode(toFilterChainingMode(fw.filterChainingMode));
        List<FacetContent> drillDownFacets = fw.facets.stream()
                .map(x -> toFacetContent(x))
                .filter(x -> x!=null)
                .collect(Collectors.toList());
        sr.setDrillDownFacets(drillDownFacets);
        return sr;
    }

    public static FacetContent toFacetContent(FacetFW fw) {
        if (fw==null || fw.components==null || fw.components.isEmpty()) return null;
        return new FacetContent(fw.components);
    }

    public static SortSetting toSortSetting(SortSettingFW fw) {
        if (fw==null || fw.fieldname==null) return null;
        return new SortSetting(fw.fieldname, fw.ascending);
    }

    public static QuerySetting toQuerySetting(QuerySettingFW fw) {
        if (fw==null) return null;
        QuerySetting qs = new QuerySetting();
        qs.setFieldname(fw.fieldname);
        qs.setQueryString(fw.queryString);
        qs.setChainType(toChainType(fw.chainType));
        qs.setQueryType(toQueryType(fw.queryType));
        return qs;
    }

    public static FilterSetting toFilterSetting(FilterSettingFW fw) {
        if (fw==null) return null;
        if (fw.fieldname==null) return null;
        if (fw.qs1==null) {
            if (fw.qs2==null) return null;
            fw.qs1 = fw.qs2;
        }
        FilterSetting fs = new FilterSetting();
        fs.setType(toFilterType(fw.filterType));
        fs.setFieldname(fw.fieldname);
        fs.setIncludeHigh(fw.includeHigh);
        fs.setIncludeLow(fw.includeLow);
        fs.setVal1(fw.qs1);
        fs.setVal2(fw.qs2);
        return fs;
    }

    public static QuerySetting.QueryType toQueryType(int num) {
        if (num <0 || num >= QuerySetting.QueryType.values().length) return QuerySetting.QueryType.QUERYPARSER;
        return QuerySetting.QueryType.values()[num];
    }

    public static QuerySetting.ChainType toChainType(int num) {
        if (num <0 || num >= QuerySetting.ChainType.values().length) return QuerySetting.ChainType.OR;
        return QuerySetting.ChainType.values()[num];
    }

    public static FilterType toFilterType(int num) {
        if (num <0 || num >= FilterType.values().length) return FilterType.QUERY;
        return FilterType.values()[num];
    }

    public static FilterChainingMode toFilterChainingMode(int num) {
        if (num <0 || num >= FilterChainingMode.values().length) return FilterChainingMode.AND;
        return FilterChainingMode.values()[num];
    }

    public static int toQueryTypeInt(QuerySetting.QueryType qt) {
        if (qt==null) return 2; // Default QueryType: QUERYPARSER
        return qt.ordinal();
    }

    public static int toChainTypeInt(QuerySetting.ChainType ct) {
        if (ct==null) return 2; // Default ChainType: OR
        return ct.ordinal();
    }

    public static int toFilterTypeInt(FilterType ft) {
        if (ft==null) return 0; // Default FilterType: QUERY
        return ft.ordinal();
    }

    public static int toFilterChainingModeInt(FilterChainingMode fc) {
        if (fc==null) return 0; // Default FilterChainingMode: AND
        return fc.ordinal();
    }


    public static SearchResultFW toSearchResultFW(SearchResult sr) {
        if (sr==null) return new SearchResultFW();

        SearchRequestFW requestFW = toSearchRequestFW(sr.getRequest());

        int totalHits = sr.getTotalHits();

        List<ResultDocumentFW> docs = sr.getResultDocuments() == null ? Collections.emptyList() :
                sr.getResultDocuments().stream()
                        .map(x -> toResultDocumentFW(x))
                        .filter(x -> x != null)
                        .collect(Collectors.toList());

        List<FacetSearchResultFW> facetResults = sr.getFacetResults() == null ? Collections.emptyList() :
                sr.getFacetResults().stream()
                        .map(x -> toFacetSearchResultFW(x))
                        .filter(x -> x != null)
                        .collect(Collectors.toList());

        return new SearchResultFW(requestFW, totalHits, facetResults, docs);

    }

    public static FacetSearchResultFW toFacetSearchResultFW(FacetSearchResult fsr) {
        if (fsr==null) return null;
        int count = fsr.getChildCount();
        double value = fsr.getValue();
        FacetFW baseFacet = toFacetFW(fsr.getFacetContent());
        List<FacetSearchResultFW> children = fsr.getChildren() == null ? Collections.emptyList() :
                fsr.getChildren().stream()
                        .map(x -> toFacetSearchResultFW(x))
                        .filter(x -> x != null)
                        .collect(Collectors.toList());
        return new FacetSearchResultFW(count, baseFacet, value, children);
    }

    public static ResultDocumentFW toResultDocumentFW(ResultDocument rd) {
        if (rd==null) return null;
        List<ResultFieldFW> fields = rd.getFields()==null ? Collections.emptyList() :
                rd.getFields().stream()
                .map(x -> toResultFieldFW(x))
                .filter(x -> x != null)
                .collect(Collectors.toList());
        return new ResultDocumentFW(rd.getId(), fields);
    }

    public static ResultFieldFW toResultFieldFW(ResultField rf) {
        if (rf==null || rf.getContents()==null) return null;
        return new ResultFieldFW(rf.getFieldname(), rf.getContents());
    }

}
