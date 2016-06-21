package at.ac.uibk.igwee.lucene.rest.helper;

import java.util.List;

/**
 * Created by joseph on 5/25/16.
 */
public class FacetSearchResultFW {

    public int count;

    public FacetFW baseFacet;

    public double value;

    public List<FacetSearchResultFW> children;

    public FacetSearchResultFW() {

    }

    public FacetSearchResultFW(int count, FacetFW baseFacet, double value, List<FacetSearchResultFW> children) {
        this.count = count;
        this.baseFacet = baseFacet;
        this.value = value;
        this.children = children;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public FacetFW getBaseFacet() {
        return baseFacet;
    }

    public void setBaseFacet(FacetFW baseFacet) {
        this.baseFacet = baseFacet;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<FacetSearchResultFW> getChildren() {
        return children;
    }

    public void setChildren(List<FacetSearchResultFW> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "FacetSearchResultFW{" +
                "count=" + count +
                ", baseFacet=" + baseFacet +
                ", value=" + value +
                ", children=" + children +
                '}';
    }
}
