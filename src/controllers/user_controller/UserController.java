package controllers.user_controller;

import db_access.user.User_DAL;
import models.user_model.UserModel;

public class UserController {
	private UserModel user = new UserModel();
	private User_DAL db = new User_DAL();
	
	public boolean signUp(String userName, String password) {
		String userId = db.create(userName, password);
		if(userId != null) {
			this.user.setUserId(userId);
			this.user.setUserName(userName);
			return true;
		}
		return false;
	};
	
	public boolean signIn(String userName, String password) {
		String userId = db.login(userName, password);
		if(userId != null) {
			this.user.setUserId(userId);
			this.user.setUserName(userName);
			return true;
		}
		return false;
	}
	
	public String getUserName() {
		return this.user.getUserName();
	}
	
	public String getUserId() {
		return this.user.getUserId();
	}
	public UserModel getUser() {
		return this.user;
	}
}
