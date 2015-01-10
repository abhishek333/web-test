/**
 * 
 */
package org.asn.web_test.model;



/**
 * @author Abhishek
 *
 */
public class User{
		
	private String j_username;
	private String j_password;
		
	private Integer userRole;
	
	public User(){}
	
	public User(String j_username, String j_password) {
		super();		
		this.j_username = j_username;
		this.j_password = j_password;
	}

	public String getJ_username() {
		return j_username;
	}

	public void setJ_username(String j_username) {
		if(j_username==null || j_username.isEmpty())
			throw new IllegalArgumentException("Invalid param");
		this.j_username = j_username;
	}

	public String getJ_password() {
		return j_password;
	}

	public void setJ_password(String j_password) {
		if(j_username==null || j_username.isEmpty())
			throw new IllegalArgumentException("Invalid param");
		this.j_password = j_password;
	}

	public void setUserRole(USER_ROLE userRole) {
		this.userRole = userRole.getRoleCode();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((j_password == null) ? 0 : j_password.hashCode());
		result = prime * result
				+ ((j_username == null) ? 0 : j_username.hashCode());
		result = prime * result
				+ ((userRole == null) ? 0 : userRole.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (j_password == null) {
			if (other.j_password != null)
				return false;
		} else if (!j_password.equals(other.j_password))
			return false;
		if (j_username == null) {
			if (other.j_username != null)
				return false;
		} else if (!j_username.equals(other.j_username))
			return false;
		if (userRole == null) {
			if (other.userRole != null)
				return false;
		} else if (!userRole.equals(other.userRole))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [j_username=");
		builder.append(j_username);
		builder.append(", j_password=");
		builder.append(j_password);
		builder.append("]");
		return builder.toString();
	}
		
	public enum USER_ROLE {
		ROLE_USER(0), ROLE_ADMIN(1);
		private int roleCode;
		
		USER_ROLE(int roleCode){
			this.roleCode = roleCode;
		}
		
		public int getRoleCode(){
			return this.roleCode;
		}
		
		public static USER_ROLE convertRole(int roleCode){
			switch (roleCode) {
			  case 0:
			   return USER_ROLE.ROLE_USER;
			  case 1:
			   return USER_ROLE.ROLE_ADMIN;			  
			  default:
			   throw new IllegalArgumentException("Unknown value: " + roleCode);			
			}
		}
		
		public static boolean contains(String test) {

		    for (USER_ROLE userRole : USER_ROLE.values()) {
		        if (userRole.name().equals(test)) {
		            return true;
		        }
		    }

		    return false;
		}
	}

}

