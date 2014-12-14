/**
 * 
 */
package org.asn.web_test.model;

/**
 * @author Abhishek
 *
 */
public class RestResponse<T> {

	private REQ success;
	private T responseContent;
	
	public RestResponse(){}	
	
	public RestResponse(REQ success, T responseContent) {
		super();
		this.success = success;
		this.responseContent = responseContent;
	}


	public REQ getSuccess() {
		return success;
	}


	public void setSuccess(REQ success) {
		this.success = success;
	}


	public T getResponseContent() {
		return responseContent;
	}


	public void setResponseContent(T responseContent) {
		this.responseContent = responseContent;
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RestResponse [success=");
		builder.append(success);
		builder.append(", responseContent=");
		builder.append(responseContent);
		builder.append("]");
		return builder.toString();
	}


	public enum REQ {
		SUCCESS("1"), FAILED("0");
		
		private String val;
		REQ(String val){
			this.val = val;
		}
		
		public String getVal(){
			return val;
		}
		
	}
}
