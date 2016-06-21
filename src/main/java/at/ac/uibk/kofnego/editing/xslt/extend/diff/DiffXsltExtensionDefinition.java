package at.ac.uibk.kofnego.editing.xslt.extend.diff;

import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

/**
 * Definition of the highlighting function.
 * @author joseph
 *
 */
public class DiffXsltExtensionDefinition extends ExtensionFunctionDefinition {
	
	
	private static final long serialVersionUID = 201502101429L;

	/**
	 * StructuredQName for the function
	 */
	private static final StructuredQName FUNCTION_QNAME = new StructuredQName(QNameConstants.DEFAULT_PREFIX, 
			QNameConstants.NAMESPACE_URL, QNameConstants.FUNCTIONNAME);
	
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
	 * function object holder. will be initialized in the constructor.
	 */
	private final DiffXsltExtensionFunction functionObjectHolder;

	/**
	 * The DiffMatchPatch object used for the text analysis
	 */
	private final DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
	
	/**
	 * Only Constructor, takes the highlighting service
	 */
	public DiffXsltExtensionDefinition() {
		super();
		this.functionObjectHolder = new DiffXsltExtensionFunction(diffMatchPatch);
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
