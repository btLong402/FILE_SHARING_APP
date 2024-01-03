package helper.payload.folder;

import helper.request.payload.BasePayload;

public class CreateFolderPayload extends BasePayload {
	String groupName;
	String folderName;
	public CreateFolderPayload() {
		super();
	}

	public String getGroupName() {
		return groupName;
	}
	public String getFolderName() {
		return folderName;
	}
	@Override
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	@Override
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
}
