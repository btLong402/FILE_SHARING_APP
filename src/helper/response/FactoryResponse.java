package helper.response;


import helper.response._response.Response;
import helper.response.payload.EmptyPayload;
import helper.response.payload.file.*;
import helper.response.payload.group.*;
import helper.response.payload.login.*;
import helper.response.payload.register.*;

public class FactoryResponse {

	private FactoryResponse() {

	}
	
	public static Response intialRespone(String messageType, int responseCode) {
		switch (messageType) {
		case "LOGIN":
			return new Response(responseCode, new LoginPayload());
		case "REGISTER":
			return new Response(responseCode, new RegisterPayload());
		case "CREATE_GROUP":
			return new Response(responseCode, new CreateGroupPayload());
		case "UPLOAD_FILE":
			return new Response(responseCode, new UploadFilePayload());
		case "DOWNLOAD_FILE":
			return new Response(responseCode, new DownloadFilePayload());
		case "REMOVE_MEMBER":
			return new Response(responseCode, new RemoveMemberPayload());
		case "JOIN_GROUP_STATUS":
			if(responseCode == 200)
			return new Response(responseCode, new JoinGroupStatusPayload());
			else
			return new Response(responseCode, new EmptyPayload());
		case "JOIN_REQUEST_LIST":
			return new Response(responseCode, new JoinRequestListPayload());
		default:
			break;
		}
		return null;
	}
}
