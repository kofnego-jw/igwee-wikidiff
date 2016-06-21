package at.ac.uibk.kofnego.editing.xslt.extend.wiki;

import aQute.bnd.annotation.component.Component;
import at.ac.uibk.kofnego.editing.xslt.extend.SaxonExtensionProvider;
import net.sf.saxon.lib.ExtensionFunctionDefinition;

/**
 * Created by Joseph on 07.03.2016.
 */
@Component
public class WikiSaxonExtensionFunctionProvider implements SaxonExtensionProvider {

    @Override
    public String getExtensionId() {
        return QNameConstants.NAMESPACE_URI + ":" + QNameConstants.FUNCTION_NAME;
    }

    @Override
    public ExtensionFunctionDefinition getExtensionFunctionDefinition() {
        return new WikiExtensionFunctionDefinition();
    }
}
