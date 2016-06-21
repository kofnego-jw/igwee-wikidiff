package at.ac.uibk.igwee.wikidiff.web;

import at.ac.uibk.igwee.wikidiff.controller.MainController;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * View Revision Endpoint
 *
 * Created by Joseph on 03.06.2016.
 */
@Controller
public class ViewDiffWebEndPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewDiffWebEndPoint.class);

    @Autowired
    private MainController mainController;

    /**
     * "/diff/revId1/revId2"
     *
     * @param revId1 the id of the first revision
     * @param revId2 the id of the seconde revision
     * @return a DiffFW
     */
    @RequestMapping("/diff/{revId1}/{revId2}")
    public ResponseEntity<DiffFW> diff(@PathVariable("revId1") String revId1, @PathVariable("revId2") String revId2) {
        DiffFW result;
        try {
            result = mainController.diffFW(revId1, revId2);
        } catch (Exception e) {
            LOGGER.error("Cannot perform the diffFW.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping("/diffHtml/{revId1}/{revId2}")
    public void diffHtml(@PathVariable("revId1") String revId1, @PathVariable("revId2") String revId2, HttpServletResponse response) {
        byte[] result;
        try {
            result = mainController.diffHtml(revId1, revId2);
        } catch (Exception e) {
            LOGGER.error("Cannot perform the diff.");
            sendError(response, HttpStatus.INTERNAL_SERVER_ERROR, "Cannot perform the diff request: " + e.getMessage());
            return;
        }
        sendContent(response, result);
    }

    /**
     * "/view/revID"
     *
     * Sends the HTML conversion to the HttpServletResponse
     *
     * @param revisionID the ID to be viewed as HTML
     * @param response the auto injected HttpServletResponse
     */
    @RequestMapping("/view/{revID}")
    public void viewRevision(@PathVariable("revID") String revisionID, HttpServletResponse response) {
        if (revisionID==null) {
            sendError(response, HttpStatus.BAD_REQUEST, "Missing revision ID.");
            return;
        }
        byte[] content;
        try {
            content = mainController.toHtml(revisionID);
        } catch (Exception e) {
            LOGGER.error("Cannot render the revision.", e);
            sendError(response, HttpStatus.INTERNAL_SERVER_ERROR, "Cannot render the request: " + e.getMessage());
            return;
        }
        sendContent(response, content);
    }

    /**
     * "/user/userId"
     *
     * Renders an HTML View of the user
     *
     * @param userId the id of the user
     * @param response the autoInjected HttpServletResponse
     */
    @RequestMapping("/user/{userId:.+}")
    public void viewUser(@PathVariable("userId") String userId, HttpServletResponse response) {
        if (userId==null) {
            sendError(response, HttpStatus.BAD_REQUEST, "Missing contributor ID.");
            return;
        }
        LOGGER.info("View revisions by user {}.", userId);
        byte[] content;
        try {
            content = mainController.userRevisions(userId);
        } catch (Exception e) {
            LOGGER.error("Cannot render the user revisions.", e);
            sendError(response, HttpStatus.INTERNAL_SERVER_ERROR, "Cannot render the user revisions: " + e.getMessage());
            return;
        }
        sendContent(response, content);
    }

    private static void sendContent(HttpServletResponse response, byte[] content) {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            IOUtils.write(content, os);
        } catch (Exception e) {
            LOGGER.error("Cannot write to the outputStream.", e);
        } finally {
            if (os!=null) {
                try {
                    os.close();
                } catch (Exception ignored) {
                    LOGGER.warn("Exception while closing the outputStream.", ignored);
                }
            }
        }
    }

    private static void sendError(HttpServletResponse response, HttpStatus status, String msg) {
        response.setStatus(status.value());
        response.setContentType("text/plain");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(msg);
        } catch (Exception e) {
            LOGGER.error("Cannot write to HttpServletResponse.", e);
        } finally {
            if (writer!=null) {
                try {
                    writer.close();
                } catch (Exception ignored) {
                    LOGGER.warn("Exception while closing the PrintWriter.", ignored);
                }
            }
        }
    }

}
