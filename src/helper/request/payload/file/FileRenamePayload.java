package helper.request.payload.file;

import helper.request.payload.BasePayload;

public class FileRenamePayload extends BasePayload{
	String fileName;
	String groupName;
	String folderName;
	String newFileName;
	public FileRenamePayload() {
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
	@Override
	public void setNewFileName(String newFileName) {
		// TODO Auto-generated method stub
		super.setNewFileName(newFileName);
		this.newFileName = newFileName;
	}
	
}
