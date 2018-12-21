package migration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.BowlGame;
import data.Pick;
import data.User;

public class MigrateDB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args == null || args.length != 2) {
			System.out.println("Invalid number of args.  Must be 2 ex: 14 bowlgame");
			return;
		}
		int fromYear = Integer.parseInt(args[0]);
		String table = args[1];
		
		Connection fromConn = null;
		Connection toConn = null;
		
		fromConn = setConnection(fromYear);
		Map<Integer, BowlGame> fromBowlGameMap = getBowlGamesMap(fromYear, fromConn, false);
		Map<Integer, User> fromUserMap = getUsersMap(fromYear, null, fromConn, false);
		toConn = setConnection(null);
		
		if (table.equalsIgnoreCase("bowlgame")) {
			if (getBowlGamesCount(fromYear, toConn) > 0 ) {
				System.out.println("20" + fromYear + " Bowl Games already migrated!");
				return;
			}
			for (Integer k : fromBowlGameMap.keySet()) {
				BowlGame bg = fromBowlGameMap.get(k); 
				createBowlGame(bg.getBowlName(), bg.getFavorite(), bg.getUnderdog(), bg.getSpread(), fromYear, null, 
						bg.getFavoriteScore(), bg.getUnderDogScore(), bg.isCompleted(), toConn);
			}
			System.out.println("20" + fromYear + " " + fromBowlGameMap.size() + " Bowl Games migrated");	
		}
		else if (table.equalsIgnoreCase("user")) {
			int poolId = getPoolIdFromYear(fromYear, toConn);
			if (getUsersCount(fromYear, poolId, toConn) > 0 ) {
				System.out.println("20" + fromYear + " Users already migrated!");
				return;
			}
			for (Integer k : fromUserMap.keySet()) {
				User u = fromUserMap.get(k); 
				createUser(u.getUserName(), fromYear, poolId, u.isAdmin(), toConn);
			}
			System.out.println("20" + fromYear + " " + fromUserMap.size() + " users migrated");
		}
		else if (table.equalsIgnoreCase("pick")) {
			int poolId = getPoolIdFromYear(fromYear, toConn);
			if (getPicksCount(fromYear, poolId, toConn) > 0) {
				System.out.println("20" + fromYear + " Picks already migrated!");
				return;
			}
			Map<Integer, BowlGame> toBowlGameMap = getBowlGamesMap(fromYear, toConn, true);
			Map<Integer, User> toUserMap = getUsersMap(fromYear, poolId, toConn, true);
			if (toBowlGameMap.size() == 0 || toUserMap.size() == 0) {
				System.out.println("20" + fromYear + " users or bowlgames do not exist");
				return;
			}
			Map<Integer, List<Pick>> fromPicksMap =  getPicksMap(fromConn);
			System.out.println(fromPicksMap.size() + " users in " + fromYear);
			List<Pick> migratedPicksList = new ArrayList<Pick>();
			for (Integer userId : fromPicksMap.keySet()) {
				List<Pick> userPicks = fromPicksMap.get(userId);
				for (Pick p : userPicks) {
					Integer newGameId = getGameIdFromOldPick(fromBowlGameMap.get(p.getGameId()), toBowlGameMap);
					System.out.println(newGameId + " from: " + p.getGameId() + " " + 
						fromBowlGameMap.get(p.getGameId()).getFavorite() + " " + fromBowlGameMap.get(p.getGameId()).getUnderdog());
					Pick newPick = new Pick();
					newPick.setGameId(newGameId);
					migratedPicksList.add(new Pick());
				}
			}
			System.out.println(migratedPicksList.size() + " migratedPicks");
		}
		else {
			System.out.println("Invalid args: " + args[1]);
		}
	}
	
	private static Integer getGameIdFromOldPick(BowlGame oldBowlGame, Map<Integer, BowlGame> toBowlGameMap) {
		int x = 1;
		for (Integer gameId : toBowlGameMap.keySet()) {
			BowlGame bg = toBowlGameMap.get(gameId);
			
			if (oldBowlGame.getFavorite().equals(bg.getFavorite()) && oldBowlGame.getUnderdog().equals(bg.getUnderdog())) {
				return bg.getGameId();
			}
		}
		
		return null;
	}
	
	// DB
	private static Connection setConnection(Integer year) {
		Connection conn = null;
		try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } 
		catch (Exception ex) {
        }
		try {
			String connString = "jdbc:mysql://localhost/bowlpool";
			if (year != null && year.intValue() < 17) { // only append year before 2017
			    connString += year;
			}
			connString += "?user=root&password=PASSWORD&useSSL=false";
			conn = DriverManager.getConnection(connString);
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		return conn;
	}
	
	public static void createBatchPicks(List<Pick> picksList, Integer poolId, Connection conn) {
		try {
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			int picksCount = 0;
			for (Pick p : picksList) {
				String insertSQL = "INSERT INTO Pick (UserId, GameId, Favorite, PoolId, CreatedTime) VALUES (" + 
					p.getUserId() + ", " + p.getGameId() + ", " + p.getFavorite() + ", " + poolId + ", NOW());";
				stmt.addBatch(insertSQL);
				picksCount++;
				// Every 500 lines, insert the records
				if (picksCount % 250 == 0) {
					System.out.println("Insert picks " + (picksCount - 250) + " : " + picksCount);
					stmt.executeBatch();
					conn.commit();
					stmt.close();
					stmt = conn.createStatement();
				}
			}
			// Insert the remaining records
			System.out.println("Insert remaining picks " + (picksCount - (picksCount % 250)) + " : " + picksCount);
			stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true); // set auto commit back to true for next inserts
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void createBowlGame(String gameName, String favorite, String underdog, Double line, Integer year, Timestamp dateTime, Integer favScore, 
		Integer dogScore, boolean completed, Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO BowlGame (BowlName, Favorite, Underdog, Spread, FavoriteScore, UnderdogScore, Completed, Year, DateTime) VALUES ('" + 
				gameName + "', '" + favorite + "', '" + underdog + "' , " + line + ", " + favScore + ", " +  dogScore + ", " + completed + ", " + year + "," +
				(dateTime != null ? "'" + dateTime + "'" : null) + ");";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void createUser(String userName, Integer year, Integer poolId, boolean admin, Connection conn) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO User (UserName, LastName, FirstName, Email, Year, admin, PoolId) VALUES ('" + 
				userName + "', '', '', '', " + year + "," + admin + ", " + poolId + ");");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static Map<Integer, BowlGame> getBowlGamesMap(Integer year, Connection conn, boolean useYearClause) {
		Map<Integer, BowlGame> bowlGamesMap = new HashMap<Integer, BowlGame>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM BowlGame" + 
					(useYearClause ? " where " + getYearClause(year, null) + " order by DateTime": ""));
			BowlGame bowlGame;
			while (rs.next()) {
				bowlGame = new BowlGame(rs.getInt("GameId"), rs.getString("BowlName"), rs.getString("Favorite"),
						rs.getString("Underdog"), rs.getDouble("Spread"), rs.getInt("FavoriteScore"), 
						rs.getInt("UnderDogScore"), rs.getBoolean("Completed"), year, null);
				bowlGamesMap.put(bowlGame.getGameId(), bowlGame);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return bowlGamesMap;
	}
	
	private static Map<Integer, User> getUsersMap(Integer year, Integer poolId, Connection conn, boolean useYearClause) {
		Map<Integer, User> userMap = new HashMap<Integer, User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User" + (useYearClause ? " where " + getYearClause(year, poolId) : ""));
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), year, false, 0);
				userMap.put(user.getUserId(), user);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userMap;
	}
	
	public static Map<Integer, List<Pick>> getPicksMap(Connection conn) {
		Map<Integer, List<Pick>> picksMap = new HashMap<Integer, List<Pick>>();
		ArrayList<Pick> picksList = new ArrayList<Pick>();
		Integer prevUserId = null; 
		Integer userId = null;
		Integer gameId = null;
		Integer pickId = null;
		Integer favorite = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select p.* from Pick p, BowlGame bg where p.GameId = bg.GameId order by p.UserId, p.GameId");
			while (rs.next()) {
				userId = rs.getInt("UserId");
				gameId = rs.getInt("GameId");
				pickId = rs.getInt("PickId");
				favorite = rs.getInt("Favorite");
				if (prevUserId != null && userId.intValue()!= prevUserId.intValue()) {
					picksMap.put(prevUserId, picksList);
					picksList = new ArrayList<Pick>();
				}
				Pick p = new Pick(pickId, userId, gameId, favorite.intValue() == 1, 0, null);
				picksList.add(p);
				prevUserId = userId;
			}
			// add last one
			if (userId != null && gameId != null && pickId != null && favorite != null) {
				picksMap.put(userId, picksList);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return picksMap;
	}
	
	private static int getBowlGamesCount(Integer year, Connection conn) {
		int numberOfBowlGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where year = " + year);
			rs.next();
			numberOfBowlGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfBowlGames;
	}
	
	public static int getPicksCount(Integer year, Integer poolId, Connection conn) {
		int numberOfPicks = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from Pick p, BowlGame bg where p.gameId = bg.gameId and bg.year = " + 
			year + " and p.poolId = " + poolId);
			rs.next();
			numberOfPicks = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfPicks;
	}
	
	private static int getUsersCount(Integer year, Integer poolId, Connection conn) {
		int numberOfUsers = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from User where year = " + year + " and poolId = " + poolId);
			rs.next();
			numberOfUsers = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfUsers;
	}
	
	private static int getPoolIdFromYear(Integer year, Connection conn) {
		// Assumes one pool for the year
		int poolId = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select poolId from Pool where year = " + year);
			rs.next();
			poolId = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return poolId;
	}
	
	public static boolean useYearClause(Integer year) {
		boolean yearClause = false;
		
		if (year.intValue() >= 17) {
			yearClause = true;
		}
		return yearClause;
	}
	
	private static String getYearClause(Integer year, Integer poolId) {
		String yearClause = "year = " + year;
		if (poolId != null) {
			yearClause += " and PoolId = " + poolId;
		}
		return yearClause;
	}

}
