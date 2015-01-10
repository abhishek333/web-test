package org.asn.web_test;

import java.util.List;

import org.asn.web_test.api.HttpTest;
import org.asn.web_test.api.HttpTestImpl;
import org.asn.web_test.model.LoginModel;
import org.asn.web_test.model.RestResponse;
import org.asn.web_test.model.RestResponse.REQ;
import org.asn.web_test.model.User;
import org.asn.web_test.model.UserRegisterationModel;

import static org.junit.Assert.*;


/**
 * Hello world!
 * 
 */
public class App {
	
	private static HttpTest httpTest;
	
	public static void main(String[] args) {		
		App app = new App();	
		httpTest = new HttpTestImpl();
		app.doLogin("user1", "123456");
		List<User> users = app.getUsers();
		System.out.println("users: "+users);
		/*for(int i=0; i<1; i++){			
			//app.doRegister("user"+i);
			REQ req = app.doLogin("user"+i, "123456");
			app.getUserList();
			System.out.println(String.format("%d %s", i, req));
		}*/
		//app.doLogin();
		//app.getUserList();
		/*doJsonGet();
		doPlainGet();
		doPlainPost();*/
	}
	
	private void getUserList() {
		httpTest.doJsonPost("/user/get/users.json",null);
	}

	private void doRegister(String username) {
		UserRegisterationModel userRegisterationModel = new UserRegisterationModel();
		userRegisterationModel.setUserName(username);
		userRegisterationModel.setPassword("123456");
		httpTest.doJsonPost("/auth/register.json", userRegisterationModel);
	}

	public REQ doLogin(String username, String password) {
		LoginModel loginModel = new LoginModel(username, password);
		RestResponse<String> response = httpTest.doJsonPostRestResponse("/auth/login.json", loginModel);
		return (response!=null)?response.getSuccess() : REQ.FAILED;
	}
	
	public List<User> getUsers() {		
		RestResponse<List<User>> response = httpTest.<List<User>>doJsonPostRestResponse("/user/get/users-rest.json", null);
		return (response!=null)?response.getResponseContent() : null;
	}
}
