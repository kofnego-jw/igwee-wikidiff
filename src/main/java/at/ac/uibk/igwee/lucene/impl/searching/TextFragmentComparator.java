package at.ac.uibk.igwee.lucene.impl.searching;

import java.util.Comparator;

import org.apache.lucene.search.highlight.TextFragment;

public class TextFragmentComparator implements Comparator<TextFragment> {

	@Override
	public int compare(TextFragment o1, TextFragment o2) {
		
		if (o1==null) {
			return (o2==null) ? 0 : -1;
		}
		
		if (o2==null) {
			return 1;
		}
		
		return o1.getFragNum() - o2.getFragNum();
	}

}
