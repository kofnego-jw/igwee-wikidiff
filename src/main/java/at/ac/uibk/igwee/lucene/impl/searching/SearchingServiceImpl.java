package at.ac.uibk.igwee.lucene.impl.searching;

import at.ac.uibk.igwee.lucene.api.Constants;
import at.ac.uibk.igwee.lucene.api.LuceneException;
import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;
import at.ac.uibk.igwee.lucene.api.indexing.IndexSetting;
import at.ac.uibk.igwee.lucene.api.searching.*;
import at.ac.uibk.igwee.lucene.impl.FacetsConfigHolder;
import at.ac.uibk.igwee.lucene.impl.LuceneApplication;
import at.ac.uibk.igwee.lucene.impl.LuceneDirHolder;
import at.ac.uibk.igwee.lucene.impl.indexing.IndexingFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main implementation of the SearchingService
 *
 * @author Joseph
 */
public class SearchingServiceImpl implements SearchingService, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchingServiceImpl.class);

    private static final int CACHE_POOL_SIZE = 10;

    private Map<String, QueryFactory> queryFactories = new LinkedHashMap<>();

    private LinkedList<CacheEntry> indexReaderCache = new LinkedList<>();

    private LinkedList<TaxonomyReaderCacheEntry> taxonomyReaderCache = new LinkedList<>();

    private Set<IndexSetting> indexSettings;
    private Set<SearchSetting> searchSettings;
    private LuceneDirHolder dirHolder;
    private FacetsConfigHolder facetsConfigHolder;

    public SearchingServiceImpl(LuceneDirHolder holder,
                                FacetsConfigHolder facetsConfigHolder,
                                Set<IndexSetting> iss,
                                Set<SearchSetting> settings) {
        super();
        this.dirHolder = holder;
        this.indexSettings = iss;
        this.searchSettings = settings;
        this.facetsConfigHolder = facetsConfigHolder;
    }

    @Override
    public void close() {
        while (indexReaderCache.size() > 0) {
            CacheEntry ce = indexReaderCache.removeLast();
            try {
                ce.getDirectoryReader().close();
            } catch (Exception ignored) {
                LOGGER.warn(
                        "Exception while closing DirectoryReader '"
                                + ce.getIndexName() + "'.", ignored);
            }
        }
        while (taxonomyReaderCache.size() > 0) {
            TaxonomyReaderCacheEntry ce = taxonomyReaderCache.removeLast();
            try {
                ce.getTaxonomyReader().close();
            } catch (Exception e) {
                LOGGER.warn("Exception while closing TaxonomyReader '" + ce.getTaxonomyIndexName() + "'.", e);
            }
        }
    }

    private FacetsConfig getFacetsConfig(String indexname) {
        return facetsConfigHolder.getFacetsConfig(indexname);
    }

    @Override
    public Set<String> getAllIndexNames() {
        Set<String> result = this.searchSettings.stream().map(SearchSetting::getIndexName).collect(Collectors.toSet());
        return result;
    }

    @Override
    public Set<String> getSearchableFields(String indexName) {
        SearchSetting ss = getSearchSetting(indexName);
        Set<String> result = new HashSet<>();
        if (ss == null || ss.getResultSettings() == null) return result;
        result.addAll(ss.getResultSettings().stream().map(ResultSetting::getFieldname).collect(Collectors.toList()));
        return result;
    }

    @Override
    public SearchResult search(String indexName, String query)
            throws LuceneSearchException {
        SearchSetting ss = getSearchSetting(indexName);
        if (ss == null) {
            throw new LuceneSearchException(
                    "Cannot find setting for the index '" + indexName + "' in " + dirHolder.getBaseDir() + ".");
        }
        SearchRequest req = new SearchRequest(indexName,
                ss.getDefaultSearchField(), query);
        return search(req);
    }

    @Override
    public SearchResult search(SearchRequest request)
            throws LuceneSearchException {

        if (request == null) {
            throw new LuceneSearchException("Null request.");
        }

        SearchSetting setting = getSearchSetting(request.getIndexName());
        IndexSetting iss = getIndexSetting(request.getIndexName());

        FacetsConfig facetsConfig = getFacetsConfig(request.getIndexName());

        LOGGER.info("Performing the search request: {}", request.toString());

        QueryFactory qf = getQueryFactory(request.getIndexName());
        if (qf == null) {
            throw new LuceneSearchException("No QueryFactory created.");
        }
        Sort s = qf.createSort(request.getSortRules());
        Query q = qf.createQuery(request);

        IndexSearcher is = getIndexSearcher(request.getIndexName());

        TaxonomyReader tr = facetsConfig==null ? null : getTaxonomyReader(request.getIndexName());
        int n = (request.getPageNumber() + 1) * request.getPageSize();
        TopDocs tds;
        try {
            tds = s == null ? is.search(q, n) : is.search(q, n, s);
        } catch (Exception e) {
            LOGGER.error("Cannot perform the search.", e);
            throw new LuceneSearchException("Cannot perform the search.", e);
        }

        FacetsCollector facetsCollector = null;
        if (tr!=null) {
            facetsCollector = new FacetsCollector();
            try {
                is.search(q, facetsCollector);
            } catch (Exception e) {
                LOGGER.error("Cannot perform the faceted search.", e);
                throw new LuceneSearchException("Cannot perform the faceted search.", e);
            }
        }

        LOGGER.info("Found total {} document(s).", tds.totalHits);

        Analyzer analyzer;
        try {
            analyzer = IndexingFactory
                    .createPerFieldAnalyzer(getIndexSetting(request
                            .getIndexName()));
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot create for '"
                    + request.getIndexName() + "'.", e);
        }

        SearchResult result = (setting.isUsePostingsHighlighter()) ?
                SearchResultFactory.createPostingsHighlightSearchResult(is,
                        tds, request, q, setting, iss) :
                SearchResultFactory.createSearchResult(is, tds,
                        request, q, setting, analyzer);

        return (facetsCollector != null) ? SearchResultFactory.addFacetResults(result, tr, facetsConfig,
                facetsCollector, iss, setting) : result;
    }

    @Override
    public List<TermDocs> getTermDocs(String indexName, String field) throws LuceneSearchException {

        DirectoryReader dr = getDirectoryReader(indexName);
        List<TermDocs> result = new ArrayList<>();

        try {
            TermsEnum termsEnum;
            Fields fields = MultiFields.getFields(dr);
            Terms terms = fields.terms(field);
            termsEnum = terms.iterator();
            BytesRef ref;
            String term;
            PostingsEnum postingsEnum = null;
            List<String> docIds = new ArrayList<>();
            int docNum;

            while ((ref = termsEnum.next()) != null) {
                term = ref.utf8ToString();
                postingsEnum = termsEnum.postings(postingsEnum);
                docIds.clear();
                while ((docNum = postingsEnum.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
                    Document doc = dr.document(docNum);
                    docIds.add(doc.get(Constants.DOC_ID_DEFAULT_FIELDNAME));
                }
                result.add(new TermDocs(field, term, docIds));
            }

        } catch (Exception e) {
            LOGGER.error("Cannot enlist TermDocs for index '" + indexName + "' and field '" + field + "'.", e);
            throw new LuceneSearchException("Cannot enlist TermDocs for index '" + indexName + "' and field '" + field + "'.", e);
        }

        return result;
    }

    @Override
    public List<TermFrequency> getIndexedTerms(String indexName, String field) throws LuceneSearchException {

        DirectoryReader dr = getDirectoryReader(indexName);
        List<TermFrequency> result = new ArrayList<>();
        try {
            TermsEnum te;
            Fields fields = MultiFields.getFields(dr);
            Terms terms = fields.terms(field);
            te = terms.iterator();
            BytesRef bytesRef;
            while ((bytesRef = te.next()) != null) {
                String t = bytesRef.utf8ToString();
                int docCount = te.docFreq();
                long totalCount = te.totalTermFreq();
                result.add(new TermFrequency(indexName, field, t, docCount, totalCount));
            }
        } catch (Exception e) {
            StringBuilder error = new StringBuilder();
            error.append("Cannot list all terms in index '").append(indexName).append("' for the field '").append(field)
                    .append("'.");
            LOGGER.error(error.toString(), e);
            throw new LuceneSearchException(error.toString(), e);
        }

        return result;
    }

    @Override
    public FacetSearchResult listBaseFacets(String indexName) throws LuceneSearchException {
        LOGGER.info("List all base facets for {}.", indexName);
        IndexSetting is = getIndexSetting(indexName);
        if (is==null) {
            LOGGER.error("Cannot find indexSetting for {}.", indexName);
            throw new LuceneSearchException("Cannot find indexSetting for index '" + indexName + "'.");
        }
        if (is.getFacetSettings()==null || is.getFacetSettings().isEmpty()) {
            LOGGER.error("No FacetSetting found for index {}.", indexName);
            throw new LuceneSearchException("No facetSetting found for index '" + indexName + "'.");
        }
        Map<FacetContent, Double> subFacets = is.getFacetSettings().stream()
                .collect(Collectors.toMap(facetSetting -> new FacetContent(facetSetting.getDim()), facetSetting -> -1D));

        return new FacetSearchResult(subFacets.size(), FacetContent.ROOT_FACET_CONTENT, subFacets, -1D);
    }

    @Override
    public FacetSearchResult listSubCategories(String indexName,
                                               FacetContent baseFacet, int level) throws LuceneSearchException {
        LOGGER.info("List sub categories for index '{}', facet '{}', level '{}'.",
                indexName, baseFacet, level);

        if (baseFacet == null || baseFacet.getComponents() == null || baseFacet.getComponents().isEmpty()) {
            LOGGER.warn("No base facet provided, return the base facets instead. Ignoring level.");
            return listBaseFacets(indexName);
        }

        IndexSearcher searcher = getIndexSearcher(indexName);
        TaxonomyReader tr = getTaxonomyReader(indexName);
        if (tr == null) {
            LOGGER.error("No taxonomy reader available for {}.", indexName);
            throw new LuceneSearchException("No taxonomy reader available for " + indexName + ".");
        }


        FacetsCollector collector = new FacetsCollector();
        Facets facets;
        FacetResult base;
        try {
            searcher.search(new MatchAllDocsQuery(), collector);
            facets = new FastTaxonomyFacetCounts(tr, getFacetsConfig(indexName), collector);
            base = facets.getTopChildren(Integer.MAX_VALUE, baseFacet.getDimension(), baseFacet.getPath());
        } catch (Exception e) {
            LOGGER.error("Cannot make facet query.", e);
            throw new LuceneSearchException("Cannot make facet query.", e);
        }

        FacetSearchResult cp = SearchResultFactory.convert(base);


        return addChildren(cp, facets, 1, level);
    }

    protected FacetSearchResult addChildren(FacetSearchResult now, Facets facets, int levelNow, int level) throws LuceneSearchException {
        if (levelNow==level) return now;
        try {
            List<FacetSearchResult> newChildren = new ArrayList<>(now.getChildCount());
            for (FacetSearchResult child: now.getChildren()) {
                FacetResult topGrandChildren = facets.getTopChildren(Integer.MAX_VALUE, child.getDimension(), child.getPath());
                if (topGrandChildren==null) {
                    newChildren.add(child);
                    continue;
                }
                FacetSearchResult childResult = SearchResultFactory.convert(topGrandChildren);
                if (levelNow < level) {
                    childResult = addChildren(childResult, facets, levelNow+1, level);
                }
                newChildren.add(childResult);
            }
            now.getChildren().clear();
            now.getChildren().addAll(newChildren);
            return now;
        } catch (Exception e) {
            LOGGER.error("Cannot add children.", e);
            throw new LuceneSearchException("Cannot add children.", e);
        }
    }

    private IndexSetting getIndexSetting(String indexName) {
        for (IndexSetting is : indexSettings)
            if (is.getIndexName().equals(indexName))
                return is;
        return null;
    }

    @Override
    public SearchSetting getSearchSetting(String indexName) {
        for (SearchSetting ss : searchSettings)
            if (ss.getIndexName().equals(indexName))
                return ss;
        return null;
    }

    private QueryFactory getQueryFactory(String indexName) {
        for (Map.Entry<String, QueryFactory> now : queryFactories.entrySet()) {
            if (now.getKey().equals(indexName)) {
                return now.getValue();
            }
        }
        IndexSetting is = getIndexSetting(indexName);
        SearchSetting ss = getSearchSetting(indexName);
        if (is == null)
            return null;
        QueryFactory newQF = new QueryFactory(is, ss, getFacetsConfig(indexName));
        this.queryFactories.put(indexName, newQF);
        return newQF;
    }

    private IndexSearcher getIndexSearcher(String indexName)
            throws LuceneSearchException {
        DirectoryReader dr;
        try {
            dr = getDirectoryReader(indexName);
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot open IndexSearcher for '"
                    + indexName + "'.", e);
        }
        return new IndexSearcher(dr);
    }

    private TaxonomyReader getTaxonomyReader(String indexName)
            throws LuceneSearchException {
        String taxDir = indexName
                + LuceneApplication.TAXONOMY_DIRECTORY_DEFAULT_ENDING;

        for (TaxonomyReaderCacheEntry entry : this.taxonomyReaderCache) {
            if (entry.getTaxonomyIndexName().equals(taxDir))
                return entry.getTaxonomyReader();
        }

        TaxonomyReader tr;
        try {
            tr = createTaxonomyReader(taxDir);
        } catch (Exception e) {
            System.out.println("Blah");
            tr = null;
        }

        if (tr != null) {
            TaxonomyReaderCacheEntry tce = new TaxonomyReaderCacheEntry(taxDir,
                    tr);
            this.taxonomyReaderCache.add(tce);
            if (this.taxonomyReaderCache.size() > CACHE_POOL_SIZE) {
                TaxonomyReaderCacheEntry toDel = this.taxonomyReaderCache
                        .remove();
                TaxonomyReader trToClose = toDel.getTaxonomyReader();
                try {
                    trToClose.close();
                } catch (Exception e) {
                    LOGGER.warn("Exception while closing TaxonomyReader '"
                            + toDel.getTaxonomyIndexName() + "'.", e);
                }
            }
            return tce.getTaxonomyReader();
        }
        return null;
    }

    private DirectoryReader getDirectoryReader(String indexName)
            throws LuceneSearchException {
        CacheEntry entry = null;
        for (CacheEntry ce : indexReaderCache)
            if (ce.getIndexName().equals(indexName))
                entry = ce;
        if (entry == null) {
            DirectoryReader ir = createDirectoryReader(indexName);
            entry = new CacheEntry(indexName, ir);
            this.indexReaderCache.add(entry);
            if (indexReaderCache.size() > CACHE_POOL_SIZE) {
                CacheEntry toDel = indexReaderCache.remove();
                try {
                    DirectoryReader drToClose = toDel.getDirectoryReader();
                    drToClose.close();
                } catch (Exception e) {
                    LOGGER.warn("Exception while closing a DirectoryReader '"
                            + toDel.getIndexName() + "'.", e);
                }
            }
        }
        DirectoryReader dr = entry.getDirectoryReader();
        DirectoryReader changed = null;
        try {
            changed = DirectoryReader.openIfChanged(dr);
        } catch (Exception e) {
            throw new LuceneSearchException(
                    "Exception while reopening the index.", e);
        }
        if (changed != null) {
            try {
                dr.close();
            } catch (Exception e) {
                LOGGER.error("Cannot close the DirectoryReader for '"
                        + indexName + "'.", e);
            } finally {
                dr = changed;
                entry.setDirectoryReader(dr);
            }
        }
        return dr;
    }

    private TaxonomyReader createTaxonomyReader(String taxDirName)
            throws LuceneSearchException {
        Directory d = getDirectory(taxDirName);
        try {
            return new DirectoryTaxonomyReader(d);
        } catch (Exception e) {
            LOGGER.info("Cannot create TaxonomyReader for '" + taxDirName + "'.", e);
//			throw new LuceneSearchException(
//					"Cannot create TaxonomyReader for '" + taxDirName + "'.", e);
        }
        return null;
    }

    private DirectoryReader createDirectoryReader(String indexName)
            throws LuceneSearchException {
        Directory d = getDirectory(indexName);
        if (d == null)
            return null;
        try {
            return DirectoryReader.open(d);
        } catch (Exception e) {
            throw new LuceneSearchException("Cannot create IndexReader: "
                    + e.getMessage(), e);
        }
    }

    private Directory getDirectory(String dirName) throws LuceneSearchException {
        Directory d = null;
        try {
            d = dirHolder.getDirectory(dirName);
        } catch (LuceneException e) {
            throw new LuceneSearchException("Cannot get the directory '"
                    + dirName + "'.", e);
        }
        return d;
    }

    private static final class TaxonomyReaderCacheEntry {
        private final String taxonomyIndexName;
        private final TaxonomyReader taxonomyReader;

        protected TaxonomyReaderCacheEntry(String taxonomyIndexName,
                                           TaxonomyReader tr) {
            super();
            this.taxonomyIndexName = taxonomyIndexName;
            this.taxonomyReader = tr;
        }

        /**
         * @return the taxonomyIndexName
         */
        public String getTaxonomyIndexName() {
            return taxonomyIndexName;
        }

        /**
         * @return the taxonomyReader
         */
        public TaxonomyReader getTaxonomyReader() {
            return taxonomyReader;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime
                    * result
                    + ((taxonomyIndexName == null) ? 0 : taxonomyIndexName
                    .hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TaxonomyReaderCacheEntry other = (TaxonomyReaderCacheEntry) obj;
            if (taxonomyIndexName == null) {
                if (other.taxonomyIndexName != null)
                    return false;
            } else if (!taxonomyIndexName.equals(other.taxonomyIndexName))
                return false;
            return true;
        }

    }

    /**
     * Cache for DirectoryReaders.
     *
     * @author Joseph
     */
    private static final class CacheEntry {
        private String indexName;
        private DirectoryReader directoryReader;

        protected CacheEntry(String indexName, DirectoryReader reader) {
            this.indexName = indexName;
            this.directoryReader = reader;
        }

        /**
         * @return the indexName
         */
        public String getIndexName() {
            return indexName;
        }

        /**
         * @return the indexReader
         */
        public DirectoryReader getDirectoryReader() {
            return directoryReader;
        }

        /**
         * @param indexReader the indexReader to set
         */
        public void setDirectoryReader(DirectoryReader indexReader) {
            this.directoryReader = indexReader;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((indexName == null) ? 0 : indexName.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheEntry other = (CacheEntry) obj;
            if (indexName == null) {
                if (other.indexName != null)
                    return false;
            } else if (!indexName.equals(other.indexName))
                return false;
            return true;
        }

    }

}
