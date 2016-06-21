package at.ac.uibk.igwee.lucene.testmock;

import at.ac.uibk.igwee.lucene.api.LuceneApplicationConfig;
import at.ac.uibk.igwee.lucene.api.serializer.LuceneApplicationSerializer;
import at.ac.uibk.igwee.lucene.api.highlighting.HighlightingService;
import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;
import at.ac.uibk.igwee.lucene.api.indexing.FieldContent;
import at.ac.uibk.igwee.lucene.api.indexing.IndexSetting;
import at.ac.uibk.igwee.lucene.api.indexing.IndexingService;
import at.ac.uibk.igwee.lucene.api.searching.SearchSetting;
import at.ac.uibk.igwee.lucene.api.searching.SearchingService;
import at.ac.uibk.igwee.lucene.api.viewing.ViewSetting;
import at.ac.uibk.igwee.lucene.api.viewing.ViewingService;
import at.ac.uibk.igwee.lucene.impl.LuceneApplication;
import at.ac.uibk.igwee.lucene.impl.RAMLuceneDirHolder;
import at.ac.uibk.igwee.xslt.XPathService;
import at.ac.uibk.igwee.xslt.impl.SaxonXPathServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

/**
 * Created by Joseph on 07.04.2016.
 */
public class MockLuceneApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockLuceneApplication.class);

    private static final String TEST_TEI_P5_FILE = "/tei.xml";

    private static final String INDEX_NAME = "basic";

    private static final Map<String, String> DEFAULT_NS_DECL;
    static {
        DEFAULT_NS_DECL = new HashMap<>();
        DEFAULT_NS_DECL.put("tei", "http://www.tei-c.org/ns/1.0");
    }

    private static final List<String> PERSNAMES =
            Arrays.asList("Kafka, Franz", "Kafka, Ottla", "Samsa, Gregor", "Samsa, Grete");

    private static final LuceneApplication APPLICATION = createMockApplication(true);

    private static LuceneApplication createMockApplication(boolean filled) {
        LOGGER.info("Create MockLuceneApplication. Filled: {}.", filled);
        LuceneApplicationConfig config = new LuceneApplicationConfig();
        config.setIndexBaseDir(RAMLuceneDirHolder.RAM_BASEDIR);
        LuceneApplication la = null;
        try {
            Class c = MockLuceneApplication.class;
            IndexSetting bis = LuceneApplicationSerializer.loadIndexSetting(c.getResourceAsStream("/lucene/.config/basic.is.xml"));
            SearchSetting bss = LuceneApplicationSerializer.loadSearchSetting(c.getResourceAsStream("/lucene/.config/basic.ss.xml"));
            ViewSetting bvs =  LuceneApplicationSerializer.loadViewSetting(c.getResourceAsStream("/lucene/.config/basic.vs.xml"));
            config.addIndexSetting(bis);
            config.addSearchSetting(bss);
            config.addViewSetting(bvs);
            la = new LuceneApplication(config);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            // This should never happen.
        }

        if (!filled) return la;

        try {
            doIndex(la);
        } catch (Exception ignored) {
            ignored.printStackTrace();
            // Should also never happen
        }

        return la;
    }


    private static void doIndex(LuceneApplication app) throws Exception {
        XPathService xpathS = new SaxonXPathServiceImpl();

        InputStream in = MockLuceneApplication.class.getResourceAsStream(TEST_TEI_P5_FILE);

        List<String> paras = xpathS.evaluateAsStringList(in, "//tei:p", DEFAULT_NS_DECL);
        for (int i=0; i<paras.size(); i++) {
            List<FieldContent> contents = new ArrayList<>();
            contents.add(new FieldContent("text", escapeNextLine( paras.get(i))));
            contents.add(new FieldContent("id", String.format("%04d", Integer.valueOf(i))));
            contents.add(new FieldContent("author", "Kafka, Franz"));
            contents.add(new FieldContent("title", "Die Verwandlung"));
            int persNameNum = i%PERSNAMES.size();
            contents.add(new FieldContent("persName", PERSNAMES.get(persNameNum)));
            int persNameNumN = persNameNum == 0 ? PERSNAMES.size()-1 : persNameNum -1;
            contents.add(new FieldContent("persName", PERSNAMES.get(persNameNumN)));

            String docId =String.format("%04d", Integer.valueOf(i));
//            String docId = Integer.toString(i);

            List<FacetContent> facetContents = new ArrayList<>();
            if (i%2!=0) {
                facetContents.add(new FacetContent("author", "Kafka, Franz"));
            }
            if (i%3!=0) {
                facetContents.add(new FacetContent("author", "Deutsches Textarchiv"));
            }
            if (i%4!=0) {
                facetContents.add(new FacetContent("genre", "Belletristik"));
            }
            if (i%5!=0) {
                facetContents.add(new FacetContent("genre", "E-Publikation"));
            }
            if (i%6!=0) {
                facetContents.add(new FacetContent("genre", "Belletristik", "Groteske"));
            }
            if (i%7!=0) {
                facetContents.add(new FacetContent("genre", "E-Publikation", "TEI-Publication"));
            }
            if (i%8!=0) {
                facetContents.add(new FacetContent("keyword", "Verwandlung"));
            }
            if (i%9!=0) {
                facetContents.add(new FacetContent("keyword", "Metamorphse"));
            }
            if (i%10!=0) {
                facetContents.add(new FacetContent("keyword", "Käfer"));
            }
            if (i%11!=0) {
                facetContents.add(new FacetContent("keyword", "Wohnung"));
            }
            app.getIndexingService().doIndexWithFacets(INDEX_NAME, docId, contents, facetContents);
        }
    }


    private static String escapeNextLine(String str) {
        return str.replaceAll("\\r?\\n", "\\\\n");
    }

    public static LuceneApplication createApplication() {
        return createMockApplication(false);
    }

    public static LuceneApplication getApplication() {
        return APPLICATION;
    }

    public static IndexingService getIndexingService() throws Exception {
        return APPLICATION.getIndexingService();
    }

    public static SearchingService getSearchingService() throws Exception {
        return APPLICATION.getSearchingService();
    }

    public static ViewingService getViewingService() throws Exception {
        return APPLICATION.getViewingService();
    }

    public static HighlightingService getHighlightingService() throws Exception {
        return APPLICATION.getHighlightingService();
    }

}
