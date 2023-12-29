package helper;

import helper.payload.file.DownloadFilePayload;
import helper.payload.file.UploadFilePayload;
import helper.payload.group.CreateGroupPayload;
import helper.payload.login.LoginPayload;
import helper.payload.register.RegisterPayload;
import helper.payload.group.RemoveMemberPayload;
import helper.payload.group.CreateFolderPayload;
import helper.request.Request;

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
