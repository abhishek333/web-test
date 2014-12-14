/**
 * 
 */
package org.asn.web_test.api;

import org.apache.http.NameValuePair;
import org.asn.web_test.model.RestResponse;

/**
 * @author Abhishek
 *
 */
public interface HttpTest {

	String doPlainPost(String path, NameValuePair[] urlParameters);
	<T>RestResponse<T> doJsonPostRestResponse(String path, Object data);
	String doJsonPost(String path, Object data);	
	String doPlainGet(String path, NameValuePair[] urlParameters);
	<T> T doJsonGet(String path, NameValuePair[] urlParameters, Class<T> type);
}
