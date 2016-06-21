package at.ac.uibk.kofnego.editing.xslt.extend.wiki.xslt.extend.lucene;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;
import at.ac.uibk.igwee.lucene.api.highlighting.HighlightingService;
import at.ac.uibk.kofnego.editing.xslt.extend.SaxonExtensionProvider;
import net.sf.saxon.lib.ExtensionFunctionDefinition;

/**
 * The ExtensionProvider for Highlight function.
 *
 * Created by joseph on 2/29/16.
 */
@Component(provide = SaxonExtensionProvider.class)
public class HighlightSaxonExtensionProvider implements SaxonExtensionProvider {

    public static final String EXTENSION_ID = QNameConstants.NAMESPACE_URL + ":" + QNameConstants.HIGHLIGHT_FUNCTIONNAME;

    private HighlightingService highlightingService;
    @Reference
    public void setHighlightingService(HighlightingService highlightingService) {
        this.highlightingService = highlightingService;
    }

    @Override
    public String getExtensionId() {
        return EXTENSION_ID;
    }

    @Override
    public ExtensionFunctionDefinition getExtensionFunctionDefinition() {
        return new HighlightingXsltExtensionDefinition(highlightingService);
    }
}
