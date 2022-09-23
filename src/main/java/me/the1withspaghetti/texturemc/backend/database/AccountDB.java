package me.the1withspaghetti.texturemc.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public static User getUser(long id) throws SQLException {
		PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?;");
		ps.setLong(1, id);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) return null;
		return new AccountDB().new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5));
	}
	
	public static User getUser(String email, String hash) throws SQLException {
		PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE email = ? AND hash = ?;");
		ps.setString(1, email);
		ps.setString(2, hash);
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) return null;
		
		return new AccountDB().new User(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getBoolean(5));
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
		
		ps = con.prepareStatement("UPDATE accounts SET verified = 1 WHERE id = ?;");
		ps.setLong(1, userId);
		ps.execute();
	}
	
	/*  Pack-related Requests  */
	
	public static Pack getPack(long id, long userId) throws SQLException {
		PreparedStatement ps = con.prepareStatement("SELECT id, userId, name, version FROM packs WHERE id = ? AND userId = ?;");
		ps.setLong(1, id);
		
		ResultSet rs = ps.executeQuery();
		if (!rs.next()) return null;
		
		return new AccountDB().new Pack(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4));
	}
	
	public static LinkedList<Pack> getPacks(long userId) throws SQLException {
		PreparedStatement ps = con.prepareStatement("SELECT id, userId, name, version FROM packs WHERE userId = ?;");
		ps.setLong(1, userId);
		 
		ResultSet rs = ps.executeQuery();
		LinkedList<Pack> packs = new LinkedList<>();
		while (rs.next()) {
			packs.add(new AccountDB().new Pack(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4)));
		}
		return packs;
	}
	
	public static void addPack(long id, long userId, String name, String version) throws SQLException {
		PreparedStatement ps = con.prepareStatement("INSERT INTO packs (id, userId, name, version) VALUES(?,?,?,?);");
		ps.setLong(1, id);
		ps.setLong(2, userId);
		ps.setString(3, name);
		ps.setString(4, version);
		ps.execute();
	}
	
	public static boolean renamePack(long id, long userId, String name) throws SQLException {
		PreparedStatement ps = con.prepareStatement("UPDATE packs SET name = ? WHERE id = ? AND userId = ?;");
		ps.setString(1, name);
		ps.setLong(2, id);
		ps.setLong(3, userId);
		return (ps.executeUpdate() > 0);
	}
	
	public static boolean duplicatePack(long oldId, long userId, long newId) throws SQLException {
		Pack pack = getPack(oldId, userId);
		if (pack == null) return false;
		if (pack.userId != userId) return false;
		
		PreparedStatement ps = con.prepareStatement("INSERT INTO packs (id, userId, name, version) VALUES(?,?,?,?);");
		ps.setLong(1, newId);
		ps.setLong(2, userId);
		ps.setString(3, incrementName(pack.name));
		ps.setString(4, pack.version);
		
		return (ps.executeUpdate() > 0);
	}
	
	public static boolean deletePack(long id, long userId) throws SQLException {
		PreparedStatement ps = con.prepareStatement("DELETE FROM packs WHERE id = ? AND userId = ?;");
		ps.setLong(1, id);
		ps.setLong(2, userId);
		
		System.out.println(ps.executeUpdate());
		return true;
	}
	
	private static String incrementName(String name) {
		Pattern p = Pattern.compile("[0-9]{1}$");
		Matcher m = p.matcher(name);
		if (m.matches()) {
			return m.replaceFirst(String.valueOf(Integer.parseInt(m.group()) + 1));
		} else {
			return name + " 2";
		}
	}
	
	
	public class User {
		public long id;
		public String email;
		public String username;
		public String hash;
		public boolean verified;
		
		public User(long id, String email, String username, String hash, boolean verified) {
			this.id = id;
			this.email = email;
			this.username = username;
			this.hash = hash;
			this.verified = verified;
		}
	}
	
	public class Pack {
		public long id;
		public long userId;
		public String name;
		public String version;
		
		public Pack(long id, long userId, String name, String version) {
			this.id = id;
			this.userId = userId;
			this.name = name;
			this.version = version;
		}
	}
}
