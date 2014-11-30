package org.asn.web_test;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.asn.web_test.api.HttpTest;
import org.asn.web_test.api.HttpTestImpl;
import org.asn.web_test.model.FormModel;
import org.junit.Test;


/**
 * Hello world!
 * 
 */
public class App {

	private static HttpTest httpTest = new HttpTestImpl();
	
	/*public static void main(String[] args) {
		doJsonPost();
		doJsonGet();
		doPlainGet();
		doPlainPost();
	}*/

	@Test
	public void doPlainPost() {
		String res = httpTest.doPlainPost("/plainPost", new NameValuePair[]{new BasicNameValuePair("name", "Tapu")});
		System.out.println(res);
	}

	@Test
	public void doPlainGet() {
		String res = httpTest.doPlainGet("/plainGet", null);
		System.out.println(res);
	}

	@Test
	public void doJsonPost() {
		FormModel formModel = new FormModel();
		formModel.setName("Abhishek");
		formModel.setAddress("Pune");
		FormModel frmMdl = httpTest.doJsonPost("/jsonPost", formModel, FormModel.class);
		System.out.println(frmMdl);
	}
	
	@Test
	public void doJsonGet() {		
		FormModel frmMdl = httpTest.doJsonGet("/jsonGet", null, FormModel.class);
		System.out.println(frmMdl);
	}
	
}
