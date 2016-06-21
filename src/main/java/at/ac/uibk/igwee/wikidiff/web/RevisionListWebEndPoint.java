package at.ac.uibk.igwee.wikidiff.web;

import at.ac.uibk.igwee.wikidiff.controller.MainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * WebEndPoint (Controller) for RevisionList
 *
 * Created by Joseph on 03.06.2016.
 */
@Controller
public class RevisionListWebEndPoint {

    @Autowired
    private MainController mainController;

    /**
     * "/list"
     * @return ResponseEntity containing either RevisionFWList or HttpStatus: Internal Server Error
     */
    @RequestMapping("/list")
    @ResponseBody
    public ResponseEntity<RevisionFWList> listRevisions() {
        try {
            return new ResponseEntity<>(mainController.listRevision(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
