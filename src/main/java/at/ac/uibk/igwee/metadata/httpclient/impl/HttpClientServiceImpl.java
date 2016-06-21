package at.ac.uibk.igwee.metadata.httpclient.impl;

import at.ac.uibk.igwee.metadata.httpclient.HttpClientException;
import at.ac.uibk.igwee.metadata.httpclient.HttpClientService;
import at.ac.uibk.igwee.metadata.httpclient.HttpMethod;
import at.ac.uibk.igwee.metadata.httpclient.ParameterPair;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation
 * @author totoro
 *
 */
//@Component(provide=HttpClientService.class)
public class HttpClientServiceImpl implements HttpClientService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientServiceImpl.class);
	
	/**
	 * Default scheme: http
	 */
	protected static final String DEFAULT_SCHEME = "http"; 
	protected static final String SECURE_SCHEME = "https";

	public HttpClientServiceImpl() {
		super();
	}

	@Override
	public ByteArrayInputStream executeHttp(String host, int port, String path, List<ParameterPair> parameters, HttpMethod method, String encoding) throws HttpClientException {
		return execute(host, port, path, parameters, method, encoding, false);
	}

	@Override
	public ByteArrayInputStream executeHttps(String host, int port, String path, List<ParameterPair> parameters, HttpMethod method, String encoding) throws HttpClientException {
		return execute(host, port, path, parameters, method, encoding, true);
	}

	public ByteArrayInputStream execute(String host, int port, String path,
										List<ParameterPair> parameters, HttpMethod method, String encoding, boolean secure)
			throws HttpClientException {
		
		LOGGER.debug("Executing HttpClient.");
		
		Charset charset = getCharset(encoding);
		
		CloseableHttpClient client = null;
		
		CloseableHttpResponse resp = null;
		
		ByteArrayInputStream result = null;
		
		try {
			
			client = HttpClientBuilder.create().build();
			
			URIBuilder builder = new URIBuilder();
			builder.setScheme(secure ? SECURE_SCHEME : DEFAULT_SCHEME);
			builder.setHost(host);
			if (port!=DEFAULT_PORT)
					builder.setPort(port);
			builder.setPath(path)
					.setCharset(charset);
			
			if (method==HttpMethod.GET && parameters!=null && !parameters.isEmpty()) {
				parameters.stream().forEach(pp -> builder.addParameter(pp.getKey(), pp.getValue()));
			}
			
			URI uri = builder.build();
			
			switch (method) {
			case POST:
				
				LOGGER.info("Executing HttpPOST: {}", uri);
				
				HttpPost post = new HttpPost(uri);
				if (parameters!=null && !parameters.isEmpty()) {
					List<NameValuePair> params = parameters.stream()
							.map(pp -> new BasicNameValuePair(pp.getKey(), pp.getValue()))
							.collect(Collectors.toList());
					post.setEntity(new UrlEncodedFormEntity(params));
				}
				resp = client.execute(post);
				break;
			
			case GET:
				
				LOGGER.info("Executing HttpGET: {}", uri);
				
				HttpGet get = new HttpGet(uri);
				resp = client.execute(get);
			}
			
			if (resp==null) {
				throw new HttpClientException("HttpResponse is null.");
			}
			
			if (resp.getStatusLine()==null) {
				throw new HttpClientException("HttpStatus is null.");
			}
			
			if (resp.getStatusLine().getStatusCode()>=400) {
				throw new HttpClientException("Http call failed: " 
						+ resp.getStatusLine().getReasonPhrase());
			}
			
			LOGGER.debug("Http success with {} {}.", 
					Integer.toString(resp.getStatusLine().getStatusCode()), 
					resp.getStatusLine().getReasonPhrase());
			
			HttpEntity entity = resp.getEntity();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(entity.getContent(), baos);
			
			result = new ByteArrayInputStream(baos.toByteArray());
			
		} catch (Exception e) {
			
			LOGGER.error("Cannot execute HTTP.", e);
			throw new HttpClientException("Cannot execute HTTP: " + e.getMessage(), e);
			
		} finally {
			if (client!=null) {
				try {
					client.close();
				} catch (Exception e2) {
					LOGGER.warn("Exception while closing the HttpClient.", e2);
				}
			}
			
			if (resp!=null) {
				try {
					resp.close();
				} catch (Exception e2) {
					LOGGER.warn("Exception while closing the HttpResponse.", e2);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param encoding
	 * @return the corresponding charset
	 * @throws HttpClientException if the charset is not available
	 */
	public static Charset getCharset(String encoding) throws HttpClientException {
		try {
			return Charset.forName(encoding);
		} catch (Exception e) {
			throw new HttpClientException("Cannot process charset with name '" + encoding + "'.", e);
		}
	}

}
