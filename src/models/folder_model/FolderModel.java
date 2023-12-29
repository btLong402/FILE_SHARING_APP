package models.folder_model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class FolderModel {
	private String folderId;
	private String folderName;
	private String groupName;
	private LocalDateTime createAt;
	private LocalDateTime updateAt;
	
	
	
	public FolderModel(String folderName, String groupName) {
		super();
		this.folderName = folderName;
		this.groupName = groupName;
	}



	public FolderModel(String folderId, String folderName, String groupName, LocalDateTime createAt,
			LocalDateTime updateAt) {
		super();
		this.folderId = folderId;
		this.folderName = folderName;
		this.groupName = groupName;
		this.createAt = createAt;
		this.updateAt = updateAt;
	}



	public String getFolderId() {
		return folderId;
	}



	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}



	public String getFolderName() {
		return folderName;
	}



	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}



	public String getGroupName() {
		return groupName;
	}



	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}



	public String getCreateAt() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		return dateFormat.format(createAt);
	}



	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}



	public String getUpdateAt() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		return dateFormat.format(updateAt);
	}



	public void setUpdateAt(LocalDateTime updateAt) {
		this.updateAt = updateAt;
	}
	
	
	
}
