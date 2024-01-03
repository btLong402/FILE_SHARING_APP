package controllers.user_controller;

import db_access.user.User_DAL;
import models.user_model.UserModel;

public class UserController {
	private UserModel user = new UserModel();
	private User_DAL db = new User_DAL();
	
	public boolean signUp(String userName, String password) {
		boolean isSuccess = db.create(userName, password);
		if(isSuccess) {
			this.user.setUserName(userName);
			return true;
		}
		return false;
	};
	
	public boolean signIn(String userName, String password) {
		boolean isSuccess = db.login(userName, password);
		if(isSuccess) {
			this.user.setUserName(userName);
			return true;
		}
		return false;
	}
	
	public String getUserName() {
		return this.user.getUserName();
	}
	
	public UserModel getUser() {
		return this.user;
	}
}
