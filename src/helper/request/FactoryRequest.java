package helper.request;

import helper.request._request.Request;
import helper.request.payload.file.DownloadFilePayload;
import helper.request.payload.file.UploadFilePayload;
import helper.request.payload.group.CreateGroupPayload;
import helper.request.payload.group.RemoveMemberPayload;
import helper.request.payload.login.LoginPayload;
import helper.request.payload.register.RegisterPayload;

public class FactoryRequest {

	private FactoryRequest() {

	}
	
	public static Request intialRequest(String messageType) {
		switch (messageType) {
		case "LOGIN":
			return new Request(messageType, new LoginPayload());
		case "REGISTER":
			return new Request(messageType, new RegisterPayload());
		case "CREATE_GROUP":
			return new Request(messageType, new CreateGroupPayload());
		case "UPLOAD_FILE":
			return new Request(messageType, new UploadFilePayload());
		case "DOWNLOAD_FILE":
			return new Request(messageType, new DownloadFilePayload());
		case "REMOVE_MEMBER": 
			return new Request(messageType, new RemoveMemberPayload());
		case "CREATE_FOLDER":
			return new Request(messageType, new CreateFolderPayload());
		default:
			break;
		}
		return null;
	}
}
