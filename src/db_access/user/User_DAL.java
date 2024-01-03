package db_access.user;

import java.sql.*;
import db_access.db_connection.FTP_Db;

public class User_DAL {
	
	public boolean create(String userName, String password) {
		try {
			Connection connection = FTP_Db.getConnection();
			Statement statement = connection.createStatement();
			String query = String.format("Select InsertNewUser('%s', '%s') AS Success;", userName, password);
			ResultSet resultSet = statement.executeQuery(query);
			if(resultSet.next()) {
				boolean userId = resultSet.getBoolean("Success");
				return userId;
			}
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	public boolean login(String userName, String password) {
	    try {
	        Connection connection = FTP_Db.getConnection();
	        String query = "SELECT Login(?, ?) AS Success;";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setString(1, userName);
	            preparedStatement.setString(2, password);
	            
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
}
