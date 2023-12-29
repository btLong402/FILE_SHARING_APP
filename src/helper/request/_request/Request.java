package helper.request._request;

import helper.request.payload.BasePayload;

public class Request {
	public String messageType;
	public BasePayload payload;
	
	public Request(String messageType, BasePayload payload) {
		super();
		this.messageType = messageType;
		this.payload = payload;
	}
}
