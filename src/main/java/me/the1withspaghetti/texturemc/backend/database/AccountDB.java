package me.the1withspaghetti.texturemc.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

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
	                + " verified BOOL DEFAULT 0"
	                + ");");
			con.createStatement().execute("CREATE TABLE IF NOT EXISTS packs ("
					+ " id BIGINT PRIMARY KEY,"
	                + "	userId BIGINT NOT NULL,"
					+ " name TEXT NOT NULL,"
	                + " version TEXT NOT NULL,"
					+ " FOREIGN KEY (userId) REFERENCES accounts(id)"
	                + ");");
			con.createStatement().execute("CREATE TABLE IF NOT EXISTS verification ("
					+ " id BIGINT PRIMARY KEY,"
	                + "	userId BIGINT NOT NULL,"
					+ " sent BIGINT NOT NULL,"
					+ " FOREIGN KEY (userId) REFERENCES accounts(id)"
	                + ");");
			con.createStatement().execute("CREATE INDEX IF NOT EXISTS accounts_by_id ON accounts (id);");
			
			System.out.println("Connection to SQL database has been established");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void purgeOldData() throws SQLException {
		PreparedStatement ps = con.prepareStatement("DELETE FROM verification WHERE sent < ?;");
		ps.setLong(1, System.currentTimeMillis() - TimeUnit.DAYS.convert(1, TimeUnit.MILLISECONDS));
	}
	
	public static void addUser(long id, String email, String username, String hash) throws SQLException {
		PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (id, email, username, hash) VALUES(?,?,?,?);");
		ps.setLong(1, id);
		ps.setString(2, email);
		ps.setString(3, username);
		ps.setString(4, hash);
		ps.execute();
	}
	
	public static void createVerificationRequest(long id, long userId, long sent) throws SQLException {
		PreparedStatement ps = con.prepareStatement("INSERT INTO verification (id, userId, sent) VALUES(?,?,?);");
		ps.setLong(1, id);
		ps.setLong(2, userId);
		ps.setLong(3, sent);
		ps.execute();
	}
	
	public static long getEmailConfirmation(long id) throws SQLException {
		PreparedStatement ps = con.prepareStatement("SELECT userId FROM verification WHERE id = ?;");
		ps.setLong(1, id);
		
		ResultSet rs = ps.executeQuery();
		
		if (!rs.next()) return 0;
		else return rs.getLong(1);
	}
	
	public static void confirmUser(long id, long userId) throws SQLException {
		PreparedStatement ps = con.prepareStatement("DELETE FROM verification WHERE id = ?;");
		ps.setLong(1, id);
		ps.execute();
		
		ps = con.prepareStatement("UPDATE accounts SET verified = 1 WHERE id = ?");
		ps.setLong(1, userId);
		ps.execute();
	}
}
