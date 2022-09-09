package me.the1withspaghetti.texturemc.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AccountDB {
	
	private static Connection con;
	
	public static void connect() {
		try {
			con = DriverManager.getConnection("jdbc:sqlite:texturemc_accounts.db");
			con.createStatement().execute("CREATE TABLE IF NOT EXISTS accounts ("
					+ " id BIGINT PRIMARY KEY,"
					+ " email TEXT NOT NULL,"
	                + "	username TEXT NOT NULL,"
	                + "	hash TEXT NOT NULL,"
	                + " verified BOOL DEFAULT 0,"
	                + " lastSentEmail BIGINT DEFAULT 0"
	                + ");");
			con.createStatement().execute("CREATE TABLE IF NOT EXISTS packs ("
					+ " id BIGINT PRIMARY KEY,"
	                + "	userId BIGINT NOT NULL"
					+ " name TEXT NOT NULL"
	                + " version TEXT NOT NULL"
					+ " FOREIGN KEY (userId) REFERENCES accounts(id)"
	                + ");");
			con.createStatement().execute("CREATE INDEX IF NOT EXISTS accounts_by_id ON accounts (id);");
			
			System.out.println("Connection to SQL database has been established");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void addUser(long id, String email, String username, String hash, long lastSentEmail) throws SQLException {
		PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (id, email, username, hash, lastSentEmail) VALUES(?,?,?,?,?);");
		ps.setLong(1, id);
		ps.setString(2, email);
		ps.setString(3, username);
		ps.setString(4, hash);
		ps.setLong(5, lastSentEmail);
		ps.execute();
	}
}
