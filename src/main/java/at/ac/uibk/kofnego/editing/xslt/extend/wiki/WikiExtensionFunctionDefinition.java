package at.ac.uibk.kofnego.editing.xslt.extend.wiki;

import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.value.SequenceType;

/**
 * Created by totoro on 05.03.16.
 */
public class WikiExtensionFunctionDefinition extends ExtensionFunctionDefinition {

    private static final StructuredQName FUNCTION_NAME = new StructuredQName(QNameConstants.PREFIX,
            QNameConstants.NAMESPACE_URI, QNameConstants.FUNCTION_NAME);

    @Override
    public StructuredQName getFunctionQName() {
        return FUNCTION_NAME;
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[]{
                SequenceType.SINGLE_STRING,
                SequenceType.OPTIONAL_STRING,
                SequenceType.OPTIONAL_STRING
        };
    }

    @Override
    public SequenceType getResultType(SequenceType[] sequenceTypes) {
        return SequenceType.NODE_SEQUENCE;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new WikiExtensionFunctionCall();
    }
}
