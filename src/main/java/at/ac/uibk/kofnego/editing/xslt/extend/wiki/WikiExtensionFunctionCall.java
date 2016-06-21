package at.ac.uibk.kofnego.editing.xslt.extend.wiki;

import info.bliki.wiki.model.WikiModel;
import net.sf.saxon.Configuration;
import net.sf.saxon.Controller;
import net.sf.saxon.event.Builder;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.event.Sender;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ParseOptions;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.TreeModel;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.tiny.TinyDocumentImpl;
import net.sf.saxon.value.Whitespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.StringReader;

/**
 * Created by totoro on 06.03.16.
 */
public class WikiExtensionFunctionCall extends ExtensionFunctionCall {

    private static final String PROLOGUE = "<?xml version=\"1.0\"?><div>";
    private static final String EPILOGUE = "</div>";

    private static final Logger LOGGER = LoggerFactory.getLogger(WikiExtensionFunctionCall.class);

    @Override
    public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
        LOGGER.debug("WikiExtensionFunctionCall called with {} arguments.", arguments.length);

        String wikiInput = arguments[0].head().getStringValue();
        String imageBase = arguments.length > 1 ?
                arguments[1].head().getStringValue() : null;

        String linkBase = arguments.length > 2 ?
                arguments[2].head().getStringValue() : null;

        String result;
        if (imageBase==null || linkBase==null) {
            result = WikiModel.toHtml(wikiInput);
        } else {
            WikiModel wm = new WikiModel(imageBase, linkBase);
            try {
                result = wm.render(wikiInput);
            } catch (Exception e) {
                LOGGER.warn("Cannot use imageBase and linkBase to render. Use default instead.", e);
                result = WikiModel.toHtml(wikiInput);
            }
        }
        return evalParseXml(result, context);
    }

    /**
     * Copied from Saxon Source: net.sf.saxon.function.ParseXml
     * @param inputArg String or the transformation
     * @param context XPath Context
     * @return a NodeInfo, having the texts parsed
     * @throws XPathException
     */
    private NodeInfo evalParseXml(String inputArg, XPathContext context) throws XPathException {
        String baseURI = "/";

        String input = PROLOGUE + inputArg + EPILOGUE;

        try {
            Controller controller = context.getController();
            if (controller == null) {
                throw new XPathException("parse-xml() function is not available in this environment");
            }
            Configuration configuration = controller.getConfiguration();

            StringReader sr = new StringReader(input);
            InputSource is = new InputSource(sr);
            is.setSystemId(baseURI);
            Source source = new SAXSource(is);
            source.setSystemId(baseURI);

            Builder b = TreeModel.TINY_TREE.makeBuilder(controller.makePipelineConfiguration());
            Receiver s = b;
            ParseOptions options = new ParseOptions();
            options.setStripSpace(Whitespace.XSLT);
            options.setErrorListener(context.getConfiguration().getErrorListener());

            if (controller.getExecutable().stripsInputTypeAnnotations()) {
                s = configuration.getAnnotationStripper(s);
            }
            s.setPipelineConfiguration(b.getPipelineConfiguration());

            Sender.send(source, s, options);

            TinyDocumentImpl node = (TinyDocumentImpl) b.getCurrentRoot();
            node.setBaseURI(baseURI);
            node.setSystemId(null);
            b.reset();
            return node;
        } catch (XPathException err) {
            throw new XPathException("First argument to parse-xml() is not a well-formed and namespace-well-formed XML document. XML parser reported: " +
                    err.getMessage(), "FODC0006");
        }
    }

}
