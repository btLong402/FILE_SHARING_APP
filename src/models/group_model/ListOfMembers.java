package models.group_model;

public class ListOfMembers {
	private String userName;
	private String role;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public ListOfMembers(String userName, String role) {
		super();
		this.userName = userName;
		this.role = role;
	}
}
