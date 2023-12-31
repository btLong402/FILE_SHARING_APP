package db_access.join;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db_access.db_connection.FTP_Db;
import models.join_model.ListOfJoinRequests;
import models.join_model.ListOfInvitation;
import models.join_model.ListOfApproval;
public class Join_DAL {
	// request To Join a Group
	public boolean joinRequest(String userName, String groupName) {
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

	// request To Join a Group
	public boolean joinInvitation(String userName, String groupName) {
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
	// Accept to a group
	public boolean accept(String userName, String groupName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT AcceptUsertoGroup(?,?) AS Success;";
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
	//Denied to a group
	public boolean denied(String userName, String groupName) {
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT DenyUsertoGroup(?,?) AS Success;";
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
	// List of Request to Join Group
	public List<ListOfJoinRequests> joinRequestStatus() {
		List<ListOfJoinRequests> requestList = new ArrayList<>();
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT groupName, status, createAt FROM `JoinGroup`;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						requestList.add(new ListOfJoinRequests(resultSet.getString("groupName"), 
								resultSet.getString("status"), resultSet. getTimestamp("createAt")));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return requestList;
	}
	
	// List of Invitation to Join Group
		public List<ListOfInvitation> listOfInvitationList() {
			List<ListOfInvitation> invitationList = new ArrayList<>();
			try {
				Connection connection = FTP_Db.getConnection();
				String query = "SELECT groupName, status, createAt FROM `JoinGroup` WHERE requestType = 'invite';";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					try (ResultSet resultSet = preparedStatement.executeQuery()) {
						while (resultSet.next()) {
							invitationList.add(new ListOfInvitation(resultSet.getString("groupName"), resultSet.getString("status"), resultSet. getTimestamp("createAt")));
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return invitationList;
		}
	//Before use this function, please check current user is Admin of groupName (checkIsAdmin function)
	public List<ListOfApproval> joinRequestList(String groupName) {
		List<ListOfApproval> approvalList = new ArrayList<>();
		try {
			Connection connection = FTP_Db.getConnection();
			String query = "SELECT userName, createAt FROM `JoinGroup` WHERE status = 'pending' AND groupName = ?;";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setString(1, groupName);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						approvalList.add(new ListOfApproval(resultSet.getString("userName"), resultSet.getTimestamp("createAt")));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return approvalList;
	}
}
