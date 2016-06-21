package at.ac.uibk.igwee.lucene.api.searching;

import at.ac.uibk.igwee.lucene.api.LuceneException;

public class LuceneSearchException extends LuceneException {
	
	private static final long serialVersionUID = 201310272200L;
	
	public LuceneSearchException() {
		super();
	}
	
	public LuceneSearchException(String msg) {
		super(msg);
	}
	
	public LuceneSearchException(Throwable cause) {
		super(cause);
	}
	
	public LuceneSearchException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
