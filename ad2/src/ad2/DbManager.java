package ad2;

//package edu.ucla.cs.cs144;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbManager {
      static private String databaseURL = "jdbc:mysql://120.26.79.234:3306/";
      static private String dbname = "ad";
      static private String username = "liferay";
      static private String password = "liferay";
	
	/**
	 * Opens a database connection
	 * @param dbName The database name
	 * @param readOnly True if the connection should be opened read-only
	 * @return An open java.sql.Connection
	 * @throws SQLException
	 */
	public static Connection getConnection(boolean readOnly)
	throws SQLException {        
          Connection conn = DriverManager.getConnection(
              databaseURL + dbname, username, password);
          conn.setReadOnly(readOnly);        
          return conn;
      }
	
	private DbManager() {}
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
