package helper.response.payload;

import java.util.List;

import models.file_model.FileModel;
import models.group_model.GroupModel;
import models.group_model.ListOfMembers;
import models.join_model.ListOfAppliedGroups;
import models.join_model.ListOfInvitation;
import models.join_model.ListOfJoinRequests;

public abstract class BasePayload {
	public void setFileName(String fileName) {}
	public void setFileSize(long fileSize) {}
	public void setListGroups(List<GroupModel> listGroups) {}
	public void setListOfAppliedGroups(List<ListOfAppliedGroups> listOfAppliedGroups) {
	}

	public void setListOfJoinRequests(List<ListOfJoinRequests> listOfJoinRequests) {
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
}