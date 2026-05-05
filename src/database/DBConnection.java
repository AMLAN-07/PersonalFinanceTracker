package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static final String URL = "jdbc:mysql://localhost:3306/financetracker";
	private static final String USER = "root";
	private static final String PASSWORD = "hzkk4567@A";   // <-- put your real password here

	private static Connection connection = null;

	public static Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection(URL, USER, PASSWORD);
				System.out.println("Database connected successfully");   // optional, now only once
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
}