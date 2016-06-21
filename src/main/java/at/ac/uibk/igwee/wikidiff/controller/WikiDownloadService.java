package at.ac.uibk.igwee.wikidiff.controller;

import at.ac.uibk.igwee.metadata.httpclient.HttpClientService;
import at.ac.uibk.igwee.metadata.httpclient.HttpMethod;
import at.ac.uibk.igwee.metadata.httpclient.ParameterPair;
import at.ac.uibk.igwee.wikidiff.WikiDiffException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

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

    /**
     * Uses HttpClientService
     */
    @Autowired
    private HttpClientService httpClientService;

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

    private static String host(String language) {
        if (language==null) return "en" + BASE_HOST;
        return language + BASE_HOST;
    }

    private static String path(String pageTitle) {
        if (pageTitle==null) return BASE_PATH + "Wikipedia";
        return BASE_PATH + pageTitle;
    }


}
