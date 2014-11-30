package org.asn.web_test.model;

public class FormModel {

	private String name;
	private String address;
	
	public FormModel(){}
	
	public FormModel(String name, String address) {
		super();
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "FormModel [name=" + name + ", address=" + address + "]";
	}
		
}
