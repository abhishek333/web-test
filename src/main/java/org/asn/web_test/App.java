package org.asn.web_test;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.asn.web_test.api.HttpTestImpl;
import org.asn.web_test.model.FormModel;
import org.asn.web_test.model.LoginModel;


/**
 * Hello world!
 * 
 */
public class App extends HttpTestImpl{
	
	public static void main(String[] args) {
		App app = new App();
		app.doLogin();
		app.doLoginCheck();
		/*doJsonGet();
		doPlainGet();
		doPlainPost();*/
	}
	
	public void doPlainPost() {
		String res = doPlainPost("/plainPost", new NameValuePair[]{new BasicNameValuePair("name", "Tapu")});
		System.out.println(res);
	}
	
	public void doPlainGet() {
		String res = doPlainGet("/plainGet", null);
		System.out.println(res);
	}

	public void doLogin() {
		LoginModel loginModel = new LoginModel("abhishek", "123");
		String res = doJsonPost("/rest/auth/login.json", loginModel);
		System.out.println(res);
	}
	
	public void doLoginCheck() {		
		Map<String, String> params = new HashMap<String, String>();
		params.put("param", "123");
		String res = doJsonPost("/rest/auth/logincheck.json", params);
		System.out.println(res);
	}
	
	public void doJsonGet() {		
		FormModel frmMdl = doJsonGet("/jsonGet", null, FormModel.class);
		System.out.println(frmMdl);
	}
	
}
