/**
 * 
 */
package org.asn.web_test.api;

import org.apache.http.NameValuePair;

/**
 * @author Abhishek
 *
 */
public interface HttpTest {

	String doPlainPost(String path, NameValuePair[] urlParameters);
	<T> T doJsonPost(String path, Object data, Class<T> entityClass);
	String doPlainGet(String path, NameValuePair[] urlParameters);
	<T> T doJsonGet(String path, NameValuePair[] urlParameters, Class<T> entityClass);
}
