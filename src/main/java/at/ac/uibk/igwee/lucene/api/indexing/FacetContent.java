package at.ac.uibk.igwee.lucene.api.indexing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.ac.uibk.igwee.lucene.api.Constants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class models a Facet. A Facet is a taxonomical string which has several components, e.g.
 * "subject heading", "philosophy", "metaphysics", "ontology".
 * 
 * @author Joseph
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
		setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacetContent {
	
	/**
	 * List of components.
	 */
	private final List<String> components = new ArrayList<>();
	
	private FacetContent() {
		super();
	}
	
	/**
	 * @param component a String in the form "subject heading|philosophy|metaphysics|ontology".
	 */
	public FacetContent(String component) {
		this(component, Constants.FACET_SEPARATOR);
	}
	/**
	 * Each component as array of String.
	 * @param components
	 */
	public FacetContent(String... components) {
		this();
		this.components.addAll(Arrays.asList(components));
	}
	/**
	 * 
	 * @param components Components in List of String.
	 */
	public FacetContent(List<String> components) {
		this();
		this.components.addAll(components);
	}
	
	/**
	 * 
	 * @param text Component as a string
	 * @param deliminator custom deliminator
	 */
	public FacetContent(String text, char deliminator) {
		this(changeStringToComponent(new ArrayList<>(), normalizeString(text, deliminator), Character.toString(deliminator)));
	}

    public FacetContent(String dim, String[] path) {
        super();
        this.components.add(dim);
        this.components.addAll(Arrays.asList(path));
    }

    /**
     *
     * @return the dimension or null if nothing is here.
     */
    public String getDimension() {
        if (this.components==null || this.components.isEmpty()) return null;
        return this.components.get(0);
    }

    /**
     *
     * @return the path (without dimension) or null if nothing here. Can be an empty array.
     */
    public String[] getPath() {
        if (this.components==null || this.components.isEmpty()) return null;
        return this.components.subList(1, this.components.size()).toArray(new String[0]);
    }

    /**
     *
     * @return the last component of this facetContent or null if no component available
     */
    public String getLastComponent() {
        if (this.components==null || this.components.isEmpty()) return null;
        return this.components.get(this.components.size()-1);
    }


    /**
     *
     * @param possibleChild possible child?
     * @return true if this node equals the parent node of possible child
     */
	public boolean isParentOf(FacetContent possibleChild) {
		return possibleChild.getParentNode().equals(this);
	}

    /**
     *
     * @param possibleDescendant possible descendant
     * @return true if this node is an ancestor of possibleDescendant
     */
    public boolean isAncestorOf(FacetContent possibleDescendant) {
        return testAncestor(possibleDescendant);
    }

    private boolean testAncestor(FacetContent other) {
        if (other==null) return false;
        if (this.equals(other.getParentNode())) return true;
        return testAncestor(other.getParentNode());
    }
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FacetContent [components=" + components + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((components == null) ? 0 : components.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FacetContent other = (FacetContent) obj;
		if (components == null) {
			if (other.components != null)
				return false;
		} else if (!components.equals(other.components))
			return false;
		return true;
	}

	/**
	 * @return the components
	 */
	public List<String> getComponents() {
		return components;
	}

    /**
     *
     * @return the parent node or null if this is already the root.
     */
	public FacetContent getParentNode() {
		if (this.components.size()<=1) return null;
		return new FacetContent(this.components.subList(0, this.components.size()-1));
	}

    /**
     *
     * @return true if it is the root, i.e. dimension node
     */
    public boolean isRoot() {
        return this.components.size()==1;
    }

	/**
	 * Static method to kill delim at the beginning and at the end of a string
	 * @param string
	 * @param delim
	 * @return
	 */
	public static String normalizeString(String string, char delim) {
		String del = Character.toString(delim);
		while (string.startsWith(del)) {
			string = string.substring(1);
		}
		while (string.endsWith(del) && string.length()>0) {
			string = string.substring(0, string.length()-1);
		}
		return string.trim();
	}
	
	/**
	 * Split the tail at delim, add the head to the heads and process the rest recursively.
	 * @param heads
	 * @param tail
	 * @param delim
	 * @return
	 */
	public static List<String> changeStringToComponent(List<String> heads, String tail, String delim) {
		if (!tail.contains(delim)) {
			return addToList(heads, tail);
		}
		int split = tail.indexOf(delim);
		String toAdd = tail.substring(0, split);
		tail = tail.substring(split+1);
		return changeStringToComponent(addToList(heads,toAdd), tail, delim);
	}
	
	/**
	 * Adds a tail to the head. normalizes the tail first. if tail is null or empty, head will be returned.
	 * @param head
	 * @param tail
	 * @return
	 */
	public static List<String> addToList(List<String> head, String tail) {
		tail = tail.trim();
		if (tail!=null && !tail.isEmpty()) 
			head.add(tail);
		return head;
	}

    /**
     * The default root facet content, has no components.
     */
	public static final FacetContent ROOT_FACET_CONTENT =
            new FacetContent();

}
