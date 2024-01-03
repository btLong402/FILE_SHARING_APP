package helper.request.payload.folder;

import helper.request.payload.BasePayload;

public class CreateFolderPayload extends BasePayload {
	String groupName;
	String folderName;
	public CreateFolderPayload() {
		super();
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
