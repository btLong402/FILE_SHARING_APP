package db_access.user;

import java.sql.*;
import db_access.db_connection.FTP_Db;

public class User_DAL {
	
	public static void create(String userName, String password) {
		try {
			Connection connection = FTP_Db.getConnection();
			Statement statement = connection.createStatement();
			
		} catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
}
