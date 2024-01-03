package helper.request.payload.file;

import helper.request.payload.BasePayload;

public class FileDeletePayload extends BasePayload {
	String fileName;
	String groupName;
	String folderName;

	public FileDeletePayload() {
		super();
	}

	@Override
	public void setFileName(String fileName) {
		// TODO Auto-generated method stub
		super.setFileName(fileName);
		this.fileName = fileName;
	}

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
