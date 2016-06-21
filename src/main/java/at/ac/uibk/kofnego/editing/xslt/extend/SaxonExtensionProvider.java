package at.ac.uibk.kofnego.editing.xslt.extend;

import net.sf.saxon.lib.ExtensionFunctionDefinition;

/**
 * This interface should be implemented by all bundles that provides an extension function.
 *
 * Created by joseph on 25.02.16.
 */
public interface SaxonExtensionProvider {

    /**
     * The implementation should provide an id for each function. Best practise:
     * NamespaceURI:FunctionName.
     * E.g. "http://www.uibk.ac.at/igwee/ns:diff
     * @return the ID of the function
     */
    String getExtensionId();

    /**
     *
     * @return the function definition
     */
    ExtensionFunctionDefinition getExtensionFunctionDefinition();

}
