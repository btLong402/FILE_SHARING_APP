package helper.payload.register;

import helper.payload.BasePayload;

public class RegisterPayload extends BasePayload {
	String userName;
	String password;
	public RegisterPayload() {
		super();
	}
	public String getUserName() {
		return userName;
	}
	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Override
	public void setPassword(String password) {
		this.password = password;
	}
}
