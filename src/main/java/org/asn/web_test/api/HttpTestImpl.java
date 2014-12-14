/**
 * 
 */
package org.asn.web_test.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.asn.web_test.model.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Abhishek
 * 
 */
public class HttpTestImpl implements HttpTest {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private final String host;
	private final String scheme;
	private final String context;
	private final int port;
	private final ObjectMapper mapper = new ObjectMapper();
	private final BasicHttpContext httpClientContext;
	private CloseableHttpClient httpclient;

	public static final HttpTest httpTest = SingletonHelper.build();
	
	public HttpTestImpl() {
		LOG.debug("HttpTestImpl instance created.");
		Properties httpProperties = new Properties();
		try {
			httpProperties.load(this.getClass().getClassLoader()
					.getResourceAsStream("http-config.properties"));
		} catch (IOException e) { 
			LOG.error("Error: {}", e.getMessage(), e);
		}
		host = httpProperties.getProperty("host", "localhost");
		scheme = httpProperties.getProperty("scheme", "http");
		context = httpProperties.getProperty("context", "appName");
		port = Integer.parseInt(httpProperties.getProperty("port", "6060"));
		
		CookieStore cookieStore = new BasicCookieStore();
		httpClientContext = new BasicHttpContext();
		httpClientContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
		httpclient = HttpClients.createDefault();
	}
	
	static class SingletonHelper{

		public static HttpTest build() {
			return new HttpTestImpl();
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.asn.web_test.api.HttpTest#doPlainPost(java.util.List)
	 */
	public String doPlainPost(String path, NameValuePair[] urlParameters) {		
		try {
			URI uri = buildUri(path, urlParameters);			
			HttpPost httpget = new HttpPost(uri);
			LOG.debug("performing JSON request..");
			LOG.debug("URL: {}",httpget.getRequestLine());
			LOG.debug("Request Data: {}",urlParameters);
			// Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            	
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        String responseReceived = entity != null ? EntityUtils.toString(entity) : null;
                        LOG.debug("Response Data: {}",responseReceived);
                        return responseReceived;
                    } else {
                    	String msg = buildExpMsg(status, response.getStatusLine().getReasonPhrase());                    	
                        throw new ClientProtocolException(msg);
                    }
                }
            };
						
