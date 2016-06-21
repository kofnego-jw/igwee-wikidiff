package at.ac.uibk.igwee.lucene.impl;

import at.ac.uibk.igwee.lucene.api.indexing.FacetSetting;
import at.ac.uibk.igwee.lucene.api.indexing.IndexSetting;
import org.apache.lucene.facet.FacetsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * FacetsConfigHolder, caches FacetsConfigHolder.
 *
 * Created by Joseph on 06.04.2016.
 */
public class FacetsConfigHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacetsConfigHolder.class);

    private final Map<String,FacetsConfig> facetsConfigMap = new HashMap<>();

    public FacetsConfigHolder(Collection<IndexSetting> indexSettings) {
        LOGGER.debug("Initialize FacetsConfigHolder.");
        if (indexSettings!=null) {
//            LOGGER.debug("Initialize FacetsConfigHolder with {} IndexSettings.", indexSettings.size());
            for (IndexSetting is: indexSettings) {
                FacetsConfig fc = createFacetsConfig(is);
//                LOGGER.debug("FacetsConfig for {} created.", is.getIndexName());
                facetsConfigMap.put(is.getIndexName(), fc);
            }
        }
    }

    /**
     *
     * @param indexname Indexname
     * @return A FacetsConfig or null if the indexsetting does not contain facetsettings.
     */
    public FacetsConfig getFacetsConfig(String indexname) {
        return facetsConfigMap.get(indexname);
    }

    /**
     *
     * @param is IndexSetting
     * @return FacetsConfig object or null, if is is null or if is doesn't contain facetsSettings
     */
    public static FacetsConfig createFacetsConfig(IndexSetting is) {
        if (is==null || is.getFacetSettings()==null || is.getFacetSettings().isEmpty())
            return null;
        LOGGER.debug("Create FacetsConfig for {}.", is.getIndexName());
        FacetsConfig facetsConfig = new FacetsConfig();
        for (FacetSetting fs: is.getFacetSettings()) {
            facetsConfig.setHierarchical(fs.getDim(), fs.isHierachical());
            if (fs.getIndexfieldname()!=null && !fs.getIndexfieldname().isEmpty())
                facetsConfig.setIndexFieldName(fs.getDim(), fs.getIndexfieldname());
            facetsConfig.setMultiValued(fs.getDim(), fs.isMultivalued());
            facetsConfig.setRequireDimCount(fs.getDim(), fs.isRequireDimCount());
        }
        return facetsConfig;
    }

}
