package at.ac.uibk.igwee.wikirest;

import at.ac.uibk.igwee.metadata.httpclient.HttpMethod;
import at.ac.uibk.igwee.metadata.httpclient.ParameterPair;
import at.ac.uibk.igwee.metadata.httpclient.impl.HttpClientServiceImpl;
import at.ac.uibk.igwee.xslt.impl.SaxonXsltServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by joseph on 7/4/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WikiRestClientTest {

    private static final boolean RUN_TEST = false;

    private static final HttpClientServiceImpl HTTP = new HttpClientServiceImpl();

    private static final File COMBINE = new File("./src/main/resources/xslt/wikiCombine.xsl");

    private static final SaxonXsltServiceImpl XSLT = new SaxonXsltServiceImpl();

    private static final File FINAL_FILE = new File("./target/rest/final.xml");

    private static final List<ParameterPair> DEFAULT_PARAMS =
            Arrays.asList(new ParameterPair("history", "true"));


    private static List<File> files = new ArrayList<>();

    private static final File TMP = new File("./target/rest/");

    @BeforeClass
    public static void setup() {
        Assume.assumeTrue(RUN_TEST);
    }

    @Test
    public void t01_readArchives() throws Exception {
        String host = "de.wikipedia.org";
        String path = "/wiki/Special:Export/Diskussion:Holocaust/Archiv/";

        for (int i=1; i<=8; i++) {
            String now = path + Integer.toString(i);
            ByteArrayInputStream result = HTTP.executeHttps(host, now, DEFAULT_PARAMS, HttpMethod.POST);

            System.out.println("Result for " + now + ": " + result.available());

            File out = new File(TMP, Integer.toString(i) + ".xml");
            FileUtils.copyInputStreamToFile(result, out);
            files.add(out);

        }

    }

    @Test
    public void t02_combineArchives() throws Exception {
        XSLT.addToXslCache("COMBINE", COMBINE.toURI().toURL().toString());
        if (files.size()<1) {
            System.out.println("No file available.");
            return;
        }
        InputStream in = FileUtils.openInputStream(files.get(0));
        if (files.size()<2) {
            System.out.println("No need to combine.");
            FileUtils.copyInputStreamToFile(in, FINAL_FILE);
            return;
        }
        for (int i=1; i<files.size(); i++) {
            System.out.println("Doing File: " + files.get(i).getAbsolutePath());
            Map<String,String> params = new HashMap<>();
            params.put("mergeUrl", files.get(i).toURI().toURL().toString());
            in = XSLT.doXsltFromCache("COMBINE", in, params);
        }
        FileUtils.copyInputStreamToFile(in, FINAL_FILE);
    }


}
