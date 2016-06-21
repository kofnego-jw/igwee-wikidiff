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
 * Created by Joseph on 13.06.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IgweeWikidiffTestApp.class})
public class UserExportTest {

    @Autowired
    private ExtendedXsltService xsltService;

    private static final File INPUT = MainControllerTest.XML_FILE;
    private static final File XSLT = new File("./src/main/resources/xslt/userExportTei.xsl");

    private static final File OUTPUT = new File("./target/xsltOutput/userExport.xml");

    @Test
    public void export() throws Exception {
        Map<String,Object> userIdParam = new HashMap<>();
        userIdParam.put("userID", "62");
        InputStream result = xsltService.doXslt(INPUT.toURI().toURL().toString(),
                XSLT.toURI().toURL().toString(),
                userIdParam);
        FileUtils.copyInputStreamToFile(result, OUTPUT);
    }

}
