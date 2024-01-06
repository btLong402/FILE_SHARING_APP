package controllers.folder_controller;

import java.util.List;

import db_access.folder.Folder_DAL;
import models.folder_model.FolderContentsModel;

public class FolderController {
	private Folder_DAL db = new Folder_DAL();

	public boolean createFolder(String folderName, String groupName) {
		return db.createFolder(folderName, groupName);
	}

	public boolean rename(String groupName, String folderName, String newFolderName) {
		return db.renameFolder(groupName, folderName, newFolderName);
	}

	public boolean delete(String groupName, String folderName) {
		return db.deleteFolder(folderName, groupName);
	}

	public boolean move(String fromGroup, String toGroup, String folderName) {
		if (db.createFolder(folderName, toGroup) && db.deleteFolder(folderName, fromGroup))
			return true;
		return false;
	}
	public boolean copy(String fromGroup, String toGroup, String folderName) {
		return db.copyFolder(fromGroup, folderName, toGroup);
	}
	
	public List<FolderContentsModel> folderContent(String groupName, String folderName){
		return db.folderContent(groupName, folderName);
	}
}
