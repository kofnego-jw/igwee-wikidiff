package at.ac.uibk.igwee.xslt;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Interface for XSL-Transformation-Service
 * 
 * 
 * @author Joseph
 *
 */
public interface XsltService {
	
	/**
	 * Do an XSL-Transformation.
	 * @param xml XML-Source
	 * @param xsl XSL-Source
	 * @param parameters Parameters should be set
	 * @return an InputStream containing the result
	 * @throws XsltException when any exception should happen
	 */
	InputStream doXslt(InputStream xml, InputStream xsl, Map<String,? extends Object> parameters)
			throws XsltException;

	/**
	 * Do an XSL-Transformation.
	 * @param xmlUri XML-Source
	 * @param xslUri XSL-Source
	 * @param parameters Parameters should be set
	 * @return an InputStream containing the result
	 * @throws XsltException when any exception should happen
	 */	
	InputStream doXslt(String xmlUri, String xslUri, Map<String,?> parameters)
			throws XsltException;

	/**
	 * 
	 * @param xmlStream
	 * @param xslUri
	 * @param params
	 * @return
	 * @throws XsltException
	 */
	InputStream doXslt(InputStream xmlStream, String xslUri, Map<String,?> params)
			throws XsltException;
	
	/**
	 * Do an XSL-Transformation.
	 * @param xml XML-Source
	 * @param xsl XSL-Source
	 * @param parameters Parameters should be set
	 * @param result OutputStream to be written
	 * @throws XsltException when any exception should happen
	 */
	default void doXslt(InputStream xml, InputStream xsl, Map<String,?> parameters, OutputStream result)
			throws XsltException {
        InputStream in = doXslt(xml, xsl, parameters);
        copyStream(in, result);
    }
	/**
	 * Do an XSL-Transformation.
	 * @param xmlUri XML-Source
	 * @param xslUri XSL-Source
	 * @param parameters Parameters should be set
	 * @param result OutputStream to be written
	 * @throws XsltException when any exception should happen
	 */	
	default void doXslt(String xmlUri, String xslUri, Map<String,?> parameters, OutputStream result)
			throws XsltException {
        InputStream in = doXslt(xmlUri, xslUri, parameters);
        copyStream(in, result);
    }
	
	/**
	 * Do a transformation
	 * @param xmlInput
	 * @param xslUri
	 * @param params
	 * @param result
	 * @throws XsltException
	 */
	default void doXslt(InputStream xmlInput, String xslUri, Map<String,?> params, OutputStream result)
			throws XsltException {
        InputStream in = doXslt(xmlInput, xslUri, params);
        copyStream(in, result);
    }
	
	/**
	 * Adds a transformer to the cache.
	 * @param xslName Name of the transformer, if already exists in cache, the old entry 
	 * should be replaced.
	 * @param xsl InputStream of the XSLT-File or Data
	 * @throws XsltException if any exception should happen
	 */
	void addToXslCache(String xslName, InputStream xsl) throws XsltException;
	
	/**
	 * Adds a transformer to the cache
	 * @param xslName Name of the transformer, if already exists, replace the old one.
	 * @param xslUri an URI to the xslt-resource.
	 * @throws XsltException if any exception should happen.
	 */
	void addToXslCache(String xslName, String xslUri) throws XsltException;
	
	/**
	 * Does a transformation from the cache.
	 * @param xslName name of the transformer
	 * @param xml xml-source
	 * @param parameters any transformation parameters
	 * @return an InputStream of the result
	 * @throws XsltException if any exception should happen
	 */
	InputStream doXsltFromCache(String xslName, InputStream xml, Map<String,?> parameters)
			throws XsltException;

    /**
     * Do XSLT with a cached xslt. If the cache does not contains the XSLT anymore, the xsltInputStream
     * will be used to create the transformer.
     *
     * @param xlsName the name of the transformer cache
     * @param xml source xml
     * @param params parameters
     * @param xsltInputStream inputStream for xslt
     * @return an inputStream of the transformation result
     * @throws XsltException when exception happens
     */
	InputStream doXsltFromCache(String xlsName, InputStream xml, Map<String,?> params, InputStream xsltInputStream)
            throws XsltException;

	/**
     * Do XSLT with a cached xslt. If the cache does not contains the XSLT anymore, the xsltUri
     * will be used to create the transformer. The transformer is than added to the cache using the xslName.
     *
     * @param xlsName the name of the transformer cache
     * @param xml source xml
     * @param params parameters
     * @param xsltUri URI for the xslt
     * @return an inputStream of the transformation result
     * @throws XsltException when exception happens
     */
	InputStream doXsltFromCache(String xlsName, InputStream xml, Map<String,?> params, String xsltUri)
            throws XsltException;

	/**
	 * Does a transformation from the cache.
	 * @param xslName name of the transformer
	 * @param xmlUri xml-source
	 * @param parameters any transformation parameters
	 * @return an InputStream of the result
	 * @throws XsltException if any exception should happen
	 */
	InputStream doXsltFromCache(String xslName, String xmlUri, Map<String,?> parameters)
			throws XsltException;

    /**
	 * Does a transformation from the cache. If the cache does not contain the transformer
     * anymore, the xslt inputStream will be used to create the transformer and the transformer is
     * than cached.
     *
	 * @param xslName name of the transformer
	 * @param xmlUri xml-source
	 * @param parameters any transformation parameters
     * @param xslt XSLT inputStream
	 * @return an InputStream of the result
	 * @throws XsltException if any exception should happen
	 */
	InputStream doXsltFromCache(String xslName, String xmlUri, Map<String,?> parameters, InputStream xslt)
			throws XsltException;

    /**
	 * Does a transformation from the cache. If the cache does not contain the transformer
     * anymore, the xslt URI will be used to create the transformer and the transformer is
     * than cached.
     *
	 * @param xslName name of the transformer
	 * @param xmlUri xml-source
	 * @param parameters any transformation parameters
     * @param xslt XSLT URI
	 * @return an InputStream of the result
	 * @throws XsltException if any exception should happen
	 */
	InputStream doXsltFromCache(String xslName, String xmlUri, Map<String,?> parameters, String xslt)
			throws XsltException;


	
	/**
	 * Does a transformation from the cache.
	 * @param xslName name of the transformer
	 * @param xml xml-source
	 * @param parameters any transformation parameters
	 * @param result the result outputstream
	 * @throws XsltException if any exception should happen
	 */
	default void doXsltFromCache(String xslName, InputStream xml, Map<String,?> parameters,
			OutputStream result) throws XsltException {
        InputStream in = doXsltFromCache(xslName, xml, parameters);
        copyStream(in, result);
    }
	
	/**
	 * Does a transformation from the cache.
	 * @param xslName name of the transformer
	 * @param xmlUri xml-source
	 * @param parameters any transformation parameters
	 * @param result the result outputstream
	 * @throws XsltException if any exception should happen
	 */
	default void doXsltFromCache(String xslName, String xmlUri, Map<String,?> parameters,
			OutputStream result) throws XsltException {
        InputStream in = doXsltFromCache(xslName, xmlUri, parameters);
        copyStream(in, result);
    }

    static void copyStream(InputStream in, OutputStream out) throws XsltException {
        try {
            IOUtils.copy(in, out);
        } catch (Exception e) {
            throw new XsltException("Cannot copy the inputStream to outputStream.", e);
        }
    }

}
