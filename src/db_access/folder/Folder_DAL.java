package db_access.folder;

import db_access.db_connection.FTP_Db;
import models.folder_model.FolderContentsModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Folder_DAL {

  // create a folderName in groupName
  public boolean createFolder(String folderName, String groupName) {
    try {
      Connection connection = FTP_Db.getConnection();
      String query = "SELECT CreateFolder(?,?) AS Success;";
      try (
        PreparedStatement preparedStatement = connection.prepareStatement(query)
      ) {
        preparedStatement.setString(1, folderName);
        preparedStatement.setString(2, groupName);
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

  // delete a folderName in groupName (only admin can do it, please checkIsAdmin before)
  public boolean deleteFolder(String folderName, String groupName) {
    try {
      Connection connection = FTP_Db.getConnection();
      String query = "SELECT RemoveFolder(?,?) AS Success;";
      try (
        PreparedStatement preparedStatement = connection.prepareStatement(query)
      ) {
        preparedStatement.setString(1, folderName);
        preparedStatement.setString(2, groupName);
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

  public boolean renameFolder(
    String groupName,
    String folderName,
    String newFolderName
  ) {
    try {
      Connection connection = FTP_Db.getConnection();
      String query = "SELECT RenameFolder(?,?,?) AS Success;";
      try (
        PreparedStatement preparedStatement = connection.prepareStatement(query)
      ) {
        preparedStatement.setString(1, groupName);
        preparedStatement.setString(2, folderName);
        preparedStatement.setString(3, newFolderName);
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

  public boolean copyFolder(
    String fromGroupName,
    String folderName,
    String toGroupName
  ) {
    try {
      Connection connection = FTP_Db.getConnection();
      String query = "SELECT CopyFolder(?,?,?) AS Success;";
      try (
        PreparedStatement preparedStatement = connection.prepareStatement(query)
      ) {
        preparedStatement.setString(1, fromGroupName);
        preparedStatement.setString(2, folderName);
        preparedStatement.setString(3, toGroupName);
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

  // when it comes to Folder Move fromGroup,folder_Name,toGroup - we only need to call createFolder(String folder_Name, String toGroup) and deleteFolder(folder_name, fromGroup)

  public List<FolderContentsModel> folderContent(String groupName, String folderName) {
    List<FolderContentsModel> contents = new ArrayList<>();
    try {
      Connection connection = FTP_Db.getConnection();
      String query =
        "SELECT fName, fileSize FROM `File` WHERE groupName = ? AND folderName = ?";
      try (
        PreparedStatement preparedStatement = connection.prepareStatement(query)
      ) {
        preparedStatement.setString(1, groupName);
        preparedStatement.setString(2, folderName);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
          while (resultSet.next()) {
            String fileName = resultSet.getString("fName");
            long fileSize = resultSet.getLong("fileSize");
            contents.add(new FolderContentsModel(fileName, fileSize));
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return contents;
  }
}
