package helper.response;

import helper.response._response.Response;
import helper.response.payload.file.DownloadFilePayload;
import helper.response.payload.file.UploadFilePayload;
import helper.response.payload.folder.CreateFolderPayload;
import helper.response.payload.group.CreateGroupPayload;
import helper.response.payload.group.ListAllGroupPayload;
import helper.response.payload.group.RemoveMemberPayload;
import helper.response.payload.login.LoginPayload;
import helper.response.payload.register.RegisterPayload;

public class FactoryResponse {

	private FactoryResponse() {

	}

	public static Response intialResponse(String messageType) {
		switch (messageType) {
		case "LOGIN":
			return new Response(new LoginPayload());
		case "REGISTER":
			return new Response(new RegisterPayload());
		case "CREATE_GROUP":
			return new Response(new CreateGroupPayload());
		case "UPLOAD_FILE":
			return new Response(new UploadFilePayload());
		case "DOWNLOAD_FILE":
			return new Response(new DownloadFilePayload());
		case "REMOVE_MEMBER":
			return new Response(new RemoveMemberPayload());
		case "LIST_ALL_GROUPS":
			return new Response(new ListAllGroupPayload());
		case "CREATE_FOLDER":
			return new Response(new CreateFolderPayload());
		default:
			break;
		}
		return null;
	}
}
