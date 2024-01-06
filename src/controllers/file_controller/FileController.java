package controllers.file_controller;

import db_access.file.File_DAL;

public class FileController {
	private File_DAL db = new File_DAL();

	public boolean createFile(String fileName, long fileSize, String groupName, String folderName) {
		return db.createFile(fileName, fileSize, groupName, folderName);
	}

	public boolean delete(String fileName, String groupName, String folderName) {
		return db.deleteFile(fileName, groupName, folderName);
	}

	public boolean rename(String fileName, String newFileName, String groupName, String folderName) {
		return db.renameFile(fileName, groupName, folderName, newFileName);
	}

	public boolean move(String fileName, long fileSize, String fromGroup, String toGroup, String fromFolder,
			String toFolder) {
		if (db.createFile(fileName, fileSize, toGroup, toFolder) && db.deleteFile(fileName, fromGroup, fromFolder))
			return true;
		return false;
	}
	public boolean copy(String fileName, long fileSize, String fromGroup, String toGroup, String fromFolder, 
			String toFolder) {
		return db.createFile(fileName, fileSize, toGroup, toFolder);
	}
}
