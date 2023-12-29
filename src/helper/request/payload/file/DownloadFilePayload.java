package helper.request.payload.file;

import helper.request.payload.BasePayload;

public class DownloadFilePayload extends BasePayload {
	String fileName;
	String groupName;
	String folderName;
	public DownloadFilePayload() {
		super();
	}
	public String getFileName() {
		return fileName;
	}
	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getGroupName() {
		return groupName;
	}
	@Override
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getFolderName() {
		return folderName;
	}
	@Override
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
}
