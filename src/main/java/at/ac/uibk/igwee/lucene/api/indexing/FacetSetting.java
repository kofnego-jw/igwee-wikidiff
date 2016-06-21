package at.ac.uibk.igwee.lucene.api.indexing;

/**
 * Setting for facet indexing.
 *
 *
 * Created by Joseph on 29.03.2016.
 */
public class FacetSetting {

    private String dim;
    private boolean hierachical = true;
    private boolean multivalued = true;
    private String indexfieldname;
    private boolean requireDimCount = false;

    public FacetSetting() {

    }

    public FacetSetting(String dim, boolean hierachical, boolean multivalued, String indexfieldname, boolean requireDimCount) {
        this.dim = dim;
        this.hierachical = hierachical;
        this.multivalued = multivalued;
        this.indexfieldname = indexfieldname;
        this.requireDimCount = requireDimCount;
    }

    public String getDim() {
        return dim;
    }

    public void setDim(String dim) {
        this.dim = dim;
    }

    public boolean isHierachical() {
        return hierachical;
    }

    public void setHierachical(boolean hierachical) {
        this.hierachical = hierachical;
    }

    public boolean isMultivalued() {
        return multivalued;
    }

    public void setMultivalued(boolean multivalued) {
        this.multivalued = multivalued;
    }

    public String getIndexfieldname() {
        return indexfieldname;
    }

    public void setIndexfieldname(String indexfieldname) {
        this.indexfieldname = indexfieldname;
    }

    public boolean isRequireDimCount() {
        return requireDimCount;
    }

    public void setRequireDimCount(boolean requireDimCount) {
        this.requireDimCount = requireDimCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FacetSetting that = (FacetSetting) o;

        return dim.equals(that.dim);

    }

    @Override
    public int hashCode() {
        return dim.hashCode();
    }

    @Override
    public String toString() {
        return "FacetSetting{" +
                "dim='" + dim + '\'' +
                ", hierachical=" + hierachical +
                ", multivalued=" + multivalued +
                ", indexfieldname='" + indexfieldname + '\'' +
                ", requireDimCount=" + requireDimCount +
                '}';
    }
}
