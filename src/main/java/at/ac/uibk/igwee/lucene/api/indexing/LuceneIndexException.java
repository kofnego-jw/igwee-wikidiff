package at.ac.uibk.igwee.lucene.api.indexing;

import at.ac.uibk.igwee.lucene.api.LuceneException;

public class LuceneIndexException extends LuceneException {
	
	private static final long serialVersionUID = 201310271929L;
	
	public LuceneIndexException() {
		super();
	}
	
	public LuceneIndexException(String msg) {
		super(msg);
	}
	
	public LuceneIndexException(Throwable cause) {
		super(cause);
	}
	
	public LuceneIndexException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
