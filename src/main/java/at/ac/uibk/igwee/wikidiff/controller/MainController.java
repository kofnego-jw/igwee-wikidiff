package at.ac.uibk.igwee.wikidiff.controller;

import at.ac.uibk.igwee.lucene.api.indexing.FieldContent;
import at.ac.uibk.igwee.lucene.api.indexing.IndexingService;
import at.ac.uibk.igwee.lucene.api.searching.SearchRequest;
import at.ac.uibk.igwee.lucene.api.searching.SearchResult;
import at.ac.uibk.igwee.lucene.api.searching.SearchingService;
import at.ac.uibk.igwee.lucene.impl.LuceneApplication;
import at.ac.uibk.igwee.lucene.rest.helper.ConversionUtil;
import at.ac.uibk.igwee.lucene.rest.helper.SearchRequestFW;
import at.ac.uibk.igwee.lucene.rest.helper.SearchResultFW;
import at.ac.uibk.igwee.metadata.httpclient.HttpClientService;
import at.ac.uibk.igwee.wikidiff.WikiDiffException;
import at.ac.uibk.igwee.wikidiff.lucene.LuceneApplicationController;
import at.ac.uibk.igwee.wikidiff.lucene.LuceneDocList;
import at.ac.uibk.igwee.wikidiff.lucene.LuceneField;
import at.ac.uibk.igwee.wikidiff.web.DiffFW;
import at.ac.uibk.igwee.wikidiff.web.RevisionFWList;
import at.ac.uibk.kofnego.editing.xslt.extend.ExtendedXsltService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main Program entry point.
 *
 * Created by Joseph on 03.06.2016.
 */
