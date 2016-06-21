package at.ac.uibk.igwee.xslt.impl;

import static at.ac.uibk.igwee.xslt.impl.XsltUtils.createSource;
import static at.ac.uibk.igwee.xslt.impl.XsltUtils.nullOrEmpty;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.uibk.igwee.xslt.XPathService;
import at.ac.uibk.igwee.xslt.XsltException;

/**
 * Implementation of XPathService using Saxon
 * @author Apple
 *
 */
public class SaxonXPathServiceImpl implements XPathService {

	/**
	 * Default maximum size of the cache.
	 */
	private static final int DEFAULT_MAX_CACHE_SIZE = 20;

	private final Cache<String,XdmNode> cache = CacheBuilder
            .newBuilder()
            .maximumSize(DEFAULT_MAX_CACHE_SIZE)
            .build();

	/**
	 * Document builder used for building documents to XmdNode.
	 */
	private DocumentBuilder docBuilder;
	/**
	 * xpathCompiler. 
	 */
	private XPathCompiler xpathCompiler;

	/**
	 * Slf4j-Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SaxonXPathServiceImpl.class);
		
	public SaxonXPathServiceImpl() {
		super();
		Processor proc = new Processor(false);
		docBuilder = proc.newDocumentBuilder();
		xpathCompiler = proc.newXPathCompiler();
	}

	/**
	 * main method for evaluating the xpath.
	 * @param xpath xpath-expression
	 * @param context the document
	 * @param prefixes namespace declarations
	 * @return a XdmValue of the result
	 * @throws XsltException
	 */
	private XdmValue evaluate(String xpath, XdmNode context, Map<String,String> prefixes) 
			throws XsltException {
		try {
			if (prefixes!=null && !prefixes.isEmpty()) {
				for (Map.Entry<String, String> ns: prefixes.entrySet()) {
					xpathCompiler.declareNamespace(nullOrEmpty(ns.getKey()), nullOrEmpty(ns.getValue()));
				}
			}
			return xpathCompiler.evaluate(xpath, context);
		} catch (Exception e) {
			logger.error("Error while evaluating xpath", e);
			throw new XsltException("Exception while evaluating xpath.", e);
		}
	}
	
	/**
	 * Static method, converts a XdmValue to a list of String.
	 * @param val XdmValue, result of xpath evaluation
	 * @return a list of string, can be empty but never null.
	 */
	private static List<String> convertToStringList(XdmValue val) {
		List<String> result = new ArrayList<>(val.size());
		for (int i=0; i<val.size(); i++) {
			XdmItem item = val.itemAt(i);
			result.add(item.getStringValue());
		}
		return result;
	}

	@Override
	public List<String> evaluateAsStringList(InputStream xmlIn, String xpath,
			Map<String, String> namespaceDeclarations) throws XsltException {
		XdmNode doc = createNode(xmlIn);
		XdmValue result = evaluate(xpath, doc, namespaceDeclarations);
		return convertToStringList(result);
	}
	
	@Override
	public List<String> evaluateFromCacheAsStringList(String name, String xpath,
			Map<String,String> nsDecl) throws XsltException {
		XdmNode doc = getFromCache(name);
		XdmValue res = evaluate(xpath, doc, nsDecl);
		return convertToStringList(res);
	}
	
	@Override
	public List<String> evaluateAsStringList(String url, String xpath, 
			Map<String,String> nsDecl) throws XsltException {
		XdmNode doc = createNode(url);
		XdmValue result = evaluate(xpath, doc, nsDecl);
		return convertToStringList(result);
	}

	@Override
	public String bound(InputStream xml) throws XsltException {
		XdmNode doc = createNode(xml);
		return addToCache(doc);
	}

	@Override
	public String bound(String xmlUri) throws XsltException {
		XdmNode doc = createNode(xmlUri);
		return addToCache(doc);
	}

	@Override
	public void unbound(String docName) {
        cache.invalidate(docName);
	}

    private XdmNode getFromCache(String hash) throws XsltException {
        XdmNode node = cache.getIfPresent(hash);
        if (node==null)
            throw new XsltException("Cannot find cached item with name " + hash + ".");
        return node;
    }

    private String addToCache(XdmNode node) throws XsltException {
        if (node==null)
            throw new XsltException("Cannot add null node to cache.");
        String hash = Integer.toString(node.hashCode());
        cache.put(hash, node);
        return hash;
    }


	/**
	 * Helper method to read the document.
	 * @param in InputStream of the xml.
	 * @return a XdmNode, can be used for the evaluation.
	 * @throws XsltException
	 */
	private XdmNode createNode(InputStream in) throws XsltException {
		Source s = createSource(in);
		XdmNode node;
		try {
			node = docBuilder.build(s);
		} catch (Exception e) {
			logger.error("Exception while parsing the document", e);
			throw new XsltException("Exception while building the document.", e);
		}
		return node;
	}

	/**
	 * Converts an uri to XdmNode.
	 * @param uri Uri
	 * @return a XdmNode
	 * @throws XsltException
	 */
	private XdmNode createNode(String uri) throws XsltException {
		Source s = createSource(uri);
		XdmNode node;
		try {
			node = docBuilder.build(s);
		} catch (Exception e) {
			logger.error("Exception while parsing the document.", e);
			throw new XsltException("Exception while building the document.", e);
		}
		return node;
	}

}
