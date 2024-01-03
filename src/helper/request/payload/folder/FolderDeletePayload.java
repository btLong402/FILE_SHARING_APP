package helper.request.payload.folder;

import helper.request.payload.BasePayload;

public class FolderDeletePayload extends BasePayload{
	String folderName;
	String groupName;
	@Override
	public void setGroupName(String groupName) {
		// TODO Auto-generated method stub
		super.setGroupName(groupName);
		this.groupName = groupName;
	}
	@Override
	public void setFolderName(String folderName) {
		// TODO Auto-generated method stub
		super.setFolderName(folderName);
		this.folderName = folderName;
	}
	
}
