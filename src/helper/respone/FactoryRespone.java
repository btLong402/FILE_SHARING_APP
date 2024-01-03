package helper.respone;

import helper.respone._respone.Respone;
import helper.respone.payload.file.DownloadFilePayload;
import helper.respone.payload.file.UploadFilePayload;
import helper.respone.payload.group.CreateGroupPayload;
import helper.respone.payload.group.RemoveMemberPayload;
import helper.respone.payload.login.LoginPayload;
import helper.respone.payload.register.RegisterPayload;

public class FactoryRespone {

	private FactoryRespone() {

	}
	
	public static Respone intialRespone(String messageType, int responeCode) {
		switch (messageType) {
		case "LOGIN":
			return new Respone(messageType,responeCode, new LoginPayload());
		case "REGISTER":
			return new Respone(messageType,responeCode, new RegisterPayload());
		case "CREATE_GROUP":
			return new Respone(messageType,responeCode, new CreateGroupPayload());
		case "UPLOAD_FILE":
			return new Respone(messageType,responeCode, new UploadFilePayload());
		case "DOWNLOAD_FILE":
			return new Respone(messageType,responeCode, new DownloadFilePayload());
		case "REMOVE_MEMBER":
			return new Respone(messageType,responeCode, new RemoveMemberPayload());
		default:
			break;
		}
		return null;
	}
}
