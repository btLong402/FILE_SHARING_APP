package models.user_model;

public class UserModel {
	private String userId;
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
		this.userId = userId;
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
