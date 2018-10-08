package com.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseTimings {
	private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String connectionURL = "jdbc:mysql://localhost:3306/bus_timings";
	private static final String USER = "root";
	private static final String PWD = "asd";
	private Connection connection = null;
	
	public Connection getConnection() throws SQLException{
		
		
		try {
			Class.forName(DRIVER).newInstance();
			connection = DriverManager.getConnection(connectionURL, USER, PWD);
			return connection;
			
		}catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		catch (SQLException e2) {
			e2.printStackTrace();
		}
		
		catch (Exception e3) {
			e3.printStackTrace();
		}
		
		return null;
		
	}
	
	public void endConnection() {
		if (connection != null) {
			try {
				System.out.println("connection Closed");
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Error clossing connection");
		}
	}

}
