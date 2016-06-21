package at.ac.uibk.igwee.xslt.impl;

import static at.ac.uibk.igwee.xslt.impl.XsltUtils.createResult;
import static at.ac.uibk.igwee.xslt.impl.XsltUtils.createSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.sf.saxon.TransformerFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Component;
import at.ac.uibk.igwee.xslt.XsltException;
import at.ac.uibk.igwee.xslt.XsltService;

/**
 * Implementation of XsltService using Saxon API.
 * @author Apple
 *
 */
@Component
public class SaxonXsltServiceImpl implements XsltService {

	private static final int TRANSFORMER_CACHE_SIZE = 10;

	private final Cache<String,Transformer> transformerCache =
            CacheBuilder.newBuilder()
                .maximumSize(TRANSFORMER_CACHE_SIZE)
                .build();

	/**
	 * A transformation factory, reused for any xslt-source.
	 */
	private final TransformerFactory transformerFactory = new TransformerFactoryImpl();
	
	/**
	 * slf4j-Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SaxonXsltServiceImpl.class);
		
	public SaxonXsltServiceImpl() {
		super();
	}
	
	/**
	 * Creates a Transformer using a source.
	 * @param xsl Source of the XSL-File.
	 * @return a Transformer. Can be reused.
	 * @throws XsltException if any exception should happen.
	 */
	private Transformer createTransformer(Source xsl) throws XsltException {
		try {
			return transformerFactory.newTransformer(xsl);
		} catch (Exception e) {
			LOGGER.error("Error while creating transformer", e);
			throw new XsltException("While creating transformer.", e);
		}
	}
	
	/**
	 * Main method for transformation.
	 * @param xml xml-Source
	 * @param tf transformer
	 * @param params parameters, can be null
	 * @param result the result
	 * @throws XsltException if any exception should happen.
	 */
	private void doXslt(Source xml, Transformer tf, Map<String,?> params, Result result) 
			throws XsltException {
		try {
            tf.clearParameters();
			if (params!=null && !params.isEmpty()) {
				for (Map.Entry<String, ?> param: params.entrySet()) {
					tf.setParameter(param.getKey(), param.getValue());
				}
			}
			tf.transform(xml, result);
		} catch (Exception e) {
			LOGGER.error("Error while doing the xslt.", e);
			throw new XsltException("While doing the xslt.", e);
		} finally {
			tf.clearParameters();
		}
	}
	
	/**
	 * Do the xslt. relegates to main xslt method.
	 * @param xml
	 * @param tf
	 * @param params
	 * @return
	 * @throws XsltException
	 */
	private InputStream doXslt(Source xml, Transformer tf, Map<String,?> params) 
			throws XsltException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		doXslt(xml, tf, params, createResult(baos));
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	@Override
	public InputStream doXslt(InputStream xml, InputStream xsl,
			Map<String, ?> parameters) throws XsltException {
		Source xmlSource = createSource(xml);
		Source xslSource = createSource(xsl);
		return doXslt(xmlSource, createTransformer(xslSource), parameters);
	}

	@Override
	public InputStream doXslt(String xmlUri, String xslUri,
			Map<String, ?> parameters) throws XsltException {
		Source xml = createSource(xmlUri);
		Source xsl = createSource(xslUri);
		return doXslt(xml, createTransformer(xsl), parameters);
	}
	@Override
	public InputStream doXslt(InputStream in, String xslUri, Map<String, ?> params) 
			throws XsltException {
		Source xml = createSource(in);
		Source xsl = createSource(xslUri);
		return doXslt(xml, createTransformer(xsl), params);
	}


	/**
	 * Adds a transformer to the cache. Will replace an old one with the same name.
	 * @param name Name of the transformer
	 * @param xsl XSL-Source
	 * @throws XsltException
	 */
	private void addToXslCache(String name, Source xsl) throws XsltException {
        Transformer tf = createTransformer(xsl);
        transformerCache.put(name, tf);
	}
	
	/**
	 * Gets a transformer with the xslName from the cache.
	 * @param xslName
	 * @return the transformer
	 * @throws XsltException if no transformer with the name can be found.
	 */
	private Transformer getFromCache(String xslName) throws XsltException {
        Transformer transformer = transformerCache.getIfPresent(xslName);
        if (transformer!=null) return transformer;
		throw new XsltException("Cannot find transformer '" + xslName + "' in the cache.");
	}
	
	@Override
	public void addToXslCache(String xslName, InputStream xsl)
			throws XsltException {
		addToXslCache(xslName, createSource(xsl));
	}

	@Override
	public void addToXslCache(String xslName, String xslUri)
			throws XsltException {
		addToXslCache(xslName, createSource(xslUri));
	}

	@Override
	public InputStream doXsltFromCache(String xslName, InputStream xml,
			Map<String, ?> parameters) throws XsltException {
		Transformer tf = getFromCache(xslName);
		return doXslt(createSource(xml), tf, parameters);
	}

	@Override
	public InputStream doXsltFromCache(String xslName, String xmlUri,
			Map<String,?> parameters) throws XsltException {
		Transformer tf = getFromCache(xslName);
		return doXslt(createSource(xmlUri), tf, parameters);
	}

    @Override
    public InputStream doXsltFromCache(String xlsName, InputStream xml, Map<String, ?> params, InputStream xsltInputStream) throws XsltException {
        Transformer tf = getFromCache(xlsName);
        if (tf==null) {
            addToXslCache(xlsName, createSource(xsltInputStream));
            tf = getFromCache(xlsName);
        }
        return doXslt(createSource(xml), tf, params);
    }

    @Override
    public InputStream doXsltFromCache(String xlsName, InputStream xml, Map<String, ?> params, String xsltUri) throws XsltException {
        Transformer tf = getFromCache(xlsName);
        if (tf==null) {
            addToXslCache(xlsName, createSource(xsltUri));
            tf = getFromCache(xlsName);
        }
        return doXslt(createSource(xml), tf, params);
    }

    @Override
    public InputStream doXsltFromCache(String xslName, String xmlUri, Map<String, ?> parameters, InputStream xslt) throws XsltException {
        Transformer tf = getFromCache(xslName);
        if (tf==null) {
            addToXslCache(xslName, createSource(xslt));
            tf = getFromCache(xslName);
        }
        return doXslt(createSource(xmlUri), tf, parameters);
    }

    @Override
    public InputStream doXsltFromCache(String xslName, String xmlUri, Map<String, ?> parameters, String xslt) throws XsltException {
        Transformer tf = getFromCache(xslName);
        if (tf==null) {
            addToXslCache(xslName, createSource(xslt));
            tf = getFromCache(xslName);
        }
        return doXslt(createSource(xmlUri), tf, parameters);
    }
}
