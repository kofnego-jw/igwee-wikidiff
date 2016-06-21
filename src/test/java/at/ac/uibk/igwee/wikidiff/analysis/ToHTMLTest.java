package at.ac.uibk.igwee.wikidiff.analysis;

import at.ac.uibk.igwee.wikidiff.IgweeWikidiffTestApp;
import at.ac.uibk.igwee.wikidiff.controller.MainControllerTest;
import at.ac.uibk.kofnego.editing.xslt.extend.ExtendedXsltService;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joseph on 03.06.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IgweeWikidiffTestApp.class})
public class ToHTMLTest {

    private static final File XML = MainControllerTest.XML_FILE;
    private static final File XSLT = new File("./src/main/resources/xslt/toHTML.xsl");

    private static final File OUTPUT = new File("./target/xsltOutput/revision.html");

    @Autowired
    private ExtendedXsltService xsltService;

    @Test
    public void test() throws Exception {
        Map<String,Object> params = new HashMap<>();
        params.put("revisionID", "100229730");
        InputStream result = xsltService.doXslt(XML.toURI().toURL().toString(), XSLT.toURI().toURL().toString(), params);
        FileUtils.copyInputStreamToFile(result, OUTPUT);
    }


}
