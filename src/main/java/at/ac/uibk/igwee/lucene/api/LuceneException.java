package at.ac.uibk.igwee.lucene.api;
/**
 * Lucene Exception. Used for all exceptions within the bundle.
 * @author Joseph
 *
 */
public class LuceneException extends Exception {
	
	private static final long serialVersionUID = 201310271922L;
	
	public LuceneException() {
		super();
	}
	
	public LuceneException(Throwable cause) {
		super(cause);
	}
	
	public LuceneException(String msg) {
		super(msg);
	}
	
	public LuceneException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
