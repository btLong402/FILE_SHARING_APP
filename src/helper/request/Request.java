package helper.request;

import helper.payload.BasePayload;

public class Request {
	public String messageType;
	public BasePayload payload;
	
	public Request(String messageType, BasePayload payload) {
		super();
		this.messageType = messageType;
		this.payload = payload;
	}
}
