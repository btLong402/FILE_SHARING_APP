package controllers.file_controller;

import db_access.file.File_DAL;

public class FileController {
		private File_DAL db = new File_DAL();
		public boolean createFile(String fileName, long fileSize, String groupName, String folderName) {
			if(db.createFile(fileName, fileSize, groupName, folderName)) return true;
			return false;
		}
}
