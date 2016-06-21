package at.ac.uibk.igwee.lucene.api.searching;

import at.ac.uibk.igwee.lucene.api.indexing.FacetContent;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Joseph on 29.03.2016.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class FacetSearchResult {

    private int childCount;

    private FacetContent facetContent;

    private List<FacetSearchResult> children;

    private double value;

    public FacetSearchResult() {

    }

    public FacetSearchResult(int childCount, String dimension, List<FacetSearchResult> children, String[] path, double value) {
        this.childCount = childCount;
        this.facetContent = new FacetContent(dimension, path);
        this.children = children;
        this.value = value;
    }

    public FacetSearchResult(int childCount, FacetContent baseContent, Map<FacetContent, Double> children, double value) {
        this.facetContent = baseContent;
        this.childCount = childCount;
        this.value = value;
        if (children==null || children.isEmpty()) this.children = Collections.emptyList();
        else {
            this.children = children.entrySet().stream()
                    .map(entry -> new FacetSearchResult(-1, entry.getKey(), null, entry.getValue()))
                    .collect(Collectors.toList());
        }
    }

    public int getChildCount() {
        return childCount;
    }

    public String getDimension() {
        return facetContent.getDimension();
    }

    public List<FacetSearchResult> getChildren() {
        return children;
    }

    public String[] getPath() {
        return facetContent.getPath();
    }

    public double getValue() {
        return value;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public FacetContent getFacetContent() {
        return facetContent;
    }

    public void setFacetContent(FacetContent facetContent) {
        this.facetContent = facetContent;
    }

    public void setChildren(List<FacetSearchResult> children) {
        this.children = children;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public FacetContent toFacetContent() {
        return facetContent;
    }
    public boolean isRoot() {
        return facetContent.isRoot();
    }

    @Override
    public String toString() {
        return "FacetSearchResult{" +
                "childCount=" + childCount +
                ", facetContent=" + facetContent +
                ", children=" + children +
                ", value=" + value +
                '}';
    }
}
