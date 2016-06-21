package at.ac.uibk.igwee.lucene.impl.searching;

import at.ac.uibk.igwee.lucene.api.Constants;
import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;
import at.ac.uibk.igwee.lucene.api.indexing.FieldSetting;
import at.ac.uibk.igwee.lucene.api.indexing.IndexSetting;
import at.ac.uibk.igwee.lucene.api.searching.*;
import at.ac.uibk.igwee.lucene.impl.indexing.IndexingFactory;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.BytesRef;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to create different Query objects.
 *
 * Base usage: new QueryFactory(IndexSetting,SearchSetting,FacetsConfig).
 * And then: queryFactory.createQuery(SearchRequest)
 *
 *
 * @author Joseph
 */
public class QueryFactory {

	private final IndexSetting is;
	private final SearchSetting ss;
    private final FacetsConfig facetsConfig;

	public QueryFactory(IndexSetting is, SearchSetting ss, FacetsConfig fc) {
		super();
		this.is = is;
		this.ss = ss;
        this.facetsConfig = fc;
	}

    public Query createQuery(SearchRequest request) throws LuceneSearchException {
        Query base = createQuery(request.getQuerySettings());
        base = rewriteQueryWithFacets(base,request.getDrillDownFacets(),facetsConfig);
        base = addFilter(base, request.getFilterSettings(), request.getFilterChainingMode());
        return base;
    }

	public Query createQuery(List<QuerySetting> qss)
			throws LuceneSearchException {
		if (qss == null || qss.isEmpty())
			throw new LuceneSearchException("No search clause defined.");
		if (qss.size() == 1)
			return createQuery(qss.get(0));
		BooleanQuery.Builder result = new BooleanQuery.Builder();
		for (QuerySetting qs : qss) {
			Query q = createQuery(qs);
			switch (qs.getChainType()) {
			case AND:
				result.add(q, Occur.MUST);
				break;
			case NOT:
				result.add(q, Occur.MUST_NOT);
				break;
			case OR:
			default:
				result.add(q, Occur.SHOULD);
			}
		}
		return result.build();
	}

	public Query createQuery(QuerySetting qs) throws LuceneSearchException {
		String fieldname = qs.getFieldname();
		if (fieldname == null || fieldname.isEmpty())
			fieldname = ss.getDefaultSearchField();
		switch (qs.getQueryType()) {
		case TERM:
			Term t = new Term(qs.getFieldname(), qs.getQueryString());
			return new TermQuery(t);
		case RANGE:
			String[] terms = qs.getQueryString().split("(\\s+|\\-)");
			String start = terms[0];
			String end = terms[terms.length - 1];
			return new TermRangeQuery(fieldname, new BytesRef(start),
					new BytesRef(end), true, true);
		case QUERYPARSER:
		default:
			QueryParser qp = createQueryParser(fieldname);
			try {
				return qp.parse(qs.getQueryString());
			} catch (Exception e) {
				throw new LuceneSearchException("Cannot parse the query.", e);
			}
		}
	}

	public Query rewriteQueryWithFacets(Query query,
										List<FacetContent> facetContents,
										FacetsConfig facetsConfig) {
		if (facetsConfig==null)
			return query;

        DrillDownQuery drillDownQuery = new DrillDownQuery(facetsConfig, query);

        for (FacetContent fc: facetContents) {
            String[] paths = fc.getComponents().subList(1, fc.getComponents().size()).toArray(new String[0]);
            drillDownQuery.add(fc.getComponents().get(0), paths);
        }
        return drillDownQuery;
	}

	private QueryParser createQueryParser(String fieldname) throws LuceneSearchException {
		try {
			return new QueryParser(fieldname==null ? ss.getDefaultSearchField() : fieldname,
					IndexingFactory.createPerFieldAnalyzer(is, fieldname));
		} catch (Exception e) {
			throw new LuceneSearchException("Cannot create QueryParser.", e);
		}
	}

    private Query createRangedQuery(FilterSetting fs) {
        return new TermRangeQuery(fs.getFieldname(), new BytesRef(fs.getVal1()), new BytesRef(fs.getVal2()), fs.isIncludeLow(), fs.isIncludeHigh());
    }

    private Query createFilterQuery(FilterSetting fs) throws LuceneSearchException {
        switch(fs.getType()) {
            case EXACT:
                return new TermQuery(new Term(fs.getFieldname(), fs.getValue()));
            case RANGE:
                return createRangedQuery(fs);
            default:
            case QUERY:
                return parseQuery(fs);
        }
    }

    private Query parseQuery(FilterSetting fs) throws LuceneSearchException {
        try {
            QueryParser qp = new QueryParser(fs.getFieldname(), IndexingFactory.createPerFieldAnalyzer(is));
            return qp.parse(fs.getValue());
        } catch (Exception e) {
            throw new LuceneSearchException(e.getMessage(), e);
        }
    }

    public Query addFilter(Query baseQuery, List<FilterSetting> filterSettings, FilterChainingMode chainingMode)
            throws LuceneSearchException {
        if (filterSettings==null || filterSettings.isEmpty()) return baseQuery;
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(new BooleanClause(baseQuery, Occur.MUST));
        for (FilterSetting setting: filterSettings) {
            builder.add(new BooleanClause(createFilterQuery(setting), Occur.FILTER));
        }
        return builder.build();
    }

	public Sort createSort(List<SortSetting> sortSettings) {
		if (sortSettings == null || sortSettings.isEmpty())
			return null;
		List<SortField> fields = new ArrayList<>(sortSettings.size());
		for (SortSetting now : sortSettings) {
			String fn = now.getFieldname();
			if (fn.equals(Constants.DOC_ID_DEFAULT_FIELDNAME)) {
				SortField sf = new SortField(fn, SortField.Type.STRING_VAL, 
						!now.isAscending());
				fields.add(sf);
				continue;
			}
			FieldSetting fs = is.getFieldSetting(fn);
			if (fs == null || !fs.isIndexed())
				continue;
            SortField sf;
			if (fs.getNumericType()==null || fs.getNumericType()== Constants.NumericType.NULL) {
                sf = new SortField(fs.getFieldname(),
                        SortField.Type.STRING_VAL, !now.isAscending());
			} else {
                sf = new SortField(fs.getFieldname(), SortField.Type.DOUBLE, !now.isAscending());
            }

			fields.add(sf);
		}
		return new Sort(fields.toArray(new SortField[0]));
	}

}
