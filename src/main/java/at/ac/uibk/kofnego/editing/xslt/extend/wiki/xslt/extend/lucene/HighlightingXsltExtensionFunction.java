package at.ac.uibk.kofnego.editing.xslt.extend.wiki.xslt.extend.lucene;

import at.ac.uibk.igwee.lucene.api.highlighting.HighlightingService;
import at.ac.uibk.igwee.lucene.api.searching.SearchRequest;
import at.ac.uibk.igwee.lucene.api.searching.SearchingServiceSerializer;
import at.ac.uibk.igwee.lucene.json.JsonObjectMapper;
import at.ac.uibk.igwee.lucene.json.JsonSearchRequest;
import at.ac.uibk.kofnego.editing.xslt.extend.SaxonHelper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Function implementation for uibk:highlight.
 * The implementation uses a cache: JsonStrings->SearchRequest will be cached
 * @author totoro
 *
 */
public class HighlightingXsltExtensionFunction extends ExtensionFunctionCall {
	
	
	private static final long serialVersionUID = -201502101317L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HighlightingXsltExtensionFunction.class);
	
	/**
	 * Default caching size: 1000
	 */
	private static final long DEFAULT_CACHE_SIZE = 1000;
	
	/**
	 * Default caching time: 300 (seconds)
	 */
	private static final long DEFAULT_CACHING_TIME = 300;

	private static final String HIGHLIGHT_START_TAG = "<HIGHLIGHT>";

    private static final String HIGHLIGHT_END_TAG =   "</HIGHLIGHT>";

	private static final Pattern HIGHLIGHT_PATTERN = Pattern.compile("<HIGHLIGHT>(.*?)</HIGHLIGHT>");
	
	/**
	 * highlighting service
	 */
	private final HighlightingService highlightingService;
	
	/**
	 * Cache for Json String to SearchRequest
	 */
	private LoadingCache<String,SearchRequest> cache;
	
	
	public HighlightingXsltExtensionFunction(HighlightingService hs) {
		super();
		this.highlightingService = hs;
		this.cache =  CacheBuilder.newBuilder().maximumSize(DEFAULT_CACHE_SIZE)
				.expireAfterAccess(DEFAULT_CACHING_TIME, TimeUnit.SECONDS).build(
						new CacheLoader<String, SearchRequest>() {
							@Override
							public SearchRequest load(String key) throws Exception {
                                SearchRequest sr;
								try {
                                    sr = SearchingServiceSerializer.json2searchRequest(key);
                                } catch (Exception e) {
                                    // Do nothing, perhaps using JsonSearchRequest.
                                    sr = null;
                                }
                                if (sr!=null) return sr;
								JsonSearchRequest jsr = JsonObjectMapper.jsonToSearchRequest(key);
								if (jsr==null) {
									throw new Exception("Cannot convert.");
								}
								return ConversionUtils.toSearchRequest(jsr);
							}
						}
					);
	}
	
	/**
	 * Try the cache. If the jsonString isn't there, a searchRequest will be created.
	 * @param jsonString
	 * @return
	 */
	private SearchRequest getOrCreateSearchRequest(String jsonString) throws XPathException {
		
		try {
			return cache.get(jsonString);
		} catch (Exception e) {
			LOGGER.error("Cannot create searchRequest from jsonString.", e);
			throw new XPathException("Cannot create searchRequest from jsonString.", e);
		}
	}
	
	@Override
	public Sequence call(XPathContext context, Sequence[] arguments)
			throws XPathException {

		if (arguments.length<1)
			return convertToSequence("");

		String toHighlight = arguments[0].iterate().next().getStringValue();
		
		if (arguments.length<2)
			return convertToSequence(toHighlight);
		
		String jsonString = arguments[1].iterate().next().getStringValue();
		
		SearchRequest sr = getOrCreateSearchRequest(jsonString);
		
		if (sr==null) {
			throw new XPathException("Cannot deserialize the query.");
		}
		
		String highlighted = null;
		try {
			highlighted = highlightingService.highlightText(sr, toHighlight,
					HIGHLIGHT_START_TAG, HIGHLIGHT_END_TAG);


		} catch (Exception e) {
			throw new XPathException("Exception while highlighting a text passage.", e);
		}
		
		return convertToSequence(highlighted);
	}
	
	/**
	 * Makes an endtag out of starttag. if start tag starts with "&lt;", then 
	 * the end tag will be "&lt;/" + starttag. similiar if one uses [ instead of &lt;.
	 * Otherwise, the endtag will just be "/" + starttag.
	 * @param startTag
	 * @return
	 */
	public static String makeEndTag(String startTag) {
		Pattern pat = Pattern.compile("^(<|\\[+)(.*)$");
		Matcher mat = pat.matcher(startTag);
		
		if (mat.find()) {
			return mat.group(1) + "/" + mat.group(2);
		}
		return "/" + startTag;
	}
	
	/**
	 * Converts a string into a single string value sequence.
	 * @param content
	 * @return
	 */
	public static Sequence convertToSequence(String content) throws XPathException {
        List<String> result = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
        Matcher mat = HIGHLIGHT_PATTERN.matcher(content);
        while (mat.find()) {
            mat.appendReplacement(sb, "");
            result.add("0" + sb.toString());
            sb.setLength(0);
            result.add("+" + mat.group(1));
        }
        mat.appendTail(sb);
        result.add("0" + sb.toString());

        return SaxonHelper.createListSequenceIterator(result);
	}
	
}
