package at.ac.uibk.igwee.wikidiff.lucene;

import at.ac.uibk.igwee.wikidiff.IgweeWikidiffTestApp;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by joseph on 6/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IgweeWikidiffTestApp.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LuceneApplicationServiceTest {

    @Autowired
    private LuceneApplicationController luceneApplicationController;

    @Test
    public void testGetApplication() throws Exception {
        Assert.assertNotNull(luceneApplicationController.getLuceneApplication());
        Assert.assertTrue(luceneApplicationController.getLuceneApplication().getIndexingService().getIndexSettings().size() == 1);
    }


}
