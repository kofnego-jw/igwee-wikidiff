package at.ac.uibk.igwee.lucene.impl.searching;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;
import at.ac.uibk.igwee.lucene.api.searching.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLEncoder;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.postingshighlight.PostingsHighlighter;

import at.ac.uibk.igwee.lucene.api.Constants;
import at.ac.uibk.igwee.lucene.api.indexing.IndexSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains static method to create SearchResult object.
 *
 * @author Joseph
 */
public class SearchResultFactory {

    /**
     * Default parameter for PostingsHighlighter, should be removed to
     * SearchSetting?
     */
    public static final int DEFAULT_MAX_LENGTH = 300;

    /**
     * Default Prefix for PostingsHighlighter
     */
    private static final String POSTINGSHIGHLIGHTER_PREFIX = "<b>";

    /**
     * Default postix for PostingsHighlighter
     */
    private static final String POSTINGSHIGHLIGHTER_POSTIX = "</b>";

    /**
     * TextFragment Comparator
     */
    private static final TextFragmentComparator TEXTFRAGMENT_COMPARATOR = new TextFragmentComparator();

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultFactory.class);

    /**
     * Adds a facet result to an existing Search result
     *
     * @param result       The existing SearchResult
     * @param taxoReader   TaxonomyReader, will not be closed by this method
     * @param facetsConfig FacetsConfig
     * @param fc           the FacetCollector
     * @param is           IndexSetting, to get the base facets
     * @param ss           SearchSetting
     * @return The same searchresult, enriched with the facets.
     * @throws LuceneSearchException
     */
    public static SearchResult addFacetResults(SearchResult result, TaxonomyReader taxoReader,
                                               FacetsConfig facetsConfig,
                                               FacetsCollector fc, IndexSetting is, SearchSetting ss) throws LuceneSearchException {

        if (result == null)
            return null;
        try {
            if (fc == null) return result;

            Facets facets = new FastTaxonomyFacetCounts(taxoReader, facetsConfig, fc);

            List<FacetSearchResult> facetS = is.getFacetSettings().stream()
                    .map(fs -> {
                        try {
                            return facets.getTopChildren(ss.getMaxFacetListSize(), fs.getDim());
                        } catch (Exception e) {
                            LOGGER.error("Cannot get the FacetResult.", e);
                            return null;
                        }
                    })
                    .filter(fr -> fr != null)
                    .map(SearchResultFactory::convert)
                    .collect(Collectors.toList());

                result.addFacetResults(facetS);
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot parse the FacetResults. ", e);
        }

        return result;
    }

    /**
     * Converts a FacetResult to FacetSearchResult
     * @param fr FacetResult
     * @return a new FacetSearchResult, containing one level old children, or null if fr is null
     */
    public static FacetSearchResult convert(FacetResult fr) {
        if (fr==null) return null;
        final List<String> paths = Arrays.asList(fr.path);
        List<FacetSearchResult> children = Arrays.asList(fr.labelValues)
                .stream()
                .map(lv -> {
                    List<String> childPaths = new ArrayList<>(paths.size() + 1);
                    childPaths.addAll(paths);
                    childPaths.add(lv.label);
                    return new FacetSearchResult(0, fr.dim, Collections.emptyList(), childPaths.toArray(new String[0]), lv.value.doubleValue());
                })
                .collect(Collectors.toList());
        FacetSearchResult result = new FacetSearchResult(fr.childCount, fr.dim, children, fr.path, fr.value.doubleValue());
        return result;
    }

    public static List<FacetContent> listChildrenFacets(FacetResult fr) {
        if (fr==null) return null;
        final List<String> paths = Arrays.asList(fr.path);
        return Arrays.asList(fr.labelValues)
                .stream()
                .map(lv -> {
                    List<String> childrenPath = new ArrayList<>(paths.size()+2);
                    childrenPath.add(fr.dim);
                    childrenPath.addAll(paths);
                    childrenPath.add(lv.label);
                    return new FacetContent(childrenPath);
                })
                .collect(Collectors.toList());
    }

    /**
     * Creates a search Result using the postingshighlighter.
     *
     * @param is      IndexSearcher
     * @param result  The TopDocs
     * @param request The request (will passed into the SearchResult)
     * @param query   the Query (will passed into the SearchResult)
     * @param setting the SearchSetting
     * @param iss     the IndexSetting
     * @return a new SearchResult, using the docs
     * @throws LuceneSearchException
     */
    public static SearchResult createPostingsHighlightSearchResult(
            IndexSearcher is, TopDocs result, SearchRequest request,
            Query query, SearchSetting setting, IndexSetting iss)
            throws LuceneSearchException {

        if (result == null || result.totalHits == 0)
            return emptyResult(request, 0);

        int start = getStart(request);
        if (start > result.scoreDocs.length - 1)
            return emptyResult(request, result.totalHits);

        String[] fieldnames = getFieldNames(setting, iss);

        int[] maxPassages = new int[fieldnames.length];
        for (int i = 0; i < maxPassages.length; i++)
            maxPassages[i] = setting.getResultSetting(fieldnames[i])
                    .getMaxFragNumber();

        PostingsHighlighter hl = new PostingsHighlighter(DEFAULT_MAX_LENGTH);
        Map<String, String[]> highlights = null;
        try {
            highlights = hl.highlightFields(fieldnames, query, is, result,
                    maxPassages);
        } catch (IOException e) {
            throw new LuceneSearchException(
                    "Cannot highlight with PostingsHighlighter. ", e);
        }

        int end = getEnd(request);
        if (end > result.scoreDocs.length)
            end = result.scoreDocs.length;
        List<ResultDocument> list = new ArrayList<ResultDocument>(end - start);
        for (int i = start; i < end; i++) {
            List<ResultField> content = new ArrayList<ResultField>(
                    fieldnames.length);
            for (String fn : fieldnames) {
                if (highlights.get(fn)[i] == null) continue;
                ResultField rf = new ResultField(fn, convertToCustomTags(
                        highlights.get(fn)[i], setting.getHighlightStartTag(),
                        setting.getHighlightEndTag()));
                content.add(rf);
            }
            String id;
            try {
                id = is.doc(result.scoreDocs[i].doc).get(
                        setting.getIdFieldname());
            } catch (IOException e) {
                throw new LuceneSearchException("Cannot read the id field. ", e);
            }

            ResultDocument doc = new ResultDocument(request.getIndexName(), id,
                    content);
            list.add(doc);
        }
        return new SearchResult(request, list, result.totalHits);
    }

    /**
     * This is a hack to replace the standard postlingshighlighter-pre and postfixes with
     * custom prefixes and postfixes.
     * @param postingsHighlighterString the highlighted passage
     * @param prefix the custom prefix
     * @param postix the custom postix
     * @return a highlighted string
     */
    private static String convertToCustomTags(String postingsHighlighterString,
                                              String prefix, String postix) {
        if (postingsHighlighterString == null) return null;
        return postingsHighlighterString.replace(POSTINGSHIGHLIGHTER_PREFIX,
                prefix).replace(POSTINGSHIGHLIGHTER_POSTIX, postix);
    }

    private static String[] getFieldNames(SearchSetting setting,
                                          IndexSetting iss) {
        List<String> res = new ArrayList<String>();
        for (ResultSetting rs : setting.getResultSettings())
            if (rs.getMode() != ResultMode.NEVER
                    && iss.getFieldSetting(rs.getFieldname()).getIndexOption() == Constants.IndexOption.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS)
                res.add(rs.getFieldname());
        return res.toArray(new String[0]);
    }

    private static int getStart(SearchRequest request) {
        return request.getPageNumber() * request.getPageSize();
    }

    private static int getEnd(SearchRequest request) {
        return getStart(request) + request.getPageSize();
    }

    public static SearchResult createSearchResult(IndexSearcher is,
                                                  TopDocs result, SearchRequest request, Query query,
                                                  SearchSetting setting, Analyzer analyzer)
            throws LuceneSearchException {

        if (result == null || result.totalHits == 0)
            return emptyResult(request, 0);

        int start = getStart(request);
        if (start > result.scoreDocs.length - 1)
            return emptyResult(request, result.totalHits);

        int end = getEnd(request);
        if (end > result.scoreDocs.length)
            end = result.scoreDocs.length;

        List<ResultDocument> list = new ArrayList<ResultDocument>(
                request.getPageSize());

        if (result.scoreDocs.length < start)
            return new SearchResult(request, list, result.totalHits);

        for (int i = start; i < end; i++) {
            Document doc = null;
            try {
                doc = is.doc(result.scoreDocs[i].doc);
            } catch (Exception e) {
                throw new LuceneSearchException("Cannot read document number '"
                        + Integer.toString(result.scoreDocs[i].doc) + "'.", e);
            }
            if (doc == null)
                continue;
            ResultDocument sr = createResultDocument(request.getIndexName(),
                    doc, query, setting, analyzer);
            if (sr != null)
                list.add(sr);
        }

        return new SearchResult(request, list, result.totalHits);
    }

    public static ResultDocument createResultDocument(String indexName,
                                                      Document doc, Query query, SearchSetting setting, Analyzer analyzer)
            throws LuceneSearchException {

        String id = doc.get(setting.getIdFieldname());

        List<ResultField> content = new ArrayList<ResultField>(setting
                .getResultSettings().size());

        for (ResultSetting rs : setting.getResultSettings()) {
            ResultField rf = null;
            String prefix = rs.isHighlight() ? setting.getHighlightStartTag()
                    : "";
            String postix = rs.isHighlight() ? setting.getHighlightEndTag()
                    : "";
            switch (rs.getMode()) {
                case ALWAYS:
                    rf = createFulltextResultFieldAlways(rs.getFieldname(),
                            doc.getValues(rs.getFieldname()), query, analyzer, prefix, postix);
                    if (rf != null)
                        content.add(rf);
                    break;
                case FULLTEXT_WHEN_FOUND:
                    rf = createFulltextResultFieldWithNoFragmentation(
                            rs.getFieldname(), doc.getValues(rs.getFieldname()),
                            query, analyzer, prefix, postix);
                    if (rf != null)
                        if (!rs.isHighlight() || hasHighlightedText(rf, prefix))
                            content.add(rf);
                    break;
                case SNIPPET_WHEN_FOUND:
                    rf = createSnippetResultField(rs.getFieldname(),
                            doc.getValues(rs.getFieldname()), query, analyzer,
                            prefix, postix, rs.getFragmentSize(),
                            rs.getMaxFragNumber(), rs.isMergeContiguous(), rs.getFragmenterName());
                    if (rf != null)
                        if (!rs.isHighlight() || hasHighlightedText(rf, prefix))
                            content.add(rf);
                case NEVER:
                default:
                    // Do nothing
            }
        }

        return new ResultDocument(indexName, id, content);

    }

    private static boolean hasHighlightedText(ResultField rf, String startTag) {
        for (String now : rf.getContents())
            if (now.contains(startTag))
                return true;
        return false;
    }

    private static ResultField createResultField(String fieldname,
                                                 String[] contents, Highlighter hl, Analyzer analyzer,
                                                 int maxFragNumber, boolean mergeContiguous, String prefix) throws LuceneSearchException {
        List<String> result = new ArrayList<String>();
        for (String content : contents) {
            List<String> frags = new ArrayList<String>();

            try {
                TokenStream ts = analyzer.tokenStream(fieldname, content);
                TextFragment[] tFrags = hl.getBestTextFragments(ts, content, mergeContiguous, maxFragNumber);
                Arrays.sort(tFrags, TEXTFRAGMENT_COMPARATOR);
                for (TextFragment t : tFrags) {
                    if (t.toString().contains(prefix)) {
                        frags.add(t.toString());
                    }
                }

            } catch (Exception e) {
                throw new LuceneSearchException("Cannot highlight text: '"
                        + content + "'.", e);
            }
            if (!frags.isEmpty())
                for (String now : frags)
                    result.add(now);
        }
        if (result.isEmpty())
            return null;
        return new ResultField(fieldname, result);
    }

    private static ResultField createSnippetResultField(String fieldname,
                                                        String[] contents, Query q, Analyzer analyzer, String prefix,
                                                        String postix, int fragmentSize, int maxFragNumber, boolean mergeContiguous, FragmenterName fName)
            throws LuceneSearchException {
        Highlighter hl = createHighlighter(q, prefix, postix, fragmentSize, fName);
        return createResultField(fieldname, contents, hl, analyzer,
                maxFragNumber, mergeContiguous, prefix);
    }

    /**
     * Creates a ResultField: every content is put in and the highlighter will
     * highlight the field.
     *
     * @param fieldname   fieldname
     * @param contents    the contents one gets with the call for
     *                    Document.getValues(fieldname)
     * @param q           Query
     * @param analyzer    Analyzer used for the document
     * @param prefix      StartTag for highlighter
     * @param postix      Endtag for highlighter
     * @return
     * @throws LuceneSearchException
     */
    private static ResultField createFulltextResultFieldWithNoFragmentation(
            String fieldname, String[] contents, Query q, Analyzer analyzer,
            String prefix, String postix) throws LuceneSearchException {
        Highlighter hl = createHighlighter(q, prefix, postix, 0, FragmenterName.NULL);
        return createResultField(fieldname, contents, hl, analyzer,
                contents.length, true, prefix);

    }

    private static ResultField createFulltextResultFieldAlways(String fieldname, String[] contents, Query q, Analyzer analyzer,
                                                               String prefix, String postix) throws LuceneSearchException {
        Highlighter hl = createHighlighter(q, prefix, postix, 0, FragmenterName.NULL);
        List<String> marked = new ArrayList<>(contents.length);
        for (String now : contents) {
            String mked = null;
            try {
                TokenStream tokenStream = analyzer.tokenStream(fieldname, now);
                mked = hl.getBestFragment(tokenStream, now);
            } catch (Exception e) {
                // Ignore this and add the text as full
            }

            if (mked == null)
                mked = now;
            if (mked != null)
                marked.add(mked);
        }
        return new ResultField(fieldname, marked);
    }


    private static Formatter createFormatter(String prefix, String postix) {
        return new SimpleHTMLFormatter(prefix, postix);
    }

    private static Highlighter createHighlighter(Query q, String prefix,
                                                 String postix, int fragmentSize, FragmenterName fName) {
        QueryScorer scorer = new QueryScorer(q);
        Formatter f = createFormatter(prefix, postix);

        Fragmenter frag;
        if (fragmentSize <= 0) {
            frag = new NullFragmenter();
        } else {
            switch (fName) {
                case TOKENGROUP:
                    frag = new TokenGroupFragmenter(fragmentSize);
                    break;
                case SIMPLE:
                    frag = new SimpleFragmenter(fragmentSize);
                    break;
                case SIMPLE_SPAN:
                    frag = new SimpleSpanFragmenter(scorer, fragmentSize);
                    break;
                case NULL:
                default:
                    frag = new NullFragmenter();
            }
        }
        Encoder enc = new SimpleHTMLEncoder();
        Highlighter hl = new Highlighter(f, enc, scorer);
        hl.setTextFragmenter(frag);
        return hl;
    }

    public static SearchResult emptyResult(SearchRequest request, int totalHits) {
        List<ResultDocument> emptyList = new ArrayList<ResultDocument>(1);
        return new SearchResult(request, emptyList, totalHits);
    }

}
