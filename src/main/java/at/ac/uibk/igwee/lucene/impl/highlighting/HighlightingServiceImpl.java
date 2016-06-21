package at.ac.uibk.igwee.lucene.impl.highlighting;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import at.ac.uibk.igwee.lucene.impl.FacetsConfigHolder;
import at.ac.uibk.igwee.lucene.impl.FileLuceneDirHolder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.DefaultEncoder;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import at.ac.uibk.igwee.lucene.api.highlighting.HighlightingService;
import at.ac.uibk.igwee.lucene.api.highlighting.LuceneHighlightException;
import at.ac.uibk.igwee.lucene.api.indexing.IndexSetting;
import at.ac.uibk.igwee.lucene.api.indexing.LuceneIndexException;
import at.ac.uibk.igwee.lucene.api.searching.LuceneSearchException;
import at.ac.uibk.igwee.lucene.api.searching.QuerySetting;
import at.ac.uibk.igwee.lucene.api.searching.SearchRequest;
import at.ac.uibk.igwee.lucene.api.searching.SearchSetting;
import at.ac.uibk.igwee.lucene.impl.indexing.IndexingFactory;
import at.ac.uibk.igwee.lucene.impl.searching.QueryFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Implementation class of HighlightingService. Uses internal a SearchRequest-Cache.
 * @author Joseph
 *
 */
public class HighlightingServiceImpl implements HighlightingService {
	
	/**
	 * Default cache size
	 */
	private static final int DEFAULT_CACHE_SIZE = 100;
	
	/**
	 * IndexSetting Set
	 */
	protected Set<IndexSetting> iss;
	/**
	 * SearchSetting Set
	 */
	protected Set<SearchSetting> sss;
	
	/**
	 * query cache
	 */
	protected LoadingCache<SearchRequest, Query> queryCache;

	/**
	 * FacetsConfigHolder
	 */
	protected FacetsConfigHolder facetsConfigHolder;
	
	/**
	 * Requires both sets to initialize the service.
	 * @param is
	 * @param ss
	 * @param cacheSize
	 */
	public HighlightingServiceImpl(FacetsConfigHolder facetsConfigHolder, Set<IndexSetting> is, Set<SearchSetting> ss, int cacheSize) {
		super();
		this.iss = is;
		this.sss = ss;
		this.queryCache = CacheBuilder.newBuilder()
				.maximumSize(cacheSize)
				.expireAfterAccess(100, TimeUnit.SECONDS)
				.build(new CacheLoader<SearchRequest, Query>(){
					@Override
					public Query load(SearchRequest key) throws LuceneHighlightException {
						return createQuery(key);
					}
				});
		this.facetsConfigHolder = facetsConfigHolder;
	}
	
	/**
	 * Uses the default cache size
	 * @param iss
	 * @param sss
	 */
	public HighlightingServiceImpl(FacetsConfigHolder facetsConfigHolder, Set<IndexSetting> iss, Set<SearchSetting> sss) {
		this(facetsConfigHolder, iss, sss, DEFAULT_CACHE_SIZE);
	}
	
	@Override
	public String highlightText(String indexName, String queryString,
			String textToHighlight, String startTag, String endTag) throws LuceneHighlightException {
		
		SearchSetting ss = getSearchSetting(indexName);
		if (ss==null)
			throw new LuceneHighlightException("Cannot find search setting for index '" + indexName + "'.");
		
		SearchRequest req = new SearchRequest(indexName, ss.getDefaultSearchField(), queryString);
		
		return highlightText(req, textToHighlight, startTag, endTag);
	}
	
	@Override
	public String highlightText(String indexName, String queryString,
			String textToHighlight) throws LuceneHighlightException {
		
		SearchSetting ss = getSearchSetting(indexName);
		
		return highlightText(indexName, queryString, textToHighlight, 
				ss.getHighlightStartTag(), ss.getHighlightEndTag());
	}
	
	@Override
	public String highlightText(SearchRequest request, String textToHighlight)
			throws LuceneHighlightException {
		
		SearchSetting ss = getSearchSetting(request.getIndexName());
		
		return highlightText(request, textToHighlight, ss.getHighlightStartTag(), ss.getHighlightEndTag());
	}
	
