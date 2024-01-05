package db_access.db_connection;

import java.sql.Connection;
import java.sql.DriverManager;
import utils.Configs;

public class FTP_Db {

  private static Connection connection = null;
  private static final String className = "com.mysql.cj.jdbc.Driver";

  public static Connection getConnection() {
    if (connection != null) {
      return connection;
    }
    try {
      Class.forName(className);
      connection =
        DriverManager.getConnection(
          Configs.DB_URL,
          Configs.DB_USER,
          Configs.DB_PASSWORD
        );
      System.out.println("Connect to database successfully");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return connection;
  }

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    FTP_Db.getConnection();
  }
}
