package helper.payload.file;

import helper.payload.BasePayload;

public class UploadFilePayload extends BasePayload{
	String fileName;
	long fileSize;
	String groupName;
	String folderName;
	public UploadFilePayload() {
		super();
	}
	public String getFileName() {
		return fileName;
	}
	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getFileSize() {
		return fileSize;
	}
	@Override
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
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
