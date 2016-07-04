package at.ac.uibk.igwee.wikidiff.controller;

import at.ac.uibk.igwee.metadata.httpclient.HttpClientService;
import at.ac.uibk.igwee.metadata.httpclient.HttpMethod;
import at.ac.uibk.igwee.metadata.httpclient.ParameterPair;
import at.ac.uibk.igwee.wikidiff.WikiDiffException;
import at.ac.uibk.igwee.xslt.XsltService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DownloadService from Wikipedia.
 *
 * As for 2016/06/20:
 *
 * Wikipedia offers the Special:Export page for exporting the contents. It uses the same url across all MediaWiki
 * site:
 *
 * https://host/pathToWiki/Special:Export/{PageTitle}
 *
 * Using the "POST" request and set the parameter "history" (to anything), MediaWiki will export
 * all revisions of this page as XML.
 *
 * This class eases the http-call.
 *
 * Created by Joseph on 15.06.2016.
 */
@Component
public class WikiDownloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiDownloadService.class);

    private static final String BASE_HOST = ".wikipedia.org";

    private static final String BASE_PATH = "/wiki/Special:Export/";

    private static final List<ParameterPair> DEFAULT_PARAMS =
            Arrays.asList(new ParameterPair("history", "true"));

    private static final byte[] WIKI_COMBINE_XSLT;
    static {
        byte[] test;
        try {
            test = IOUtils.toByteArray(WikiDownloadService.class.getResourceAsStream("/xslt/wikiCombine.xsl"));
        } catch (Exception e) {
            LOGGER.error("Cannot read wikiCombine.xsl.", e);
            test = null;
        }
        WIKI_COMBINE_XSLT = test;
    }

    private static final File DOWNLOAD_DIR = new File("download");

    /**
     * Uses HttpClientService
     */
    @Autowired
    private HttpClientService httpClientService;

    /**
     * Uses XsltService to combine XML files.
     */
    @Autowired
    private XsltService xsltService;

    /**
     *
     * @param languageCode the language code. Used to create the host
     * @param pageTitle the page title to be retrieved
     * @return the XML export
     * @throws WikiDiffException Please note: Since MediaWiki still sends "200 OK" for page not
     *                           existing, this method also does not throw exception if there is no page with the page title.
     */
    public byte[] getRevisionFile(String languageCode, String pageTitle) throws WikiDiffException {
        LOGGER.info("Download whole history from {} Wiki: page {}.", languageCode, pageTitle);

        ByteArrayInputStream result;
        try {
            result = httpClientService.executeHttps(host(languageCode), path(pageTitle), DEFAULT_PARAMS, HttpMethod.POST, "utf-8");
            return IOUtils.toByteArray(result);
        } catch (Exception e) {
            LOGGER.error("Cannot convert ByteArrayInputStream to byte[].", e);
            throw new WikiDiffException("Cannot convert ByteArrayInputStream to byte[]... This should never happen.", e);
        }

    }

    public byte[] getCombinedRevisionFile(String languageCode, String pageTitle,
                                          boolean includeArchive, int archivePagecount) throws WikiDiffException{
        if (!includeArchive) return getRevisionFile(languageCode, pageTitle);
        if (archivePagecount<=0) {
            LOGGER.warn("Archive Page Count is too small, do not use archive function.");
            return getRevisionFile(languageCode, pageTitle);
        }
        LOGGER.info("Download whole history including archive from: {} Wiki: page {}, archive page counting: {}",
                languageCode, pageTitle, archivePagecount);
        InputStream lastPage = new ByteArrayInputStream(getRevisionFile(languageCode, pageTitle));
        for (int i=1; i<=archivePagecount; i++) {
            String path =archivePath(languageCode, pageTitle, i);
            Map<String,String> params = new HashMap<>();
            ByteArrayInputStream xslt = new ByteArrayInputStream(WIKI_COMBINE_XSLT);
            try {
                InputStream nowPage = httpClientService.executeHttps(host(languageCode), path,
                        DEFAULT_PARAMS, HttpMethod.POST, "utf-8");
                File tmp = new File(DOWNLOAD_DIR, filename(languageCode, pageTitle, i));
                FileUtils.copyInputStreamToFile(nowPage, tmp);
                params.put("mergeUrl", tmp.toURI().toURL().toString());
                lastPage = xsltService.doXslt(lastPage, xslt, params);
            } catch (Exception e) {
                LOGGER.error("Cannot read archive page from: " + path, e);
                continue;
            }
        }
        try {
            return IOUtils.toByteArray(lastPage);
        } catch (Exception e) {
            LOGGER.error("Cannot convert InputStream to byte[].", e);
            throw new WikiDiffException("Cannot convert inputStream to byte array.", e);
        }
    }

    private static String host(String language) {
        if (language==null) return "en" + BASE_HOST;
        return language + BASE_HOST;
    }

    private static String path(String pageTitle) {
        if (pageTitle==null) return BASE_PATH + "Wikipedia";
        return BASE_PATH + pageTitle;
    }

    private static String archivePath(String languageCode, String pageTitle, int archivePage) {
        return path(pageTitle) + fixArchivePath(languageCode) + Integer.toString(archivePage);
    }

    private static String fixArchivePath(String langCode) {
        switch (langCode) {
            case "de": return "/Archiv/";
        }
        return "/Archive_";
    }

    private static String filename(String languageCode, String pageTitle, int counter) {
        return languageCode + "/" + safe(pageTitle) + "/" + counter;
    }

    private static String safe(String s) {
        return s.replaceAll("[^A-Za-z0-9.]", "_");
    }

}