			return httpclient.execute(httpget, responseHandler, httpClientContext);

		} catch (IOException | URISyntaxException e) {
			LOG.error("Error: {}", e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.asn.web_test.api.HttpTest#doJsonPost(java.lang.Object)
	 */
	public <T>RestResponse<T> doJsonPostRestResponse(String path, Object requestBody) {
		RestResponse<T> response = null;		
		try {
			URI uri = buildUri(path);
			
			HttpPost httppost = new HttpPost(uri);
			// set the Content-type
			httppost.setHeader("Content-type", "application/json");
			httppost.setHeader("Accept", "application/json");
			
			String jsonStr = mapper.writer().writeValueAsString(requestBody);						
			// add the JSON as a StringEntity 
			httppost.setEntity(new StringEntity(jsonStr));								
			LOG.debug("performing JSON request..");
			LOG.debug("URL: {}",httppost.getRequestLine());
			LOG.debug("Request Data: {}",jsonStr);
			// Create a custom response handler
            ResponseHandler<RestResponse<T>> responseHandler = new ResponseHandler<RestResponse<T>>() {
            	
                public RestResponse<T> handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        String responseReceived = entity != null ? EntityUtils.toString(entity) : null;
                        LOG.debug("Response Data: {}",responseReceived);
                        JsonNode rootNode = mapper.readTree(responseReceived);                        	
                    	JsonNode restResponse = rootNode.get("restResponse");                        	
                    	
                        if(responseReceived != null && restResponse!=null){
                        	String rest = restResponse.toString();
                        	return (RestResponse<T>) mapper.readValue(rest, new TypeReference<RestResponse<T>>(){});
                        }
                          
                        return null;
                    } else {                    	
                    	String msg = buildExpMsg(status, response.getStatusLine().getReasonPhrase());                    	
                        throw new ClientProtocolException(msg);
                    }
                }

            };
			
			response = (RestResponse<T>) httpclient.execute(httppost, responseHandler, httpClientContext);			

		} catch (IOException | URISyntaxException e) {
			LOG.error("Request Failed! {}", e.getMessage(), e);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.asn.web_test.api.HttpTest#doPlainGet(java.util.List)
	 */
	public String doPlainGet(String path, NameValuePair[] urlParameters) {		
		try {
			URI uri = buildUri(path, urlParameters);
			HttpGet httpget = new HttpGet(uri);
			LOG.debug("performing JSON request..");
			LOG.debug("URL: {}",httpget.getRequestLine());
			LOG.debug("Request Data: {}",urlParameters);
			// Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            	
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        String responseReceived = entity != null ? EntityUtils.toString(entity) : null;
                        LOG.debug("Response Data: {}",responseReceived);
                        return responseReceived;                        
                    } else {
                    	String msg = buildExpMsg(status, response.getStatusLine().getReasonPhrase());                    	
                        throw new ClientProtocolException(msg);
                    }
                }

            };
						
			return httpclient.execute(httpget, responseHandler);

		} catch (IOException | URISyntaxException e) {
			LOG.error("Error: {}", e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.asn.web_test.api.HttpTest#doJsonGet(java.util.List)
	 */
	public <T> T doJsonGet(String path, NameValuePair[] urlParameters, final Class<T> entityClass) {		
		T response = null;		
		try {
			URI uri = buildUri(path, urlParameters);			
			HttpGet httpget = new HttpGet(uri);
			LOG.debug("performing JSON request..");
			LOG.debug("URL: {}",httpget.getRequestLine());
			LOG.debug("Request Data: {}",urlParameters);
			// Create a custom response handler
            ResponseHandler<T> responseHandler = new ResponseHandler<T>() {
            	
                public T handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        String responseReceived = entity != null ? EntityUtils.toString(entity) : null;
                        LOG.debug("Response Data: {}",responseReceived);                        
                        return responseReceived != null ? (T)mapper.readValue(responseReceived, entityClass) : null;
                    } else {
                    	String msg = buildExpMsg(status, response.getStatusLine().getReasonPhrase());                    	
                        throw new ClientProtocolException(msg);
                    }
                }

            };
			
			response = (T) httpclient.execute(httpget, responseHandler, httpClientContext);	

		} catch (IOException | URISyntaxException e) {
			LOG.error("Error: {}", e.getMessage(), e);
		}
		return response;
	}

	//helper method to build URI
	public URI buildUri(String path, NameValuePair... urlParameters) throws URISyntaxException{
		URIBuilder uri = new URIBuilder().setScheme(scheme).setHost(host)
				.setPort(port).setPath(context+path);
		if(urlParameters!=null && urlParameters.length > 0){
			uri.setParameters(urlParameters).build();
		}
		return uri.build();
	}

	@Override
	public String doJsonPost(String path, Object data) {
		String response = null;		
		try {
			URI uri = buildUri(path);
			
			HttpPost httppost = new HttpPost(uri);
			// set the Content-type
			httppost.setHeader("Content-type", "application/json");
			httppost.setHeader("Accept", "application/json");
			
			String jsonStr = (data!=null)? mapper.writer().writeValueAsString(data) : "";						
			// add the JSON as a StringEntity 
			httppost.setEntity(new StringEntity(jsonStr));					
			
			LOG.debug("performing JSON request..");
			LOG.debug("URL: {}",httppost.getRequestLine());
			LOG.debug("Request Data: {}",jsonStr);
			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				
				public String handleResponse(
						final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						String responseReceived = entity != null ? EntityUtils.toString(entity) : null;
                        LOG.debug("Response Data: {}",responseReceived);
                        return responseReceived;						
					} else {
						String msg = buildExpMsg(status, response.getStatusLine().getReasonPhrase());                    	
                        throw new ClientProtocolException(msg);
					}
				}
				
			};
			
			response = httpclient.execute(httppost, responseHandler, httpClientContext
					);			
			
		} catch (IOException | URISyntaxException e) {
			LOG.error("Request Failed! {}", e.getMessage(), e);
		}
		return response;
	}

	/**Helper method to build exception message*/
	protected String buildExpMsg(int status, String reasonPhrase) {
		return MessageFormat.format("Unexpected response status: {0}, Reason: {1}", status, reasonPhrase);
	}
	
	public void shutDown(){
		try {
			httpclient.close();
		} catch (IOException e) {
			LOG.error("Error! {}", e.getMessage(), e);
		}		
	}
}
