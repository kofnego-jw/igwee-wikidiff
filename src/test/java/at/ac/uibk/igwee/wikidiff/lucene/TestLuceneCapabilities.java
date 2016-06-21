package at.ac.uibk.igwee.wikidiff.lucene;

import at.ac.uibk.igwee.lucene.api.indexing.FieldContent;
import at.ac.uibk.igwee.lucene.api.indexing.IndexingService;
import at.ac.uibk.igwee.lucene.api.searching.SearchResult;
import at.ac.uibk.igwee.lucene.api.searching.SearchingService;
import at.ac.uibk.igwee.lucene.impl.LuceneApplication;
import at.ac.uibk.igwee.lucene.testmock.SOutHelper;
import at.ac.uibk.igwee.wikidiff.IgweeWikidiffTestApp;
import at.ac.uibk.igwee.wikidiff.controller.MainControllerTest;
import at.ac.uibk.kofnego.editing.xslt.extend.ExtendedXsltService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by joseph on 6/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IgweeWikidiffTestApp.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestLuceneCapabilities {

    private static LuceneApplication luceneApplication;

    private static LuceneDocList luceneDocList;

    @BeforeClass
    public static void setup() throws Exception {
        luceneApplication = LuceneApplication.createRAMBasedLuceneApplication("./src/main/resources/lucene");
    }

    @Autowired
    private ExtendedXsltService xsltService;

    @Test
    public void t01_testXSLT() throws Exception {
        File input = MainControllerTest.XML_FILE;
        File xsl = new File("./src/main/resources/xslt/toLuceneDocs.xsl");
        InputStream inputStream = xsltService.doXslt(input.toURI().toURL().toString(), xsl.toURI().toURL().toString(), null);
        ObjectMapper om = new ObjectMapper();
        luceneDocList = om.readValue(inputStream, LuceneDocList.class);
        Assert.assertNotNull(luceneDocList);
        System.out.println(luceneDocList.luceneDocs.size());
    }

    @Test
    public void t02_testIndexing() throws Exception {
        IndexingService is = luceneApplication.getIndexingService();
        String indexName = "wikidiff";
        luceneDocList.getLuceneDocs().forEach(x -> {
            try {
                is.doIndex(indexName, x.docId, toFieldContents(x.contents));
            } catch (Exception e) {
                Assert.assertNull(e);
            }
        });
    }

    @Test
    public void t03_testSearching() throws Exception {
        SearchingService ss = luceneApplication.getSearchingService();
//        SearchRequest req = new SearchRequest("wikidiff", "revision", "liste");

        SearchResult search = ss.search("wikidiff", "list");
        SOutHelper.output(search);
    }

    private static List<FieldContent> toFieldContents(List<LuceneField> fields) {
        return fields.stream()
                .map(f -> new FieldContent(f.fieldname, f.content))
                .collect(Collectors.toList());
    }

}
