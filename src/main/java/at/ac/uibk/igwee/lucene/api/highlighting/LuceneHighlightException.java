package at.ac.uibk.igwee.lucene.api.highlighting;

import at.ac.uibk.igwee.lucene.api.LuceneException;

public class LuceneHighlightException extends LuceneException {
	
	private static final long serialVersionUID = 201502091157L;
	
	public LuceneHighlightException() {
		super();
	}
	
	public LuceneHighlightException(String msg) {
		super(msg);
	}
	
	public LuceneHighlightException(Throwable e) {
		super(e);
	}
	
	public LuceneHighlightException(String msg, Throwable e) {
		super(msg, e);
	}

}
