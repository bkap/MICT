package mict.server;

import java.sql.*;

public class DatabaseLayer {
	public DatabaseLayer(String connection, String username, String password) {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(connection, username, password);
		} catch(ClassNotFoundException e) {
			System.err.println("Driver class init is fail:\n" + e.getMessage());
			System.exit(2);
		}
	}

	private String driver = "org.postgresql.Driver";
	private Connection con = null;

	public void getChunk(int x, int y) {
		Chunk result = null;
		try {
			String query = "SELECT etc"; // TODO do and stuff
			Statement instruction = con.createStatement();
			ResultSet results= instruction.executeQuery(query);
			if(results.next()) {
				// TODO create the chunk
			}
		} catch(SQLException e) {
			System.err.println("SQL is fail:\n" +e.getMessage());
		}
		return result; // obviously this is wrong.
	}
}
