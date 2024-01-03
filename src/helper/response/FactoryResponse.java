package helper.response;


import helper.response._response.Response;
import helper.response.payload.file.DownloadFilePayload;
import helper.response.payload.file.UploadFilePayload;
import helper.response.payload.group.CreateGroupPayload;
import helper.response.payload.group.RemoveMemberPayload;
import helper.response.payload.login.LoginPayload;
import helper.response.payload.register.RegisterPayload;

public class FactoryResponse {

	private FactoryResponse() {

	}
	
	public static Response intialRespone(String messageType, int responeCode) {
		switch (messageType) {
		case "LOGIN":
			return new Response(messageType,responeCode, new LoginPayload());
		case "REGISTER":
			return new Response(messageType,responeCode, new RegisterPayload());
		case "CREATE_GROUP":
			return new Response(messageType,responeCode, new CreateGroupPayload());
		case "UPLOAD_FILE":
			return new Response(messageType,responeCode, new UploadFilePayload());
		case "DOWNLOAD_FILE":
			return new Response(messageType,responeCode, new DownloadFilePayload());
		case "REMOVE_MEMBER":
			return new Response(messageType,responeCode, new RemoveMemberPayload());
		default:
			break;
		}
		return null;
	}
}
