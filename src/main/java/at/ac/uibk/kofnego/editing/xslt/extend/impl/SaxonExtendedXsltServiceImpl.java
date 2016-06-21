package at.ac.uibk.kofnego.editing.xslt.extend.impl;

import at.ac.uibk.igwee.xslt.XsltException;
import at.ac.uibk.kofnego.editing.xslt.extend.ExtendedXsltService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.sf.saxon.TransformerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * Created by joseph on 25.02.16.
 */
public class SaxonExtendedXsltServiceImpl implements ExtendedXsltService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaxonExtendedXsltServiceImpl.class);

    private final Cache<String,Transformer> cache;

    private final TransformerFactory transformerFactory;

    private final List<String> customFunctionIds;

    /**
     * Use standard cache and no extension functions!
     */
    public SaxonExtendedXsltServiceImpl() {
        this.cache = CacheBuilder.newBuilder().maximumSize(10).build();
        this.transformerFactory = new TransformerFactoryImpl();
        this.customFunctionIds = Collections.emptyList();
    }

    public SaxonExtendedXsltServiceImpl(Cache cache, TransformerFactory transformerFactory, List<String> customFunctionIds) {
        this.cache = cache;
        this.transformerFactory = transformerFactory;
        this.customFunctionIds = customFunctionIds;
    }

    @Override
    public List<String> listCustomFunctions() {
        return customFunctionIds;
    }

    @Override
    public void addToXslCache(String xslName, String xslUri)
            throws XsltException {
        Source xsl = createSource(xslUri);
        Transformer t = createTransformer(xsl);
        cache.put(xslName,t);
    }

    @Override
    public void addToXslCache(String xslName, InputStream xsl)
            throws XsltException {
        Source xslSource = createSource(xsl);
        Transformer t = createTransformer(xslSource);
        cache.put(xslName, t);
    }

    private Transformer createTransformer(Source source) throws XsltException {
        try {
            return transformerFactory.newTransformer(source);
        } catch (TransformerException e) {
            throw new XsltException("Cannot create transformer.", e);
        }
    }

    @Override
    public InputStream doXsltFromCache(String xslName, InputStream xml,
                                       Map<String, ?> parameters) throws XsltException {
        Transformer t = getFromCache(xslName);
        Source xmlSource = createSource(xml);
        return xslt(xmlSource, t, parameters);
    }

    @Override
    public InputStream doXsltFromCache(String xslName, String xmlUri,
                                       Map<String, ?> parameters) throws XsltException {
        Source xmlSource = createSource(xmlUri);
        Transformer t = getFromCache(xslName);
        return xslt(xmlSource, t, parameters);
    }

    private Transformer getFromCacheOrAddIt(String xsltName, InputStream xslt) throws XsltException {
        Transformer t = cache.getIfPresent(xsltName);
        if (t!=null) return t;
        t = createTransformer(xslt);
        cache.put(xsltName, t);
        return t;
    }

    private Transformer getFromCacheOrAddIt(String xsltName, String xslt) throws XsltException {
        Transformer t = cache.getIfPresent(xsltName);
        if (t!=null) return t;
        t = createTransformer(xslt);
        cache.put(xsltName, t);
        return t;
    }

    @Override
    public InputStream doXsltFromCache(String xlsName, InputStream xml, Map<String, ?> params, InputStream xsltInputStream) throws XsltException {
        Transformer t = getFromCacheOrAddIt(xlsName, xsltInputStream);
        Source xmlSource = createSource(xml);
        return xslt(xmlSource, t, params);
    }

    @Override
    public InputStream doXsltFromCache(String xlsName, InputStream xml, Map<String, ?> params, String xsltUri) throws XsltException {
        Transformer t = getFromCacheOrAddIt(xlsName, xsltUri);
        Source xmlSource = createSource(xml);
        return xslt(xmlSource, t, params);
    }

    @Override
    public InputStream doXsltFromCache(String xslName, String xmlUri, Map<String, ?> parameters, InputStream xslt) throws XsltException {
        Transformer t = getFromCacheOrAddIt(xslName, xslt);
        Source xmlSource = createSource(xmlUri);
        return xslt(xmlSource, t, parameters);
    }

    @Override
    public InputStream doXsltFromCache(String xslName, String xmlUri, Map<String, ?> parameters, String xslt) throws XsltException {
        Transformer t = getFromCacheOrAddIt(xslName, xslt);
        Source xmlSource = createSource(xmlUri);
        return xslt(xmlSource, t, parameters);
    }

    private Transformer getFromCache(String name) throws XsltException {
        Transformer t = cache.getIfPresent(name);
        if (t==null)
            throw new XsltException("Cannot find transformer with name " + name + ".");
        return t;
    }

    @Override
    public InputStream doXslt(String xmlUri, String xslUri,
                              Map<String, ?> parameters) throws XsltException {
        Source xml = createSource(xmlUri);
        Transformer t = createTransformer(xslUri);
        return xslt(xml, t, parameters);
    }

    @Override
    public InputStream doXslt(InputStream xmlStream, String xslUri,
                              Map<String, ?> params) throws XsltException {
        Source xmlSource = createSource(xmlStream);
        Transformer t = createTransformer(xslUri);
        return xslt(xmlSource, t, params);
    }

    @Override
    public InputStream doXslt(InputStream xml, InputStream xsl,
                              Map<String, ? extends Object> parameters) throws XsltException {
        Source xmlSource = createSource(xml);
        Transformer t = createTransformer(xsl);
        return xslt(xmlSource, t, parameters);
    }

    private Transformer createTransformer(String uri) throws XsltException {
        Source xsl = createSource(uri);
        return createTransformer(xsl);
    }

    private Transformer createTransformer(InputStream in) throws XsltException {
        Source xsl = createSource(in);
        return createTransformer(xsl);
    }

    private ByteArrayInputStream xslt(Source xmlSource, Transformer tf, Map<String, ?> parameters) throws XsltException {
        tf.clearParameters();
        if (parameters!=null && !parameters.isEmpty()) {
            parameters.forEach((k,v) -> tf.setParameter(k, v));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            tf.transform(xmlSource, createResult(baos));
        } catch (Exception e) {
            LOGGER.error("Cannot do the transformation.", e);
            throw new XsltException("Cannot do the transformation.", e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }


    private static Source createSource(InputStream in) {
        return new StreamSource(in);
    }

    private static Source createSource(String inUrl) {
        return new StreamSource(inUrl);
    }

    private static Result createResult(OutputStream os) {
        return new StreamResult(os);
    }


}
