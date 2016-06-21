package at.ac.uibk.igwee.wikidiff.lucene;

import at.ac.uibk.igwee.lucene.impl.LuceneApplication;
import at.ac.uibk.igwee.wikidiff.WikiDiffException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * LuceneApplication Service. Used to inject LuceneApplication. It will use the default Lucene Path ("./lucene")
 * to create a RAM based LuceneApplication.
 *
 * Created by joseph on 6/7/16.
 */
public class LuceneApplicationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneApplicationController.class);
    private static final List<String> LOCATIONS = Arrays.asList(
            "/lucene/.config/wikidiff.is.xml",
            "/lucene/.config/wikidiff.ss.xml",
            "/lucene/.config/wikidiff.vs.xml"
    );

    private static final String LUCENE_PATH = "./lucene";

    private LuceneApplication luceneApplication;

    private final boolean useRAM;

    /**
     * Creates a RAM based LuceneApplicationController
     * @throws WikiDiffException
     */
    public LuceneApplicationController() throws WikiDiffException {
        this(true);
    }

    /**
     * Creates a LuceneApplicationController
     * @param useRAM set to true (default) if you want a RAM based LuceneApplicationController. Set to false to be File Based.
     * @throws WikiDiffException
     */
    public LuceneApplicationController(boolean useRAM) throws WikiDiffException {
        this.useRAM = useRAM;
        if (!new File(LUCENE_PATH).exists()) {
            init();
        }
        try {
            if (useRAM) {
                LOGGER.info("Create RAM based Lucene Application.");
                luceneApplication = LuceneApplication.createRAMBasedLuceneApplication(LUCENE_PATH);
            } else {
                LOGGER.info("Create FS based Lucene Application.");
                luceneApplication = new LuceneApplication(LUCENE_PATH);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot initalize the Lucene Application.", e);
            throw new WikiDiffException("Cannot initialize the RAM Lucene Application.", e);
        }
    }

    public LuceneApplication getLuceneApplication() {
        return luceneApplication;
    }

    @PreDestroy
    public void shutDown() {
        LOGGER.info("Closing Lucene Application.");
        try {
            if (luceneApplication!=null) {
                luceneApplication.close();
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot shut down Lucene Applicatio gracefully.", e);
        }
        luceneApplication = null;
    }


    private static void init() throws WikiDiffException {
        File base = new File(LUCENE_PATH);
        try {
            LOGGER.info("Copying Standard Lucene Config Files to {}.", base.getAbsolutePath());
            for (String now: LOCATIONS) {
                InputStream in = LuceneApplicationController.class.getResourceAsStream(now);
                FileUtils.copyInputStreamToFile(in, new File(base, now.substring(8)));
            }
            LOGGER.info("Copying Lucene Config Files successful.");
        } catch (Exception e) {
            LOGGER.error("Cannot copy lucene config files.", e);
            throw new WikiDiffException("Cannot copy lucene config files.", e);
        }
    }


}
