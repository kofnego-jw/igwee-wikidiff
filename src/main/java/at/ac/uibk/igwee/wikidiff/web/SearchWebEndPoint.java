package at.ac.uibk.igwee.wikidiff.web;

import at.ac.uibk.igwee.lucene.rest.helper.SearchRequestFW;
import at.ac.uibk.igwee.lucene.rest.helper.SearchResultFW;
import at.ac.uibk.igwee.wikidiff.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * WebEndPoint for Searching Services
 *
 * Created by joseph on 6/7/16.
 */
@Controller
public class SearchWebEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchWebEndPoint.class);

    @Autowired
    private MainController mainController;

    /**
     * "/search"
     * @param request using JSON to encode a SearchRequestFW
     * @return SearchResult (in ResponseEntity) or InternalServerError
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity<SearchResultFW> search(@RequestBody SearchRequestFW request) {
        LOGGER.info("Search request: {}", request);
        SearchResultFW fw;
        try {
            fw = mainController.doSearching(request);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(fw, HttpStatus.OK);
    }

}
