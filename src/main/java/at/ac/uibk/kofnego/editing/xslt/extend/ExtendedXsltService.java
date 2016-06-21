package at.ac.uibk.kofnego.editing.xslt.extend;

import at.ac.uibk.igwee.xslt.XsltService;

import java.util.List;

/**
 * This interface defines the extended Xslt Service.
 * It provides no further methods than the XsltService, however,
 * the implementation should provider custom functions.
 *
 * Created by joseph on 25.02.16.
 */
public interface ExtendedXsltService extends XsltService {

    /**
     * @return a list of customfunctions (=IDs), or an empty list if non implemented.
     */
    List<String> listCustomFunctions();

}