@Service
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    /**
     * String used to test the right version of WikiPedia Revision File
     */
    private static final String TEST_STRING = "http://www.mediawiki.org/xml/export-0.10/";

    /**
     * String used to validate the ingest. Ingest must contain "<revision"
     */
    private static final String REVISION_TAG = "<revision";

    /**
     * Default location to save the revision file "./wiki.xml"
     */
    private static final String DEFAULT_XML_FILE_LOCATION = "./wiki.xml";

    /**
     * "/xslt/toRevisionFWList.xsl"
     */
    private static final String TO_REVISIONLIST_JSON_XSLT = "/xslt/toRevisionFWList.xsl";

    /**
     * "xslt/toHTML.xsl"
     */
    private static final String TO_HTML_XSLT = "/xslt/toHTML.xsl";

    /**
     * "/xslt/userExportHtml.xsl"
     */
    private static final String USER_HTML_XSLT = "/xslt/userExportHtml.xsl";

    /**
     * "revisionID"
     */
    private static final String REVISION_ID = "revisionID";

    /**
     * "userID"
     */
    private static final String CONTRIBUTOR_ID = "userID";

    /**
     * "/xslt/diffHTML.xsl"
     */
    private static final String DIFF_HTML_XSLT = "/xslt/diffHTML.xsl";

    /**
     * "/xslt/toDiffFW.xsl"
     */
    private static final String DIFFFW_XSLT = "/xslt/toDiffFW.xsl";

    /**
     * "revisionID1"
     */
    private static final String REVISION_ID1 = "revisionID1";
    /**
     * "revisionID2"
     */
    private static final String REVISION_ID2 = "revisionID2";

    /**
     * "wikidiff"
     */
    private static final String LUCENE_INDEX_NAME = "wikidiff";

    /**
     * "/xslt/toLuceneDocs.xsl"
     */
    private static final String TO_LUCENE_DOCS = "/xslt/toLuceneDocs.xsl";

    /**
     * "/startFile.xml"
     */
    private static final String START_FILE_PATH = "/startFile.xml";

    /**
     * Jackson ObjectMapper
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * WikiDownloadService
     */
    @Autowired
    private WikiDownloadService wikiDownloadService;

    /**
     * ExtendedXsltService
     */
    @Autowired
    private ExtendedXsltService xsltService;

    /**
     * LuceneApplicationController
     */
    @Autowired
    private LuceneApplicationController luceneApplicationController;

    /**
     * default to DEFAULT_XML_FILE_LOCATION, it was meant to be changable, but was given up in favor
     * download  from Wiki
     */
    private final File xmlFile = new File(DEFAULT_XML_FILE_LOCATION);

    /**
     * The cache for the content
     */
    private byte[] xmlIS = null;

    /**
     * Lucene Application
     */
    private LuceneApplication luceneApplication;

    public MainController() throws WikiDiffException {
        byte[] content;
        try {
            if (!xmlFile.exists()) {
                LOGGER.info("Copy start file.");
                InputStream in = getClass().getResourceAsStream(START_FILE_PATH);
                content = IOUtils.toByteArray(in);
                FileUtils.writeByteArrayToFile(xmlFile, content);
            } else {
                content = FileUtils.readFileToByteArray(xmlFile);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot copy start file.", e);
            throw new WikiDiffException("Cannot read the start file.", e);
        }
        xmlIS = content;
    }

    /**
     * Starts indexing revision after construction
     * @throws WikiDiffException
     */
    @PostConstruct
    public void indexStartFile() throws WikiDiffException {
        if (xmlIS!=null && luceneApplicationController!=null) {
            fillTransformerCache();
            doIndexing();
        } else {
            LOGGER.warn("Cannot find content to be indexed.");
            LOGGER.warn("xmlIS: {}", xmlIS);
            LOGGER.warn("luceneApplicationController: {}", luceneApplicationController);
            throw new WikiDiffException("Cannot find content to be indexed.");
        }
    }

    /**
     * Cleanup method for luceneApplication
     */
    @PreDestroy
    public void cleanUp() {
        this.luceneApplication = null;
    }

    /**
     * @throws WikiDiffException
     */
    private void initLuceneApplication() throws WikiDiffException {
        if (luceneApplication==null) {
            if (luceneApplicationController==null) {
                throw new WikiDiffException("LuceneApplicationController is null, cannot get the lucene application.");
            }
            luceneApplication = luceneApplicationController.getLuceneApplication();
        }
    }

    private void fillTransformerCache() throws WikiDiffException {
        addToXslCache(TO_HTML_XSLT);
        addToXslCache(TO_REVISIONLIST_JSON_XSLT);
        addToXslCache(TO_LUCENE_DOCS);
        addToXslCache(DIFFFW_XSLT);
        addToXslCache(DIFF_HTML_XSLT);
    }

    private void addToXslCache(String classPath) throws WikiDiffException {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(classPath);
            xsltService.addToXslCache(classPath, in);
        } catch (Exception e) {
            throw new WikiDiffException("Cannot add to xslt cache.", e);
        } finally {
            if (in!=null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                    LOGGER.warn("Exception while closing an inputStream '" + classPath + "'.", ignored);
                }
            }
        }
    }

    private void doIndexing() throws WikiDiffException {
        LOGGER.debug("Start indexing documents.");
        if (xmlIS!=null) {
            initLuceneApplication();
            LuceneDocList luceneDocList;
            try {
                InputStream in = transform(TO_LUCENE_DOCS, null);
                luceneDocList = OBJECT_MAPPER.readValue(in, LuceneDocList.class);
            } catch (Exception e) {
                LOGGER.error("Cannot index the content.", e);
                throw new WikiDiffException("Cannot index the content.", e);
            }
            try {
                IndexingService is = luceneApplication.getIndexingService();
                is.clearIndex(LUCENE_INDEX_NAME);
                luceneDocList.getLuceneDocs().forEach(x -> {
                    try {
                        is.doIndex(LUCENE_INDEX_NAME, x.docId, toFieldContents(x.contents));
                    } catch (Exception e) {
                        LOGGER.warn("Exception while indexing a document.", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Cannot index the lucene document.", e);
                throw new WikiDiffException("Cannot index the lucene document.", e);
            }
        } else {
            throw new WikiDiffException("No content to be indexed.");
        }
    }

    public SearchResultFW doSearching(SearchRequest request) throws WikiDiffException {
        LOGGER.info("Do searching:  {}");
        initLuceneApplication();
        if (request==null) {
            LOGGER.warn("No request passed for search.");
            throw new WikiDiffException("No request passed for search.");
        }
        request.setIndexName(LUCENE_INDEX_NAME);
        try {
            SearchingService ss = luceneApplication.getSearchingService();
            return toFW(ss.search(request));
        } catch (Exception e) {
            LOGGER.error("Cannot do searching.", e);
            throw new WikiDiffException("Cannot do searching.", e);
        }
    }

    /**
     * Search within the LuceneIndex
     * @param requestFW the SearchRequest
     * @return a SearchResultFW
     * @throws WikiDiffException if any exception happens
     */
    public SearchResultFW doSearching(SearchRequestFW requestFW) throws WikiDiffException {
        SearchRequest req = ConversionUtil.toSearchRequest(LUCENE_INDEX_NAME, requestFW);
        return doSearching(req);
    }

    private SearchResultFW toFW(SearchResult sr) {
        return ConversionUtil.toSearchResultFW(sr);
    }

    /**
     * Sets the content of the XML Revision file.
     * @param bytes the content in bytes of the XML Revision File.
     * @throws WikiDiffException if the file is empty or if the file does not seem to be a Wikipedia Export file.
     *                           This exception is also thrown if anything happens while indexing the file.
     */
    public void setXmlFile(byte[] bytes) throws WikiDiffException {
        LOGGER.info("XML File will be set with new content.");
        if (bytes==null || bytes.length==0) {
            throw new WikiDiffException("File is empty.");
        }
        if (!seemsToBeWikiRevision(bytes)) {
            throw new WikiDiffException("The file does not seem to be a wikipedia export file.");
        }
        try {
            xmlIS = bytes;
            FileUtils.writeByteArrayToFile(xmlFile, xmlIS);
        } catch (Exception e) {
            LOGGER.error("Cannot upload xml file.", e);
            throw new WikiDiffException("Cannot upload XML file.",e);
        }
        LOGGER.info("Upload complete.");
        doIndexing();
    }

    /**
     *
     * @return the XML File
     */
    public File getXmlFile() {
        return xmlFile;
    }

    /**
     *
     * @return a RevisionFWList of the revisions in the current XML File
     * @throws WikiDiffException
     */
    public RevisionFWList listRevision() throws WikiDiffException {
        LOGGER.debug("List revisions");
        InputStream result = transform(TO_REVISIONLIST_JSON_XSLT, null);
        try {
            RevisionFWList revisionFWList = OBJECT_MAPPER.readValue(result, RevisionFWList.class);
            revisionFWList.calculateContributors();
            return revisionFWList;
        } catch (Exception e) {
            LOGGER.error("Cannot deserialize JSON to RevisionFWList.", e);
            throw new WikiDiffException("Cannot deserialize JSON to RevisionFWList.", e);
        }
    }

    /**
     *
     * @param revisionId the revision ID of the revision to be viewed as HTML
     * @return a byte[] containing the result of XML to HTML transformation
     * @throws WikiDiffException
     */
    public byte[] toHtml(String revisionId) throws WikiDiffException {
        LOGGER.debug("View revision {}.", revisionId);
        Map<String,Object> params = new HashMap<>();
        params.put(REVISION_ID, revisionId);
        InputStream result = transform(TO_HTML_XSLT, params);
        try {
            return IOUtils.toByteArray(result);
        } catch (Exception e) {
            LOGGER.error("Cannot convert InputStream to ByteArray.", e);
            throw new WikiDiffException("Cannot convert InputStream to ByteArray.", e);
        }
    }

    /**
     *
     * @param userId the user id
     * @return a HTML view of all revisions done by the user with the id.
     * @throws WikiDiffException
     */
    public byte[] userRevisions(String userId) throws WikiDiffException {
        LOGGER.debug("View revisions of contributor {}.", userId);
        Map<String,Object> params = new HashMap<>();
        params.put(CONTRIBUTOR_ID, userId);
        InputStream result = transform(USER_HTML_XSLT, params);
        try {
            return IOUtils.toByteArray(result);
        } catch (Exception e) {
            LOGGER.error("Cannot convert InputStrem to ByteArray.", e);
            throw new WikiDiffException("Cannot convert InputStream to ByteArray.", e);
        }
    }

    /**
     *
     * @param revId1 the ID of the first revision
     * @param revId2 the ID of the second revision
     * @return a HTML view of both revisions, including the diff.
     * @throws WikiDiffException
     */
    public byte[] diffHtml(String revId1, String revId2) throws WikiDiffException {
        LOGGER.debug("Diff revision {} with {}.", revId1, revId2);
        Map<String,Object> params = new HashMap<>();
        params.put(REVISION_ID1, revId1);
        params.put(REVISION_ID2, revId2);
        InputStream result = transform(DIFF_HTML_XSLT, params);
        try {
            return IOUtils.toByteArray(result);
        } catch (Exception e) {
            LOGGER.error("Cannot convert InputStream to ByteArray.", e);
            throw new WikiDiffException("Cannot convert InputStream to ByteArrays.", e);
        }
    }

    /**
     *
     * @param revId1 the first revision ID to be compared
     * @param revId2 the second revision ID to be compared
     * @return a DiffFW of the comparison.
     * @throws WikiDiffException
     */
    public DiffFW diffFW(String revId1, String revId2) throws WikiDiffException {
        LOGGER.debug("Diff revision {} with {} using FW.", revId1, revId2);
        Map<String,Object> params = new HashMap<>();
        params.put(REVISION_ID1, revId1);
        params.put(REVISION_ID2, revId2);
        InputStream result = transform(DIFFFW_XSLT, params);
        try {
            DiffFW diffFW = OBJECT_MAPPER.readValue(result, DiffFW.class);
            diffFW.recode();
            return diffFW;
        } catch (Exception e) {
            LOGGER.error("Cannot convert JSON to DiffFW.", e);
            throw new WikiDiffException("Cannot convert JSON to DiffFW.", e);
        }
    }

    /**
     *
     * @param languageCode the language code, e.g. "en" or "de"
     * @param pageTitle the title of the page, could be "Wikipedia" or "Discussion:Discussion"
     * @return the downloaded XML file from WIKIPEDIA
     * @throws WikiDiffException
     */
    private byte[] loadFromWikipedia(String languageCode, String pageTitle) throws WikiDiffException {
        LOGGER.info("Loading from Wikipedia with language {} and page {}", languageCode, pageTitle);
        return wikiDownloadService.getRevisionFile(languageCode, pageTitle);
    }

    /**
     * Calls loadFromWikipedia(languageCode, pageTitle) first and then ingest it if autoIngest is true.
     * @param languageCode the language code, e.g. "en" or "de"
     * @param pageTitle the title of the page, could be "Wikipedia" or "Discussion:Discussion"
     * @param autoIngest set to true if the file should be ingested automatically
     * @return the downloaded XML revision file
     * @throws WikiDiffException
     */
    public byte[] loadFromWikipedia(String languageCode, String pageTitle, boolean autoIngest) throws WikiDiffException {
        byte[] content = loadFromWikipedia(languageCode, pageTitle);
        if (autoIngest) {
            setXmlFile(content);
        }
        return content;
    }

    private InputStream transform(String xsltLocation, Map<String,Object> params) throws WikiDiffException {
        if (xmlIS==null) {
            if (xmlFile==null || !xmlFile.exists() || !xmlFile.canRead()) {
                LOGGER.error("Cannot read xml file.");
                throw new WikiDiffException("Cannot read the XML file.");
            }
            try {
                byte[] bytes = FileUtils.readFileToByteArray(xmlFile);
                xmlIS = bytes;
            } catch (Exception e) {
                LOGGER.error("Cannot read XMLFile to byte array.", e);
                throw new WikiDiffException("Cannot read XMLFile to byte arrays.", e);
            }
        }
        InputStream result;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(xmlIS);
            result = xsltService.doXsltFromCache(xsltLocation, bis, params, getClass().getResourceAsStream(xsltLocation));
        } catch (Exception e) {
            LOGGER.error("Cannot perform XSLT to get the RevisionListJson.", e);
            throw new WikiDiffException("Cannot perform XSLT to get the RevisionListJson.", e);
        }
        return result;
    }

    private static boolean seemsToBeWikiRevision(byte[] bytes) {
        try {
            String text = new String(bytes, "utf-8");
            return text.contains(TEST_STRING) && text.contains(REVISION_TAG);
        }  catch (Exception e) {
            LOGGER.error("Cannot test the bytes.", e);
            return false;
        }
    }

    private static List<FieldContent> toFieldContents(List<LuceneField> fields) {
        return fields.stream()
                .map(f -> new FieldContent(f.fieldname, f.content))
                .collect(Collectors.toList());
    }


}
