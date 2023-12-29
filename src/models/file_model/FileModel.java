package models.file_model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class FileModel {
	private String fileId;
	private String fileName;
	private String fileType;
	private long fileSize;
	private String groupName;
	private String folderName;
	private String uploadAt;
	private String updateAt;

	public FileModel(String fileName, String fileType, long fileSize, String groupName, String folderName) {
		super();
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.groupName = groupName;
		this.folderName = folderName;
	}

//	public FileModel(String fileId, String fileName, String fileType, long fileSize, String groupName,
//			String folderName, LocalDateTime uploadAt, LocalDateTime updateAt) {
//		super();
//		this.fileId = fileId;
//		this.fileName = fileName;
//		this.fileType = fileType;
//		this.fileSize = fileSize;
//		this.groupName = groupName;
//		this.folderName = folderName;
//		this.uploadAt = uploadAt;
//		this.updateAt = updateAt;
//	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

//	public String getUploadAt() {
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//		return dateFormat.format(uploadAt);
//	}
//
//	public void setUploadAt(LocalDateTime uploadAt) {
//		this.uploadAt = uploadAt;
//	}
//
	public String getUpdateAt() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		return dateFormat.format(updateAt);
	}
//
//	public void setUpdateAt(LocalDateTime updateAt) {
//		this.updateAt = updateAt;
//	}
//
//	public String getFileId() {
//		return fileId;
//	}

	@Override
	public String toString() {
		return "FileModel [fileName=" + fileName + ", fileType=" + fileType + ", fileSize=" + fileSize + ", groupName="
				+ groupName + ", folderName=" + folderName + "]";
	}
	
}
