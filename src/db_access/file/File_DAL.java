package db_access.file;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db_access.db_connection.FTP_Db;

public class File_DAL {
	// create a folderName in groupName
	public boolean createFile(String fileName, long fileSize, String groupName, String folderName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT CreateFile(?,?,?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, fileName);
				preparedStatement.setLong(2, fileSize);
				preparedStatement.setString(3, groupName);
				preparedStatement.setString(4, folderName);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						return resultSet.getBoolean("Success");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// delete a fileName in folderName in groupName (only admin can do it, please checkIsAdmin before)
	public boolean deleteFile(String fileName, String groupName, String folderName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT RemoveFile(?,?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, folderName);
				preparedStatement.setString(2, groupName);
				preparedStatement.setString(3, folderName);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						return resultSet.getBoolean("Success");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean renameFile(String fileName, String groupName, String folderName, String newFileName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT RenameFile(?,?,?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, groupName);
				preparedStatement.setString(2, groupName);
				preparedStatement.setString(3, folderName);
				preparedStatement.setString(4, newFileName);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						return resultSet.getBoolean("Success");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	// when it comes to 'File Copy' (file_name, file_size, fromGroup, toGroup, fromFolder, toFolder)
	// => we only need to call createFile (file_name, file_size, toGroup, toFolder )
	// when it comes to 'File Move' (file_name, file_size, fromGroup, toGroup, fromFolder, toFolder)
	// => we only need to call createFile (file_name, file_size, toGroup, toFolder ) and deleteFile(file_name, fromGroup, fromFolder)
}
