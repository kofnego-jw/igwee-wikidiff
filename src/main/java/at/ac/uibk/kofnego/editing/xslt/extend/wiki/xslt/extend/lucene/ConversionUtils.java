package at.ac.uibk.kofnego.editing.xslt.extend.wiki.xslt.extend.lucene;

import at.ac.uibk.igwee.lucene.api.searching.QuerySetting;
import at.ac.uibk.igwee.lucene.api.searching.QuerySetting.ChainType;
import at.ac.uibk.igwee.lucene.api.searching.QuerySetting.QueryType;
import at.ac.uibk.igwee.lucene.api.searching.SearchRequest;
import at.ac.uibk.igwee.lucene.api.searching.SortSetting;
import at.ac.uibk.igwee.lucene.json.JsonQueryClause;
import at.ac.uibk.igwee.lucene.json.JsonSearchRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Static method for conversions. Should be actually in an extra bundle?
 * TODO: Move to somewhere else?
 * @author Joseph
 *
 */
public class ConversionUtils {

	/**
	 * Converts a JsonSearchRequest to a SearchRequest
	 * 
	 * @param jsr
	 * @return null, if jsr==null or if there is no clause in jsr
	 */
	public static SearchRequest toSearchRequest(JsonSearchRequest jsr) {
		if (jsr == null)
			return null;

		if (jsr.getSearches()==null || jsr.getSearches().length==0) 
			return null;
		
		SearchRequest result = new SearchRequest(jsr.getIndexName());
		List<QuerySetting> qss = Arrays.asList(jsr.getSearches()).stream().map(qc -> convertToQuerySetting(qc))
					.collect(Collectors.toList());
		result.setQuerySettings(qss);
		if (jsr.getSorting()!=null && jsr.getSorting()!=null) {
			SortSetting ss = new SortSetting(jsr.getSorting().getSortField(), jsr.getSorting().isAscending());
			List<SortSetting> sortRules = new ArrayList<>(1);
			sortRules.add(ss);
			result.setSortRules(sortRules);
		}
		result.setPageNumber(jsr.getPageNumber());
		int pageSize = jsr.getPageSize() <= 0 ? JsonSearchRequest.DEFAULT_PAGESIZE : jsr.getPageSize();
		result.setPageSize(pageSize);
		return result;
	}

	/**
	 * Converts a JsonQueryClause to a QuerySetting
	 * @param c
	 * @return
	 */
	protected static QuerySetting convertToQuerySetting(JsonQueryClause c) {
		if (c == null)
			return null;
		QuerySetting result = new QuerySetting(c.getFieldname(),
				c.getQueryString());
		if (c.getChainType() != null) {
			switch (c.getChainType()) {
			case AND:
				result.setChainType(ChainType.AND);
				break;
			case OR:
				result.setChainType(ChainType.OR);
				break;
			case NOT:
				result.setChainType(ChainType.NOT);
			}
		}
		if (c.getQueryType() != null) {
			switch (c.getQueryType()) {
			case TERM:
				result.setQueryType(QueryType.TERM);
				break;
			case QUERYPARSER:
				result.setQueryType(QueryType.QUERYPARSER);
				break;
			case RANGE:
				result.setQueryType(QueryType.RANGE);
			}
		}
		return result;
	}
}
