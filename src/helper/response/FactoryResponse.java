package helper.response;


import helper.response._response.Response;
import helper.response.payload.file.*;
import helper.response.payload.group.*;
import helper.response.payload.login.*;
import helper.response.payload.register.*;

public class FactoryResponse {

	private FactoryResponse() {

	}
	
	public static Response intialRespone(String messageType, int responeCode) {
		switch (messageType) {
		case "LOGIN":
			return new Response(responeCode, new LoginPayload());
		case "REGISTER":
			return new Response(responeCode, new RegisterPayload());
		case "CREATE_GROUP":
			return new Response(responeCode, new CreateGroupPayload());
		case "UPLOAD_FILE":
			return new Response(responeCode, new UploadFilePayload());
		case "DOWNLOAD_FILE":
			return new Response(responeCode, new DownloadFilePayload());
		case "REMOVE_MEMBER":
			return new Response(responeCode, new RemoveMemberPayload());
		case "JOIN_GROUP_STATUS":
			return new Response(responeCode, new JoinGroupStatusPayload());
		case "JOIN_REQUEST_LIST":
			return new Response(responeCode, new JoinRequestListPayload());
		default:
			break;
		}
		return null;
	}
}
