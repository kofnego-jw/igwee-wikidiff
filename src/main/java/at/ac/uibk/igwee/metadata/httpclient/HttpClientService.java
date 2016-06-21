package at.ac.uibk.igwee.metadata.httpclient;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface HttpClientService {
	
	int DEFAULT_PORT = 80;
	
	String DEFAULT_ENCODING = "utf-8";

	ByteArrayInputStream executeHttp(String host, int port, String path,
			List<ParameterPair> parameters, HttpMethod method, String encoding) 
					throws HttpClientException;

	ByteArrayInputStream executeHttps(String host, int port, String path, List<ParameterPair> parameters,
											 HttpMethod method, String encoding) throws HttpClientException;
	
	default ByteArrayInputStream executeHttp(String host, String path, List<ParameterPair> parameters,
			HttpMethod method, String encoding) throws HttpClientException {
		return executeHttp(host, DEFAULT_PORT,path, parameters, method, encoding);
	}

	default ByteArrayInputStream executeHttps(String host, String path, List<ParameterPair> parameters,
			HttpMethod method, String encoding) throws HttpClientException {
		return executeHttps(host, DEFAULT_PORT,path, parameters, method, encoding);
	}
	
	default ByteArrayInputStream executeHttp(String host, String path, List<ParameterPair> parameters,
			HttpMethod method) throws HttpClientException {
		return executeHttp(host, DEFAULT_PORT,path, parameters, method, DEFAULT_ENCODING);
	}

	default ByteArrayInputStream executeHttps(String host, String path, List<ParameterPair> parameters,
			HttpMethod method) throws HttpClientException {
		return executeHttps(host, DEFAULT_PORT,path, parameters, method, DEFAULT_ENCODING);
	}
	
	default ByteArrayInputStream executeHttpGet(String host, String path, List<ParameterPair> params)
			throws HttpClientException {
		return executeHttp(host, path, params, HttpMethod.GET);
	}

	default ByteArrayInputStream executeHttpsGet(String host, String path, List<ParameterPair> params)
			throws HttpClientException {
		return executeHttps(host, path, params, HttpMethod.GET);
	}
	
	default ByteArrayInputStream executeHttpPost(String host, String path, List<ParameterPair> parameters)
			throws HttpClientException {
		return executeHttp(host, path, parameters, HttpMethod.POST);
	}

	default ByteArrayInputStream executeHttpsPost(String host, String path, List<ParameterPair> parameters)
			throws HttpClientException {
		return executeHttps(host, path, parameters, HttpMethod.POST);
	}
	

}