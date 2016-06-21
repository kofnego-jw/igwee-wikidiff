package at.ac.uibk.igwee.xstream.api;

public class XStreamServiceException extends Exception {
	
	private static final long serialVersionUID = 201311060748L;
	
	public XStreamServiceException() {
		super();
	}
	public XStreamServiceException(String msg) {
		super(msg);
	}
	public XStreamServiceException(Throwable cause) {
		super(cause);
	}
	public XStreamServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
