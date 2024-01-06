package helper.response;

import helper.response._response.Response;

import helper.response.payload.folder.*;
import helper.response.payload.EmptyPayload;
import helper.response.payload.file.*;
import helper.response.payload.group.*;
import helper.response.payload.login.*;
import helper.response.payload.register.*;


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
		case "REMOVE_MEMBER":
			return new Response(new RemoveMemberPayload());
		case "JOIN_GROUP":
			return new Response(new JoinGroupPayload());
		case "JOIN_GROUP_APPROVAL":
			return new Response(new ApprovalPayload());
		case "INVITE_TO_GROUP":
			return new Response(new InviteToGroupPayload());
		case "LIST_OF_INVITAION":
			return new Response(new ListOfInvitationPayload());
		case "LEAVE_GROUP":
			return new Response(new LeaveGroupPayload());
		case "LIST_GROUPS":
			return new Response(new ListGroupsPayload());
		case "LIST_GROUP_MEMBERS":
			return new Response(new ListGroupMembersPayload());
		case "FOLDER_CONTENT":
			return new Response(new FolderContentPayload());
		case "FOLDER_RENAME":
			return new Response(new FolderRenamePayload());
		case "FOLDER_DELETE":
			return new Response(new FolderDeletePayload());
		case "FOLDER_COPY":
			return new Response(new FolderCopyPayload());
		case "FOLDER_MOVE":
			return new Response(new FolderMovePayload());
		case "GOTO_GROUP":
			return new Response(new GotoGroupPayload());
		case "GOTO_FOLDER":
			return new Response(new GotoFolderPayload());
		case "FILE_RENAME":
			return new Response(new FileRenamePayload());
		case "FILE_COPY":
			return new Response(new FileCopyPayload());
		case "FILE_DELETE":
			return new Response(new FileDeletePayload());
		case "FILE_MOVE":
			return new Response(new FileMovePayload());
		case "UPLOAD_FILE":
			return new Response(new UploadFilePayload());
		case "DOWNLOAD_FILE":
			return new Response(new DownloadFilePayload());
		case "LIST_ALL_GROUPS":
			return new Response(new ListAllGroupPayload());
		case "CREATE_FOLDER":
			return new Response(new CreateFolderPayload());
		case "JOIN_GROUP_STATUS":
			return new Response(new JoinGroupStatusPayload());
		case "JOIN_REQUEST_LIST":
			return new Response(new JoinRequestListPayload());
		default:
			return new Response(new EmptyPayload());
		}
	}
}
