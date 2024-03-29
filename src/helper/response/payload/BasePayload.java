package helper.response.payload;

import java.util.List;

import models.folder_model.FolderContentsModel;
import models.group_model.GroupModel;
import models.group_model.ListOfMembers;
import models.join_model.JoinRequestList;
import models.join_model.ListOfInvitation;
import models.join_model.JoinRequestStatus;

public abstract class BasePayload {
	public void setFileName(String fileName) {}
	public void setFileSize(long fileSize) {}
	public void setListGroups(List<GroupModel> listGroups) {}

	public void setJoinRequestList(List<JoinRequestList> joinRequestList) {
		// TODO Auto-generated method stub
	}
	public void setJoinRequestStatus(List<JoinRequestStatus> joinRequestStatus) {
		// TODO Auto-generated method stub
		
	}
	public void setListOfInvitation(List<ListOfInvitation> listOfInvitation) {
		// TODO Auto-generated method stub
		
	}
	public void setListOfMembers(List<ListOfMembers> listOfMembers) {
		// TODO Auto-generated method stub
		
	}
		
	public void setFiles(List<String> files) {
		// TODO Auto-generated method stub
		
	}
	public void setFolderName(List<String> folderName) {
		// TODO Auto-generated method stub
		
	}
	public void setFileName(List<String> fileName) {
		// TODO Auto-generated method stub
		
	}
	public void setFolderContents(List<FolderContentsModel> folderContents) {
		// TODO Auto-generated method stub
		
	}
}