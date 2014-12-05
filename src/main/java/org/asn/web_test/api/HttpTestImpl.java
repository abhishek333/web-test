/**
 * 
 */
package org.asn.web_test.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Abhishek
 * 
 */
public class HttpTestImpl implements HttpTest {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final String host;
	private final String scheme;
	private final String context;
	private final int port;
	private final ObjectMapper mapper = new ObjectMapper();
	private final HttpContext httpContext;
	
	public HttpTestImpl() {
		LOG.debug("HttpTestImpl instance created.");
		Properties httpProperties = new Properties();
		try {
			httpProperties.load(this.getClass().getClassLoader()
					.getResourceAsStream("http-config.properties"));
		} catch (IOException e) { 
			e.printStackTrace();
		}
		host = httpProperties.getProperty("host", "localhost");
		scheme = httpProperties.getProperty("scheme", "http");
		context = httpProperties.getProperty("context", "appName");
		port = Integer.parseInt(httpProperties.getProperty("port", "6060"));
		
		CookieStore cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.asn.web_test.api.HttpTest#doPlainPost(java.util.List)
	 */
	public String doPlainPost(String path, NameValuePair[] urlParameters) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			URI uri = buildUri(path, urlParameters);
			LOG.debug("performing plain post to {}", uri.toURL());
			HttpPost httpget = new HttpPost(uri);
			// Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            	
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                    	StringBuffer buffer = new StringBuffer("Unexpected response status: ");
                    	buffer.append(status);
                    	buffer.append(" ");
                    	buffer.append(response.getStatusLine().getReasonPhrase());
                        throw new ClientProtocolException(buffer.toString());
                    }
                }

            };
						
			return httpclient.execute(httpget, responseHandler, httpContext);

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.asn.web_test.api.HttpTest#doJsonPost(java.lang.Object)
	 */
	public <T> T doJsonPost(String path, Object requestBody, final Class<T> entityClass) {
		T response = null;	
		CloseableHttpClient httpclient = HttpClients.createDefault();		
		try {
			URI uri = buildUri(path);
			
			HttpPost httppost = new HttpPost(uri);
			// set the Content-type
			httppost.setHeader("Content-type", "application/json");
			httppost.setHeader("Accept", "application/json");
			
			String jsonStr = mapper.writer().writeValueAsString(requestBody);			
			LOG.debug("data: {}", jsonStr);
			// add the JSON as a StringEntity 
			httppost.setEntity(new StringEntity(jsonStr));					
			
			LOG.debug("performing JSON request: {}", httppost.getRequestLine());
			
			// Create a custom response handler
            ResponseHandler<T> responseHandler = new ResponseHandler<T>() {
            	
                public T handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? (T)mapper.readValue(EntityUtils.toString(entity), entityClass) : null;
                    } else {
                    	StringBuffer buffer = new StringBuffer("Unexpected response status: ");
                    	buffer.append(status);
                    	buffer.append(" ");
                    	buffer.append(response.getStatusLine().getReasonPhrase());
                        throw new ClientProtocolException(buffer.toString());
                    }
                }

            };
			
			response = (T) httpclient.execute(httppost, responseHandler, httpContext);			

		} catch (IOException | URISyntaxException e) {
			LOG.error("Request Failed! {}", e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.asn.web_test.api.HttpTest#doPlainGet(java.util.List)
	 */
	public String doPlainGet(String path, NameValuePair[] urlParameters) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			URI uri = buildUri(path, urlParameters);
			LOG.debug("performing plain get to {}", uri.toURL());
			HttpGet httpget = new HttpGet(uri);
			
			// Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            	
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                    	StringBuffer buffer = new StringBuffer("Unexpected response status: ");
                    	buffer.append(status);
                    	buffer.append(" ");
                    	buffer.append(response.getStatusLine().getReasonPhrase());
                        throw new ClientProtocolException(buffer.toString());
                    }
                }

            };
						
			return httpclient.execute(httpget, responseHandler);

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.asn.web_test.api.HttpTest#doJsonGet(java.util.List)
	 */
	public <T> T doJsonGet(String path, NameValuePair[] urlParameters, final Class<T> entityClass) {
		CloseableHttpClient httpclient = HttpClients.createDefault();		
		T response = null;		
		try {
			URI uri = buildUri(path, urlParameters);
			LOG.debug("performing json GET: {}", uri.toURL());
			HttpGet httpget = new HttpGet(uri);
			
			// Create a custom response handler
            ResponseHandler<T> responseHandler = new ResponseHandler<T>() {
            	
                public T handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? (T)mapper.readValue(EntityUtils.toString(entity), entityClass) : null;
                    } else {
                    	StringBuffer buffer = new StringBuffer("Unexpected response status: ");
                    	buffer.append(status);
                    	buffer.append(" ");
                    	buffer.append(response.getStatusLine().getReasonPhrase());
                        throw new ClientProtocolException(buffer.toString());
                    }
                }

            };
			
			response = (T) httpclient.execute(httpget, responseHandler, httpContext);	

		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		CloseableHttpClient httpclient = HttpClients.createDefault();		
		try {
			URI uri = buildUri(path);
			
			HttpPost httppost = new HttpPost(uri);
			// set the Content-type
			httppost.setHeader("Content-type", "application/json");
			httppost.setHeader("Accept", "application/json");
			
			String jsonStr = (data!=null)? mapper.writer().writeValueAsString(data) : "";			
			LOG.debug("data: {}", jsonStr);
			// add the JSON as a StringEntity 
			httppost.setEntity(new StringEntity(jsonStr));					
			
			LOG.debug("performing JSON request: {}", httppost.getRequestLine());
			
			// Create a custom response handler
			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				
				public String handleResponse(
						final HttpResponse response) throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity) : null;
					} else {
						StringBuffer buffer = new StringBuffer("Unexpected response status: ");
						buffer.append(status);
						buffer.append(" ");
						buffer.append(response.getStatusLine().getReasonPhrase());
						throw new ClientProtocolException(buffer.toString());
					}
				}
				
			};
			
			response = httpclient.execute(httppost, responseHandler, httpContext);			
			
		} catch (IOException | URISyntaxException e) {
			LOG.error("Request Failed! {}", e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	// helper method to convert InputStream to String
	/*private String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}*/

}
