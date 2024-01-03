package helper.response._response;

import helper.response.payload.BasePayload;

public class Response {
	
	public int responseCode;
	public BasePayload payload;

	public Response(int responseCode, BasePayload payload) {
		super();
		this.responseCode = responseCode;
		this.payload = payload;
	}
}
