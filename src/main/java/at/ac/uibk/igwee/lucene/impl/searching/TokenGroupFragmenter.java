package at.ac.uibk.igwee.lucene.impl.searching;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.search.highlight.Fragmenter;

/**
 * This fragmenter implementation counts the tokens and group X tokens 
 * together to a fragment. It does not regard the sentence boundaries. 
 * @author Joseph
 *
 */
public class TokenGroupFragmenter implements Fragmenter {
	
	/**
	 * Default group size = 16 tokens
	 */
	private static final int DEFAULT_GROUPSIZE = 16;
	/**
	 * group size
	 */
	private int groupSize = DEFAULT_GROUPSIZE;
	/**
	 * position of the token
	 */
	private int tokenPosition;
	/**
	 * numbers of group preceding
	 */
	private int groupCount;
	/**
	 * PositionIncrementAttribute, used for counting position.
	 */
	private PositionIncrementAttribute positionIncAttribute;
	
	
	public TokenGroupFragmenter() {
		super();
	}
	
	public TokenGroupFragmenter(int groupSize) {
		this();
		if (groupSize>0) this.groupSize = groupSize;
	}

	@Override
	public boolean isNewFragment() {
		tokenPosition += positionIncAttribute.getPositionIncrement();
		if (tokenPosition >= groupCount*groupSize) {
			groupCount = (tokenPosition / groupSize) +1;
			return true;
		}
		return false;
	}

	@Override
	public void start(String originalText, TokenStream tokenStream) {
		positionIncAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);
		this.groupCount = 1;
		this.tokenPosition = 0;
	}

	/**
	 * @return the groupSize
	 */
	public int getGroupSize() {
		return groupSize;
	}

	/**
	 * @param groupSize the groupSize to set
	 */
	public void setGroupSize(int groupSize) {
		if (groupSize<=0) return;
		this.groupSize = groupSize;
	}

	/**
	 * @return the tokenPosition
	 */
	public int getTokenPosition() {
		return tokenPosition;
	}

	/**
	 * @param tokenPosition the tokenPosition to set
	 */
	public void setTokenPosition(int tokenPosition) {
		this.tokenPosition = tokenPosition;
	}

	/**
	 * @return the groupCount
	 */
	public int getGroupCount() {
		return groupCount;
	}

	/**
	 * @param groupCount the groupCount to set
	 */
	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	/**
	 * @return the positionIncAttribute
	 */
	public PositionIncrementAttribute getPositionIncAttribute() {
		return positionIncAttribute;
	}

	/**
	 * @param positionIncAttribute the positionIncAttribute to set
	 */
	public void setPositionIncAttribute(
			PositionIncrementAttribute positionIncAttribute) {
		this.positionIncAttribute = positionIncAttribute;
	}
	
	
	
	
	

}
