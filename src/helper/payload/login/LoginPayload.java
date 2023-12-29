package helper.payload.login;

import helper.payload.BasePayload;

public class LoginPayload extends BasePayload {
	String userName;
	String password;
	public LoginPayload() {
		super();
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public void setUserName(String userName) {
		// TODO Auto-generated method stub
		this.userName = userName;
	}
	@Override
	public void setPassword(String password) {
		this.password = password;
	}

}
