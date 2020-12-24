package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import data.BowlGame;
import data.ChampPick;
import data.Pick;
import data.Pool;
import data.User;

public class DAO {
	
	public static Connection conn; 
	
	public static void createBowlGame(String gameName, String favorite, String underdog, Double line, Integer year, Timestamp dateTime, Integer favScore, 
		Integer dogScore, boolean completed, boolean cancelled) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO BowlGame (BowlName, Favorite, Underdog, Spread, FavoriteScore, UnderdogScore, Completed, Year, DateTime, Cancelled) VALUES ('" + 
				gameName + "', '" + favorite + "', '" + underdog + "' , " + line + ", " + favScore + ", " +  dogScore + ", " + completed + ", " + year + "," +
				(dateTime != null ? "'" + dateTime + "'" : null) + ", " + cancelled + ");";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createBatchPicks(List<Pick> picksList, Integer poolId) {
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
	
	public static void createBatchChampPicks(HashMap<Integer, ChampPick> champPicksMap, Integer poolId) {
		try {
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			Iterator<Entry<Integer, ChampPick>> it = champPicksMap.entrySet().iterator();
			/*while (it.hasNext()) {
				Map.Entry<Integer, ChampPick> cp = (Map.Entry<Integer, ChampPick>)it.next();
				System.out.println("cp: user: " + cp.getKey() + " game: " + cp.getValue().getGameId() + " winner: " + 
					cp.getValue().getWinner() + " pts: " + cp.getValue().getTotalPoints());
			}*/
			while (it.hasNext()) {
				Map.Entry<Integer, ChampPick> cp = (Map.Entry<Integer, ChampPick>)it.next();
				String insertSQL = "INSERT INTO ChampPick (UserId, GameId, Winner, TotalPoints, PoolId, CreatedTime) VALUES (" + 
					cp.getValue().getUserId() + ", " + cp.getValue().getGameId() + ", '" + cp.getValue().getWinner() + "', " 
						+ cp.getValue().getTotalPoints() + ", " + poolId + ", NOW());";
				stmt.addBatch(insertSQL);
			}
			System.out.println("Insert all ChampPick records");
			stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true); // set auto commit back to true for next inserts
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createChampPick(Integer userId, Integer gameId, String winner, Integer totalPoints, Integer poolId) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO ChampPick (UserId, GameId, Winner, TotalPoints, PoolId, CreatedTime) VALUES (" + 
				userId + ", " + gameId + ", '" + winner + "'," + totalPoints + ", " + poolId + ", NOW());";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int createUser(String userName, Integer year, Integer poolId) {
		int userId = 0;
		try {
			Statement stmt = conn.createStatement();
			boolean admin = userName.equalsIgnoreCase("Jacobus") ? true : false;
			stmt.executeUpdate("INSERT INTO User (UserName, LastName, FirstName, Email, Year, admin, PoolId) VALUES ('" + 
				userName + "', '', '', '', " + year + "," + admin + ", " + poolId + ");");
			ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			if (rs.next()) {
		       userId = rs.getInt(1);
			}
			//System.out.println("ID: " + userId);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userId;
	}
	
	public static void deleteChampPickByUserIdAndPoolId(Integer userId, Integer poolId) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "DELETE from ChampPick WHERE userId = " + userId + " and poolId = " + poolId;
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void deletePicksByUserIdAndPoolId(Integer userId, Integer poolId) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "DELETE from Pick WHERE userId = " + userId + " and poolId = " + poolId;
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static TreeMap<String, Integer> getStandings(Integer year, Pool pool) {
		TreeMap<String, Integer> standings = new TreeMap<String, Integer>(Collections.reverseOrder());
		HashMap<Integer, Integer> champGameWinners = new HashMap<Integer, Integer>();
		boolean useSpreads = pool != null ? pool.isUsePointSpreads() : true;
		try { 
			Statement stmt1 = conn.createStatement();
			String query1String = "SELECT u.UserName, u.UserId, count(*) from Pick p, User u, BowlGame bg where  " +
				"p.userId= u.userId and bg.gameId = p.gameId and bg.completed = true and " + 
				getYearClause("bg", year, "p", pool.getPoolId()) + " and " + "(p.Favorite = true and " +  
				"(bg.FavoriteScore - " + (useSpreads ? "bg.Spread" : "0") + " > bg.UnderdogScore) or " +
				"(p.Favorite = false and (bg.UnderdogScore + " + (useSpreads ? "bg.Spread" : "0") + " > bg.FavoriteScore))) " + 
				"group by u.UserName";
			ResultSet rs1 = stmt1.executeQuery(query1String);
			// Get who picked champ game correct
			if (useYearClause(year)) { // ChampPick did not exist until 2017
				Statement stmt2 = conn.createStatement();
				String query2String = "SELECT u.UserId, count(*) from ChampPick p, User u, BowlGame bg where " + 
					"p.userId = u.userId and bg.gameId = p.gameId and bg.completed = true and " + 
					getYearClause("bg", year, "p", pool.getPoolId()) + 
					" and (locate(p.winner, bg.Favorite) and (bg.FavoriteScore > bg.UnderdogScore) or (locate(p.winner, bg.Underdog) and (bg.UnderdogScore > bg.FavoriteScore))) " + 
					"group by u.UserName";
				ResultSet rs2 = stmt2.executeQuery(query2String);
				while (rs2.next()) {
					champGameWinners.put(rs2.getInt(1), rs2.getInt(2));
				}
			}
			while (rs1.next()) {
				int champWin = champGameWinners.get(rs1.getInt(2)) != null ? 1 : 0; 
				String wins = Integer.toString(rs1.getInt(3) + champWin); 
				if (rs1.getInt(3) < 10) {
					wins = "0" + wins;
				}
				standings.put(wins + ":" + rs1.getString(1), rs1.getInt(2));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		List<User> usersList = getUsersWithPicksList(year, pool != null ? pool.getPoolId() : null);
		// Merge any users with 0 wins
		for (User u : usersList) {
			if (!standings.containsValue(u.getUserId())) {
				standings.put("00:" + u.getUserName(), u.getUserId());
			}
		}
		return standings;
	}
	
	public static List<BowlGame> getBowlGamesList(Integer year) {
		List<BowlGame>bowlGameList = new ArrayList<BowlGame>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM BowlGame where " + getYearClause(year, null) + " order by DateTime");
			BowlGame bowlGame;
			while (rs.next()) {
				bowlGame = new BowlGame(rs.getInt("GameId"), rs.getString("BowlName"), rs.getString("Favorite"),
					rs.getString("Underdog"), rs.getDouble("Spread"), rs.getInt("FavoriteScore"), 
					rs.getInt("UnderDogScore"), rs.getBoolean("Completed"), rs.getInt("Year"), 
					rs.getTimestamp("DateTime"), rs.getBoolean("Cancelled"));
				bowlGameList.add(bowlGame);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return bowlGameList;
	}
	
	public static Map<Integer, BowlGame> getBowlGamesMap(Integer year, Timestamp firstGameStart) {
		Map<Integer, BowlGame> bowlGamesMap = new HashMap<Integer, BowlGame>();
		try {
			Statement stmt = conn.createStatement();
			// Only get games after first game start date
			String firstGameStartSQL = firstGameStart != null ? "DateTime > '" + firstGameStart + "' and " : "";
			ResultSet rs = stmt.executeQuery("SELECT * FROM BowlGame where " + firstGameStartSQL + getYearClause(year, null) + " order by DateTime");
			BowlGame bowlGame;
			while (rs.next()) {
				bowlGame = new BowlGame(rs.getInt("GameId"), rs.getString("BowlName"), rs.getString("Favorite"),
					rs.getString("Underdog"), rs.getDouble("Spread"), rs.getInt("FavoriteScore"), rs.getInt("UnderDogScore"), 
					rs.getBoolean("Completed"), rs.getInt("Year"), rs.getTimestamp("DateTime"), rs.getBoolean("Cancelled"));
				bowlGamesMap.put(bowlGame.getGameId(), bowlGame);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return bowlGamesMap;
	}
	
	public static Map<Integer, List<Pick>> getPicksMap(Integer year, Integer poolId) {
		Map<Integer, List<Pick>> picksMap = new HashMap<Integer, List<Pick>>();
		ArrayList<Pick> picksList = new ArrayList<Pick>();
		Integer prevUserId = null; 
		Integer userId = null;
		Integer gameId = null;
		Integer pickId = null;
		Integer favorite = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select p.* from Pick p, BowlGame bg where p.GameId = bg.GameId " +
				"and " + getYearClause("bg", year, "p", poolId) + " order by p.UserId, p.GameId");
			while (rs.next()) {
				userId = rs.getInt("UserId");
				gameId = rs.getInt("GameId");
				pickId = rs.getInt("PickId");
				favorite = rs.getInt("Favorite");
				if (prevUserId != null && userId.intValue()!= prevUserId.intValue()) {
					picksMap.put(prevUserId, picksList);
					//if (numOfBowlGames == 1) {
						//numOfBowlGames = picksList.size();
					//}
					picksList = new ArrayList<Pick>();
				}
				Pick p = new Pick(pickId, userId, gameId, favorite.intValue() == 1, poolId, null);
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
	
	public static Map<Integer, ChampPick> getChampPicksMap(Integer year, Integer poolId) {
		Map<Integer, ChampPick> picksMap = new HashMap<Integer, ChampPick>();
		ChampPick champPick = new ChampPick();
		Integer userId = null;
		Integer gameId = null;
		Integer pickId = null;
		String winner = null;
		Integer totalPts = null;
		try {
			Statement stmt = conn.createStatement();
			String queryString = "select cp.* from ChampPick cp, BowlGame bg where cp.GameId = bg.GameId and " +
				getYearClause("bg", year, "cp", poolId) + " order by cp.UserId";
			ResultSet rs = stmt.executeQuery(queryString);
			while (rs.next()) {
				userId = rs.getInt("UserId");
				gameId = rs.getInt("GameId");
				pickId = rs.getInt("PickId");
				winner = rs.getString("Winner");
				totalPts = rs.getInt("TotalPoints");
				champPick = new ChampPick(pickId, userId, gameId, winner, totalPts, poolId, null);
				picksMap.put(userId, champPick);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return picksMap;
	}
	
	public static User getUser(String name, Integer year, Integer poolId) {
		User user = null;
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM User where userName = '" + name + "' and " + getYearClause(year, poolId);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), rs.getInt("Year"), rs.getBoolean("admin"), rs.getInt("PoolId"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public static Pool getPool(Integer poolId) {
		Pool pool = null;
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM Pool where poolId = " + poolId;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				pool = new Pool(rs.getInt("PoolId"), rs.getString("PoolName"), rs.getInt("Year"), rs.getBoolean("UsePointSpreads"), rs.getTimestamp("FirstGameDate"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return pool;
	}
	
	public static List<User> getUsersList(Integer year, Integer poolId) {
		List<User>userList = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User where " + getYearClause(year, poolId));
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), rs.getInt("Year"), rs.getBoolean("admin"), rs.getInt("PoolId"));
				userList.add(user);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
	
	public static List<User> getUsersWithPicksList(Integer year, Integer poolId) {
		List<User>userList = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT distinct u.* FROM User u, Pick p where u.userId = p.userId and p.poolID = " + poolId);
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), rs.getInt("Year"), rs.getBoolean("admin"), rs.getInt("PoolId"));
				userList.add(user);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
	
	public static int getNumberOfCompletedOrCancelledGames(Integer year, Timestamp firstGameStart) {
		int numberOfCompletedGames = 0;
		try {
			Statement stmt = conn.createStatement();
			String firstGameStartSQL = firstGameStart != null ? "DateTime > '" + firstGameStart + "' and " : "";
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where (Completed = 1 or Cancelled = 1) and " + firstGameStartSQL + getYearClause(year, null));
			rs.next();
			numberOfCompletedGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfCompletedGames;
	}
	
	public static int getUsersCount(Integer year, Integer poolId) {
		int numberOfUsers = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from User where " + getYearClause(year, poolId));
			rs.next();
			numberOfUsers = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfUsers;
	}
	
	public static int getBowlGamesCount(Integer year, Timestamp firstGameStart) {
		int numberOfBowlGames = 0;
		try {
			Statement stmt = conn.createStatement();
			String firstGameStartSQL = firstGameStart != null ? "DateTime > '" + firstGameStart + "' and " : "";
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where " + firstGameStartSQL + getYearClause(year, null));
			rs.next();
			numberOfBowlGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfBowlGames;
	}
	
	public static int getPicksCount(Integer year, Integer poolId) {
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
	
	public static Timestamp getFirstGameDateTime(int year) {
		Timestamp dt = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select min(dateTime) from BowlGame where year =" + year);
			rs.next();
			dt = rs.getTimestamp(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return dt;
	}
	
	public static List<String> getPotentialChampionsList(int year) {
		List<String>potentialChampionsList = new ArrayList<String>();
		try {
			// select favorite, underdog from bowlgame where year=19 and bowlname like '%CFP Semifinal%';
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Favorite, Underdog FROM BowlGame where BowlName like '%CFP Semifinal%' and year = " + year);
			while (rs.next()) {
				potentialChampionsList.add(rs.getString(1)); // favorite
				potentialChampionsList.add(rs.getString(2)); // underdog
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return potentialChampionsList;
	}
	
	public static boolean isThereDataForAYear(int year) {
		int totalDataCount = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select (select count(*) from BowlGame where year = " + year + 
				") + (select count(*) from User where year = " + year + ") as total_rows from dual");
			rs.next();
			totalDataCount = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return totalDataCount == 0;
	}
	
	public static boolean isChampGameCompleted(Integer year) {
		int numberOfCompletedGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where Completed = 1 and BowlName like '%Championship%' and " + getYearClause(year, null));
			rs.next();
			numberOfCompletedGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfCompletedGames > 0;
	}
	
	public static void updateBowlGameSpread(Integer gameId, Double spread) {
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("UPDATE BowlGame SET Spread = " + spread + " WHERE GameId = " + gameId);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	
	public static void updateBowlGameScore(Integer favoriteScore, Integer underDogScore, Integer gameId, String favorite, String underdog) {
		String champUpdate = "";
		if (favorite != null && favorite.length() > 0 && underdog != null && underdog.length() > 0) {
			champUpdate += ", Favorite = '" + favorite + "', Underdog = '" + underdog + "'";
		}
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("UPDATE BowlGame SET FavoriteScore = " + favoriteScore + ", UnderDogScore = " +  underDogScore + ", Completed = true" + champUpdate + " WHERE GameId = " + gameId);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return;
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
	
	private static String getYearClause(String yearPrefix, Integer year, String poolIdPrefix, Integer poolId) {
		String yearClause = yearPrefix + ".year = " + year;
		if (poolId != null) {
			yearClause += " and " + poolIdPrefix + ".PoolId = " + poolId;
		}
		return yearClause;
	}
	
	public static void setConnection() {
		try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } 
		catch (Exception ex) {
        }
		try {
			String connString = "jdbc:mysql://localhost/bowlpool";
			/*if (year != null && year.intValue() < 17) { // only append year before 2017
			    connString += year;
			}*/
			connString += "?user=root&password=PASSWORD&useSSL=false&allowPublicKeyRetrieval=true";
			conn = DriverManager.getConnection(connString);
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	/*
	public static void updateChampPickTotPts(Integer userId, String totPts) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "UPDATE ChampPick SET TotalPoints=" + totPts + " where UserId=" + userId + ";";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
}
