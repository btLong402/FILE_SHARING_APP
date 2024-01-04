package helper.response._response;

import helper.response.payload.BasePayload;

public class Response {
	
	public int responseCode;
	public BasePayload payload;

	public Response(BasePayload payload) {
		super();
		this.payload = payload;
	}

	public Response(int responseCode, BasePayload payload) {
		super();
		this.responseCode = responseCode;
		this.payload = payload;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public BasePayload getPayload() {
		return payload;
	}

	public void setPayload(BasePayload payload) {
		this.payload = payload;
	}
	
}
