package at.ac.uibk.kofnego.editing.xslt.extend.diff;

import aQute.bnd.annotation.component.Component;
import at.ac.uibk.kofnego.editing.xslt.extend.SaxonExtensionProvider;
import net.sf.saxon.lib.ExtensionFunctionDefinition;

/**
 * Created by totoro on 29.02.16.
 */
@Component(provide = SaxonExtensionProvider.class)
public class DiffSaxonExtensionProvider implements SaxonExtensionProvider {

    public static final String EXTENSION_ID = QNameConstants.NAMESPACE_URL + ":" + QNameConstants.FUNCTIONNAME;

    @Override
    public String getExtensionId() {
        return EXTENSION_ID;
    }

    @Override
    public ExtensionFunctionDefinition getExtensionFunctionDefinition() {
        return new DiffXsltExtensionDefinition();
    }
}
