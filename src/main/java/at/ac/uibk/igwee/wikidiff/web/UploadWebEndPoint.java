package at.ac.uibk.igwee.wikidiff.web;

import at.ac.uibk.igwee.wikidiff.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * WebEndpoint for Uploading XML file or for downloading from WikiPedia.
 *
 * Created by Joseph on 03.06.2016.
 */
@Controller
public class UploadWebEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadWebEndPoint.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'-'HHmmss");

    @Autowired
    private MainController mainController;

    /**
     * "/upload"
     *
     * @param file a Multipart File containing the upload
     * @return ResponseEntity for a message String. HttpStatus either OK, BadRequest or InternalServerError
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>("Nothing uploaded, cannot process.", HttpStatus.BAD_REQUEST);
        }
        try {
            mainController.setXmlFile(file.getBytes());
        } catch (Exception e) {
            LOGGER.error("Cannot set XML file.", e);
            return new ResponseEntity<>("Cannot write the XML file for analysis.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * "/downloadFromWikipedia"
     *
     * @param lc "languageCode" for the language code (e.g. "en")
     * @param pt "pageTitle" for the page title.
     * @param autoIngest "autoIngest" for autoIngest
     * @return a FileContainer in ResponseEntity or BadRequest
     */
    @RequestMapping(value = "/downloadFromWikipedia", method = RequestMethod.POST)
    public ResponseEntity<FileContainer> downloadFromWikipedia(@RequestParam("languageCode") String lc,
                                                        @RequestParam("pageTitle") String pt,
                                                        @RequestParam("autoIngest") boolean autoIngest) {
        if (lc==null || lc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FileContainer("Language Code not set.", null));
        }
        if (pt==null || pt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FileContainer("Page Title not set.", null));
        }
        String languageCode = lc.toLowerCase().trim().substring(0, 2);
        String pageTitle = pt.trim();
        byte[] bytes = null;
        try {
            bytes = mainController.loadFromWikipedia(languageCode, pageTitle, autoIngest);
        } catch (Exception e) {
            LOGGER.error("Cannot load from Wikipedia.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FileContainer("Cannot load from Wikipedia: " + e.getMessage(), null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new FileContainer(createPageTitleFilename(languageCode, pageTitle), bytes));
    }

    @RequestMapping(value = "/downloadArchiveFromWikipedia", method = RequestMethod.POST)
    public ResponseEntity<FileContainer> downloadArchiveFromWikipedia(@RequestParam("languageCode") String lc,
                                                        @RequestParam("pageTitle") String pt,
                                                        @RequestParam("archiveCount") int archiveCount,
                                                        @RequestParam("autoIngest") boolean autoIngest) {
        if (lc==null || lc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FileContainer("Language Code not set.", null));
        }
        if (pt==null || pt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FileContainer("Page Title not set.", null));
        }
        String languageCode = lc.toLowerCase().trim().substring(0, 2);
        String pageTitle = pt.trim();
        byte[] bytes = null;
        try {
            bytes = mainController.loadArchiveFromWikipedia(languageCode, pageTitle, autoIngest, archiveCount);
        } catch (Exception e) {
            LOGGER.error("Cannot load from Wikipedia.", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FileContainer("Cannot load from Wikipedia: " + e.getMessage(), null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new FileContainer(createPageTitleFilename(languageCode, pageTitle), bytes));
    }

    /**
     * Creates the title of a WikiPedia download. Using:
     * "wiki_{languageCode}_{pageTitle}_yyyyMMdd-HHmmss.xml"
     * @param lc languageCode
     * @param pt pageTitle
     * @return the default file name.
     */
    public static String createPageTitleFilename(String lc, String pt) {
        LocalDateTime now = LocalDateTime.now();
        String dateString = now.format(DATE_TIME_FORMATTER);
        return "wiki_" + lc + "_" + pt + "_" + dateString + ".xml";
    }

}
