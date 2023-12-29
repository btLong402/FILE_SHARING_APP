package helper.respone._respone;

import helper.respone.payload.BasePayload;

public class Respone {
	public String messageType;
	public int responeCode;
	public BasePayload payload;

	public Respone(String messageType, int responeCode, BasePayload payload) {
		super();
		this.messageType = messageType;
		this.responeCode = responeCode;
		this.payload = payload;
	}
}
