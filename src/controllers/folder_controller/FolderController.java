package controllers.folder_controller;

import db_access.folder.Folder_DAL;

public class FolderController {
	private Folder_DAL db = new Folder_DAL();
	public boolean createFolder(String folderName, String groupName) {
		if(db.createFolder(folderName, groupName)) return true;
		return false;
	}
}
