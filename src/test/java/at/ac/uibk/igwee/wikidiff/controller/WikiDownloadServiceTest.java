package at.ac.uibk.igwee.wikidiff.controller;

import at.ac.uibk.igwee.wikidiff.IgweeWikidiffWebappApplication;
import at.ac.uibk.igwee.wikidiff.WikiDiffException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

/**
 * Created by Joseph on 15.06.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IgweeWikidiffWebappApplication.class})
public class WikiDownloadServiceTest {

    private static final File TEST_FILE = new File("./src/test/resources/wiki.xml");
    private static final boolean RUN_TEST = false;

    @Autowired
    private WikiDownloadService service;

    @Autowired
    private MainController mainController;

    @BeforeClass
    public static void reallyRun() {
        Assume.assumeTrue(RUN_TEST);
    }

    @Test
    public void downloadPage() throws Exception {
        String lc = "en";
        String pageTitle = "Wikipedia:General disclaimer";
        byte[] content = service.getRevisionFile(lc, pageTitle);
        FileUtils.writeByteArrayToFile(TEST_FILE, content);
    }

    @Test
    public void getDeRev() throws Exception {

        String languageCode = "de";
        String pageTitle = "Brigitte Mazohl";

        byte[] content = service.getRevisionFile(languageCode, pageTitle);
        mainController.setXmlFile(content);
        Assert.assertTrue(mainController.listRevision().getRevisionList().size() >= 18);
    }

    @Test
    public void getEnRev() throws Exception {

        String languageCode = "en";
        String pageTitle = "Martin Fowler";

        byte[] content = service.getRevisionFile(languageCode, pageTitle);
        mainController.setXmlFile(content);
        Assert.assertTrue(mainController.listRevision().getRevisionList().size() >= 77);
    }

    @Test(expected = WikiDiffException.class)
    public void getDeNotValid() throws Exception {

        String languageCode = "de";
        String pageTitle = "sadfhajs hdklahfskjdf alksejhrjhaw jhjkdaskjfhkas d"; // Hopefully there will be never this page.

        byte[] content = service.getRevisionFile(languageCode, pageTitle);
        mainController.setXmlFile(content);
    }

    @Test
    public void getPageWithArchive() throws Exception {
        String languageCode = "en";
        String pageTitle = "Talk:Psychokinesis";
        int count = 11;
        byte[] all = service.getCombinedRevisionFile(languageCode, pageTitle, true, count);
        mainController.setXmlFile(all);
    }


}
