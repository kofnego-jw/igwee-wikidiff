package at.ac.uibk.igwee.wikidiff.controller;

import at.ac.uibk.igwee.lucene.api.searching.SearchRequest;
import at.ac.uibk.igwee.lucene.rest.helper.SearchResultFW;
import at.ac.uibk.igwee.wikidiff.IgweeWikidiffTestApp;
import at.ac.uibk.igwee.wikidiff.web.DiffFW;
import at.ac.uibk.igwee.wikidiff.web.RevisionFWList;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Random;

/**
 * Created by Joseph on 03.06.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ContextConfiguration(classes = IgweeWikidiffTestApp.class)
public class MainControllerTest {

    public static final File XML_FILE = new File("./src/test/resources/wiki.xml");

    private static final Random RANDOM = new Random();

    private static String revId;

    @Autowired
    private MainController mainController;

    @Test
    public void t01_uploadFile() throws Exception {
        mainController.setXmlFile(FileUtils.readFileToByteArray(XML_FILE));
        Assert.assertTrue(mainController.getXmlFile().exists());
    }

    @Test
    public void t02_listRevisions() throws Exception {
        RevisionFWList revisionFWList = mainController.listRevision();
        Assert.assertEquals(124, revisionFWList.revisionList.size());
        revId = revisionFWList.getRevisionList()
                .get(RANDOM.nextInt(revisionFWList.getRevisionList().size())).id;
    }

    @Test
    public void t03_renderRev() throws Exception {
        byte[] content = mainController.toHtml(revId);
        String s = new String(content, "utf-8");
        Assert.assertNotNull(s);
    }

    @Test
    public void t04_diffRevs() throws Exception {
        String rev1 = "100229730";
        String rev2 = "";
        byte[] content = mainController.diffHtml(rev1, rev2);
        String s = new String(content, "utf-8");
        Assert.assertNotNull(s);
    }

    @Test
    public void t05_diffFW() throws Exception {
        String rev1 = "653541794";
        String rev2 = "";
        DiffFW diffFW = mainController.diffFW(rev1, rev2);
        Assert.assertTrue(!diffFW.diff.isEmpty());
    }

    @Test
    public void t06_search() throws Exception {
        SearchRequest sr = new SearchRequest();
        sr.addQuerySetting("revision", "information");
        sr.addQuerySetting("revision", "enjoy");
        SearchResultFW searchResult = mainController.doSearching(sr);
        System.out.println(searchResult);
    }

    @Test
    public void t07_userRevisions() throws Exception {
        String userId = "1692350";
        byte[] bytes = mainController.userRevisions(userId);
        Assert.assertNotNull(bytes);
    }

    @Test
    public void t08_userRevisions() throws Exception {
        String userId = "88.152.72.251";
        byte[] bytes = mainController.userRevisions(userId);
        FileUtils.writeByteArrayToFile(new File("./target/xsltOutput/userRev.html"), bytes);
    }



}
