package helper.request.payload.folder;

import helper.request.payload.BasePayload;

public class FolderRenamePayload extends BasePayload{
	String folderName;
	String groupName;
	String newFolderName;
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
	@Override
	public void setNewFolderName(String newFolderName) {
		// TODO Auto-generated method stub
		super.setNewFolderName(newFolderName);
		this.newFolderName = newFolderName;
	}
	
}