	@Override
	public String highlightText(SearchRequest request,
			String textToHighlight, String startTag, String endTag) throws LuceneHighlightException {
		
		if (textToHighlight==null || textToHighlight.isEmpty())
			return "";
		
		if (request==null || request.getQuerySettings()==null || request.getQuerySettings().isEmpty())
			throw new LuceneHighlightException("Cannot highlight text without search request.");
		
		QuerySetting firstQS = request.getQuerySettings().get(0);
		
		String defaultField = firstQS.getFieldname();
		
		Query q;
		try {
			q = queryCache.get(request);
		} catch (Exception e) {
			throw new LuceneHighlightException("Cannot create query from searchRequest.", e);
		}
		
		Formatter f = createFormatter(startTag, endTag);
		
		Scorer s = new QueryScorer(q);
		
		Encoder e = new DefaultEncoder();
		
		Highlighter hl = new Highlighter(f, e, s);
		
		hl.setTextFragmenter(new NullFragmenter());
		
		String result;
		
		try {
			Analyzer analyzer = IndexingFactory.createPerFieldAnalyzer(getIndexSetting(request.getIndexName()), defaultField);
			result = hl.getBestFragment(analyzer, defaultField, textToHighlight);
		} catch (LuceneIndexException ex) {
			throw new LuceneHighlightException(ex);
		} catch (IOException | InvalidTokenOffsetsException ite) {
			throw new LuceneHighlightException("Cannot get the highlighted text.", ite);
		}
		
		if (result==null || result.isEmpty())
			return textToHighlight;
		
		return result;
	}
	
	@Override
	public String getStartTag(SearchRequest sr) {
		return getStartTag(sr.getIndexName());
	}
	
	@Override
	public String getStartTag(String indexName) {
		try {
			return getSearchSetting(indexName).getHighlightStartTag();
		} catch (Exception e) {
			return "";
		}
	}
	
	@Override
	public String getEndTag(SearchRequest sr) {
		return getEndTag(sr.getIndexName());
	}
	
	@Override
	public String getEndTag(String indexName) {
		try {
			return getSearchSetting(indexName).getHighlightEndTag();
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * Creates a simple html formatter
	 * @param indexName
	 * @return
	 */
	public Formatter createFormatter(String indexName) throws LuceneHighlightException {
		return createFormatter(getSearchSetting(indexName));
	}
	
	/**
	 * 
	 * @param ss
	 * @return
	 */
	public Formatter createFormatter(SearchSetting ss) {
		return createFormatter(ss.getHighlightStartTag(), ss.getHighlightEndTag());
	}
	
	/**
	 * 
	 * @param startTag
	 * @param endTag
	 * @return
	 */
	public Formatter createFormatter(String startTag, String endTag) {
		return new SimpleHTMLFormatter(startTag, endTag);
	}
	
	/**
	 * Creates a new Query, using the QueryFactory
	 * @param req
	 * @return a query. will never 
	 * @throws LuceneHighlightException
	 */
	public Query createQuery(SearchRequest req) throws LuceneHighlightException {
		String indexName = req.getIndexName();
		QueryFactory qf = getQueryFactory(indexName);
		
		List<QuerySetting> qList = req.getQuerySettings();
		
		Query query;
		try {
			query = qf.createQuery(qList);
		} catch (LuceneSearchException e) {
			throw new LuceneHighlightException(e);
		}
		
		return query;
	}
	
	/**
	 * 
	 * @param indexName
	 * @return a query factory
	 * @throws LuceneHighlightException if no setting found
	 */
	protected QueryFactory getQueryFactory(String indexName) throws LuceneHighlightException {
		IndexSetting is = getIndexSetting(indexName);
		SearchSetting ss = getSearchSetting(indexName);
		FacetsConfig config = facetsConfigHolder.getFacetsConfig(indexName);
		if (is==null || ss==null)
			throw new LuceneHighlightException("Cannot find settings for the index '" + indexName + "'.");
		return new QueryFactory(is, ss, config);
	}
	
	protected IndexSetting getIndexSetting(String indexName) {
		return iss.stream().filter(is -> is.getIndexName().equals(indexName)).findAny().orElse(null);
	}
	
	protected SearchSetting getSearchSetting(String indexName) throws LuceneHighlightException {
		return sss.stream().filter(ss -> ss.getIndexName().equals(indexName)).findAny()
				.orElseThrow(() -> new LuceneHighlightException("Cannot find search setting for '" 
										+ indexName + "'."));
	}
	
		
	/**
	 * Static class for cache
	 * @author Joseph
	 *
	 */
	protected static class CacheLinkedHashMap extends LinkedHashMap<SearchRequest, Query> {
		
		private static final long serialVersionUID = 201502111130L;
		
		private final int maxCacheSize;
		
		public CacheLinkedHashMap(int maxSize) {
			super();
			this.maxCacheSize = maxSize;
		}
		
		@Override
		protected boolean removeEldestEntry(Map.Entry<SearchRequest, Query> eldestEntry) {
			return size() > this.maxCacheSize;
		}
		
	}

}
