package at.ac.uibk.igwee.lucene.rest.helper;

import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by joseph on 5/24/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacetFW {

    public List<String> components;

    public FacetFW() {

    }

    public FacetFW(List<String> components) {
        this.components = components;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return "FacetFW{" +
                "components=" + components +
                '}';
    }
}
