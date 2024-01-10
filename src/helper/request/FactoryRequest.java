package helper.request;

import helper.request._request.Request;
import helper.request.payload.file.DownloadFilePayload;
import helper.request.payload.file.FileCopyPayload;
import helper.request.payload.file.FileDeletePayload;
import helper.request.payload.file.FileMovePayload;
import helper.request.payload.file.FileRenamePayload;
import helper.request.payload.file.UploadFilePayload;
import helper.request.payload.folder.CreateFolderPayload;
import helper.request.payload.folder.FolderContentPayload;
import helper.request.payload.folder.FolderCopyPayload;
import helper.request.payload.folder.FolderDeletePayload;
import helper.request.payload.folder.FolderMovePayload;
import helper.request.payload.folder.FolderRenamePayload;
import helper.request.payload.group.ApprovalPayload;
import helper.request.payload.group.CreateGroupPayload;
import helper.request.payload.group.InviteGroupPayload;
import helper.request.payload.group.JoinGroupPayload;
import helper.request.payload.group.LeaveGroupPayload;
import helper.request.payload.group.ListAllGroupPayload;
import helper.request.payload.group.ListGroupMemberPayload;
import helper.request.payload.group.ListGroupPayload;
import helper.request.payload.group.ListInvitationPayload;
import helper.request.payload.group.ListRequestPayload;
import helper.request.payload.group.ListRequestStatusPayload;
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
		case "JION_GROUP":
			return new Request(messageType, new JoinGroupPayload());
		case "INVITE_TO_GROUP":
			return new Request(messageType, new InviteGroupPayload());
		case "LEAVE_GROUP":
			return new Request(messageType, new LeaveGroupPayload());
		case "LIST_GROUPS":
			return new Request(messageType, new ListGroupPayload());
		case "LIST_ALL_GROUPS":
			return new Request(messageType, new ListAllGroupPayload());
		case "LIST_GROUP_MEMBERS":
			return new Request(messageType, new ListGroupMemberPayload());
		case "FOLDER_CONTENT":
			return new Request(messageType, new FolderContentPayload());
		case "FOLDER_RENAME":
			return new Request(messageType, new FolderRenamePayload());
		case "FOLDER_DELETE":
			return new Request(messageType, new FolderDeletePayload());
		case "FOLDER_COPY":
			return new Request(messageType, new FolderCopyPayload());
		case "FOLDER_MOVE":
			return new Request(messageType, new FolderMovePayload());
		case "FILE_COPY":
			return new Request(messageType, new FileCopyPayload());
		case "FILE_MOVE":
			return new Request(messageType, new FileMovePayload());
		case "FILE_RENAME":
			return new Request(messageType, new FileRenamePayload());
		case "FILE_DELETE":
			return new Request(messageType, new FileDeletePayload());
		case "JOIN_REQUEST_STATUS":
			return new Request(messageType, new ListRequestStatusPayload());
		case "JOIN_REQUEST_LIST":
			return new Request(messageType, new ListRequestPayload());
		case "LIST_INVITATION":
			return new Request("LIST_OF_INVITATION", new ListInvitationPayload());
		case "APPROVAL":
			return new Request(messageType, new ApprovalPayload());
		default:
			break;
		}
		return null;
	}
}
