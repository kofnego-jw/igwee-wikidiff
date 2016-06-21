package at.ac.uibk.igwee.metadata.httpclient;

/**
 * ExceptionWrapper. 
 * @author joseph
 *
 */
public class HttpClientException extends Exception {
	
	private static final long serialVersionUID = 201504010935L;
	
	public HttpClientException() {
		super();
	}
	
	public HttpClientException(String msg) {
		super(msg);
	}
	
	public HttpClientException(Throwable cause) {
		super(cause);
	}
	
	public HttpClientException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
