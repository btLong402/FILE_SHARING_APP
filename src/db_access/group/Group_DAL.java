package db_access.group;

import db_access.db_connection.FTP_Db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.group_model.GroupModel;
import models.group_model.ListOfMembers;

public class Group_DAL {

	// create a GroupName by userName
	public boolean createGroup(String userName, String groupName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT CreateNewGroup(?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, userName);
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

	// a userName invite to GroupName
	public boolean inviteGroup(String userName, String groupName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT InviteToGroup(?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, userName);
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

	// a userName join to GroupName
	public boolean joinGroup(String userName, String groupName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT RequestToJoinGroup(?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, userName);
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

	// a userName leave a GroupName
	public boolean leaveGroup(String userName, String groupName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT LeaveGroup(?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, userName);
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
	// admin remove a userName from a GroupName
	public boolean removeMember(String userName, String groupName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT LeaveGroup(?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, userName);
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

	// Check Admin of groupName
	public boolean checkIsAdmin(String userName, String groupName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT CheckIsAdmin(?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, userName);
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

	// Check Member of groupName
	public boolean checkIsMember(String userName, String groupName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT CheckIsMember(?,?) AS Success;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, userName);
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
	// List all Groups
	public List<GroupModel> listAllGroup() {
		List<GroupModel> groupList = new ArrayList<>();
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT groupName FROM `Groups`";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						String groupName = resultSet.getString("groupName");
						groupList.add(new GroupModel(groupName));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupList;
	}
	public List<ListOfMembers> listMember(String groupName) {
		List<ListOfMembers> memberList = new ArrayList<>();
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT userName, mRole FROM `MemberOfGroup` WHERE groupName = ?;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, groupName);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						memberList.add(new ListOfMembers(resultSet.getString("userName"), resultSet.getString("mRole")));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return memberList;
	}
}
