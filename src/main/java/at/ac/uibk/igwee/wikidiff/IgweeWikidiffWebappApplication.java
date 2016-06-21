package at.ac.uibk.igwee.wikidiff;

import at.ac.uibk.igwee.metadata.httpclient.HttpClientService;
import at.ac.uibk.igwee.metadata.httpclient.impl.HttpClientServiceImpl;
import at.ac.uibk.igwee.wikidiff.lucene.LuceneApplicationController;
import at.ac.uibk.kofnego.editing.xslt.extend.ExtendedXsltService;
import at.ac.uibk.kofnego.editing.xslt.extend.diff.DiffXsltExtensionDefinition;
import at.ac.uibk.kofnego.editing.xslt.extend.impl.SaxonExtendedXsltServiceImpl;
import at.ac.uibk.kofnego.editing.xslt.extend.wiki.WikiExtensionFunctionDefinition;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.xml.transform.Transformer;
import java.util.Arrays;
import java.util.List;

/**
 * Main Class for Igwee-WikiDiff
 *
 * Creates all components under at.ac.uibk.igwee.wikidiff and
 * an ExtendedXsltService, containing XSLT Extension Functions for Lucene, Diff and Wiki
 * an HttpClientService
 * a LuceneApplicationController
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"at.ac.uibk.igwee.wikidiff"})
public class IgweeWikidiffWebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(IgweeWikidiffWebappApplication.class, args);
	}

    @Bean
    public ExtendedXsltService extendedXsltService() {

        Cache<String, Transformer> cache = CacheBuilder.newBuilder()
                .maximumSize(20)
                .build();

        TransformerFactoryImpl tfFactory = new TransformerFactoryImpl();
        Configuration configuration = tfFactory.getConfiguration();
        configuration.registerExtensionFunction(new DiffXsltExtensionDefinition());
        configuration.registerExtensionFunction(new WikiExtensionFunctionDefinition());

        List<String> customFunctions = Arrays.asList("http://www.uibk.ac.at/igwee/ns:diff", "http://www.uibk.ac.at/igwee/ns:wiki");

        SaxonExtendedXsltServiceImpl impl = new SaxonExtendedXsltServiceImpl(cache, tfFactory, customFunctions);

        return impl;
    }

    @Bean
    public HttpClientService httpClientService() {
        HttpClientServiceImpl impl = new HttpClientServiceImpl();
        return impl;
    }

    @Bean
    public LuceneApplicationController luceneApplicationController() throws Exception {
        return new LuceneApplicationController();
    }

}
