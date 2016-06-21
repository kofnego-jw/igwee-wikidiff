package at.ac.uibk.kofnego.editing.xslt.extend.wiki.xslt.extend.lucene;

import at.ac.uibk.igwee.lucene.api.highlighting.HighlightingService;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;

/**
 * Definition of the highlighting function.
 * @author totoro
 *
 */
public class HighlightingXsltExtensionDefinition extends ExtensionFunctionDefinition {
	
	
	private static final long serialVersionUID = 201502101429L;

	/**
	 * StructuredQName for the function
	 */
	private static final StructuredQName FUNCTION_QNAME = new StructuredQName(QNameConstants.DEFAULT_PREFIX, 
			QNameConstants.NAMESPACE_URL, QNameConstants.HIGHLIGHT_FUNCTIONNAME);
	
	/**
	 * Argument: up to 2 strings.
	 */
	private static final SequenceType[] ARGUMENT_TYPES = new SequenceType[] {
		SequenceType.SINGLE_STRING,
		SequenceType.SINGLE_STRING};
	
	/**
	 * Result type: any sequence
	 */
	private static final SequenceType RESULT_TYPE = SequenceType.STRING_SEQUENCE;
	
	/**
	 * HighlightingService
	 */
	private final HighlightingService highlightingService;
	
	/**
	 * function object holder. will be initialized in the constructor.
	 */
	private final HighlightingXsltExtensionFunction functionObjectHolder;
	
	/**
	 * Only Constructor, takes the highlighting service
	 * @param hl
	 */
	public HighlightingXsltExtensionDefinition(HighlightingService hl) {
		super();
		this.highlightingService = hl;
		this.functionObjectHolder = new HighlightingXsltExtensionFunction(highlightingService);
	}
	
	
	@Override
	public SequenceType[] getArgumentTypes() {
		return ARGUMENT_TYPES;
	}
	
	@Override
	public StructuredQName getFunctionQName() {
		return FUNCTION_QNAME;
	}
	
	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return RESULT_TYPE;
	}
	
	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return this.functionObjectHolder;
	}
	
	@Override
	public int getMinimumNumberOfArguments() {
		return 2;
	}
	
	@Override
	public int getMaximumNumberOfArguments() {
		return 4;
	}
	

}
