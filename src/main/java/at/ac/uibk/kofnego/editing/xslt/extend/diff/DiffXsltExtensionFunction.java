package at.ac.uibk.kofnego.editing.xslt.extend.diff;

import at.ac.uibk.kofnego.editing.xslt.extend.SaxonHelper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Function implementation for uibk:highlight.
 * The implementation uses a cache: JsonStrings->SearchRequest will be cached
 * @author totoro
 *
 */
public class DiffXsltExtensionFunction extends ExtensionFunctionCall {
	
	
	private static final long serialVersionUID = -201502101317L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DiffXsltExtensionFunction.class);
	
	/**
	 * DiffMatchPatch
	 */
	private final DiffMatchPatch diffMatchPatch;

	
	public DiffXsltExtensionFunction(DiffMatchPatch hs) {
		super();
		this.diffMatchPatch = hs;
	}
	

	@Override
	public Sequence call(XPathContext context, Sequence[] arguments)
			throws XPathException {

        LOGGER.debug("XPath Extension Function Diff called.");

		if (arguments.length<2) {
            LOGGER.error("Function Diff needs at least two arguments.");
            throw new XPathException("Must provide at least 2 arguments.");
        }

        String s1 = arguments[0].head().getStringValue();
        String s2 = arguments[1].head().getStringValue();

        List<DiffMatchPatch.Diff> diffs = diffMatchPatch.diffMain(s1, s2);

        List<String> collect = diffs.stream().map(diff -> (diff.operation == DiffMatchPatch.Operation.INSERT ? "+" :
                diff.operation == DiffMatchPatch.Operation.DELETE ? "-" : "0") + diff.text).collect(Collectors.toList());

        return SaxonHelper.createListSequenceIterator(collect);
	}

}
