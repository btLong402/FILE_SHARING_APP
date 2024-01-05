package models.user_model;

public class UserModel {
	private String userName;
	
	public UserModel() {
		super();
	}
	public UserModel(String userName) {
		super();
		this.userName = userName;
	}
	public UserModel(String userId, String userName) {
		super();
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
