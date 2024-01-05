package controllers.folder_controller;

import db_access.folder.Folder_DAL;

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
}
