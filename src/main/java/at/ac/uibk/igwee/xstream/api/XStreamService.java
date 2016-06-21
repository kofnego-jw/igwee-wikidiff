package at.ac.uibk.igwee.xstream.api;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class XStreamService {
	
	public static final String ENCODING = "UTF-8";
	public static final String PROLOG = "<?xml version=\"1.0\" encoding=\""+ ENCODING + "\"?>";
	
	public static String fromInputStream(InputStream in) throws XStreamServiceException {
		try {
			return IOUtils.toString(in, ENCODING);
		} catch (Exception e) {
			throw new XStreamServiceException("Cannot read from InputStream.", e);
		}
	}
	
	public static void toOutputStream(String text, OutputStream os) 
			throws XStreamServiceException {
		try {
			if (!text.startsWith("<?xml "))
				text = PROLOG + text;
			IOUtils.write(text, os, ENCODING);
		} catch (Exception e) {
			throw new XStreamServiceException("Cannot write to OutputStream.", e);
		} finally {
			try {
				if (os!=null) os.close();
			} catch (Exception ignored) {}
		}
	}
	
}
