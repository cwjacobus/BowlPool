package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	
	public static void createBowlGame(String gameName, String favorite, String underdog, Double line, Integer year) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO BowlGame (BowlName, Favorite, Underdog, Spread, FavoriteScore, UnderdogScore, Completed, Year) VALUES ('" + 
				gameName + "', '" + favorite + "', '" + underdog + "' , " + line + ", 0, 0, false, " + year + ");";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createBatchPicks(List<Pick> picksList) {
		try {
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			int picksCount = 0;
			for (Pick p : picksList) {
				String insertSQL = "INSERT INTO Pick (UserId, GameId, Favorite) VALUES (" + 
					p.getUserId() + ", " + p.getGameId() + ", " + p.getFavorite() + ");";
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
	
	public static void createBatchChampPicks(HashMap<Integer, ChampPick> champPicksMap) {
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
				String insertSQL = "INSERT INTO ChampPick (UserId, GameId, Winner, TotalPoints) VALUES (" + 
					cp.getValue().getUserId() + ", " + cp.getValue().getGameId() + ", '" + cp.getValue().getWinner() + "', " 
						+ cp.getValue().getTotalPoints() + ");";
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
	
	/*
	public static void createPick(Integer userId, Integer gameId, Boolean favorite) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO Pick (UserId, GameId, Favorite) VALUES (" + 
				userId + ", " + gameId + ", " + favorite + ");";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createChampPick(Integer userId, Integer gameId, String winner) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO ChampPick (UserId, GameId, Winner) VALUES (" + 
				userId + ", " + gameId + ", '" + winner + "');";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
	
	public static int createUser(String userName, Integer year) {
		int userId = 0;
		try {
			Statement stmt = conn.createStatement();
			boolean admin = userName.equalsIgnoreCase("Jacobus") ? true : false;
			stmt.executeUpdate("INSERT INTO User (UserName, LastName, FirstName, Email, Year, admin) VALUES ('" + 
				userName + "', '', '', '', " + year + "," + admin + ");");
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
	
	public static TreeMap<String, Integer> getStandings(Integer year) {
		TreeMap<String, Integer> standings = new TreeMap<String, Integer>(Collections.reverseOrder());
		HashMap<Integer, Integer> champGameWinners = new HashMap<Integer, Integer>();
		System.out.println("Year: " + year);
		try { 
			Statement stmt1 = conn.createStatement();
			String query1String = "SELECT u.UserName, u.UserId, count(*) from Pick p, User u, BowlGame bg where  " +
					"p.userId= u.userId and bg.gameId = p.gameId and bg.completed = true and " + (useYearClause(year) ? getYearClause("bg", year) + " and ": "") + "(p.Favorite = true and " +  
					"(bg.FavoriteScore - bg.Spread > bg.UnderdogScore) or (p.Favorite = false and (bg.UnderdogScore + bg.Spread > bg.FavoriteScore))) " + 
					"group by u.UserName";
			ResultSet rs1 = stmt1.executeQuery(query1String);
			// Get who picked champ game correct
			if (useYearClause(year)) { // ChampPick did not exist until 2017
				Statement stmt2 = conn.createStatement();
				String query2String = "SELECT u.UserId, count(*) from ChampPick p, User u, BowlGame bg where " + 
						"p.userId = u.userId and bg.gameId = p.gameId and bg.completed = true and " + (useYearClause(year) ? getYearClause("bg", year) + " and ": "") +
						"(bg.Favorite = p.Winner and (bg.FavoriteScore > bg.UnderdogScore) or (bg.Underdog = p.Winner and (bg.UnderdogScore > bg.FavoriteScore))) " + 
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
		List<User> usersList = DAO.getUsersList(year);
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
			ResultSet rs = stmt.executeQuery("SELECT * FROM BowlGame" + (useYearClause(year) ? " where " + getYearClause(year): ""));
			BowlGame bowlGame;
			while (rs.next()) {
				bowlGame = new BowlGame(rs.getInt("GameId"), rs.getString("BowlName"), rs.getString("Favorite"),
						rs.getString("Underdog"), rs.getDouble("Spread"), rs.getInt("FavoriteScore"), 
						rs.getInt("UnderDogScore"), rs.getBoolean("Completed"), (useYearClause(year) ? rs.getInt("Year") : 0));
				bowlGameList.add(bowlGame);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return bowlGameList;
	}
	
	public static Map<Integer, List<Pick>> getPicksMap(Integer year) {
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
				(useYearClause(year) ? "and " + getYearClause("bg", year) : "") + " order by p.UserId, p.GameId");
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
				Pick p = new Pick(pickId, userId, gameId, favorite.intValue() == 1);
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
	
	public static Map<Integer, ChampPick> getChampPicksMap(Integer year) {
		Map<Integer, ChampPick> picksMap = new HashMap<Integer, ChampPick>();
		ChampPick champPick = new ChampPick();
		Integer userId = null;
		Integer gameId = null;
		Integer pickId = null;
		String winner = null;
		Integer totalPts = null;
		try {
			Statement stmt = conn.createStatement();
			String queryString = "select cp.* from ChampPick cp, BowlGame bg where cp.GameId = bg.GameId " +
					(useYearClause(year) ? "and " + getYearClause("bg", year) : "") + " order by cp.UserId";
			ResultSet rs = stmt.executeQuery(queryString);
			while (rs.next()) {
				userId = rs.getInt("UserId");
				gameId = rs.getInt("GameId");
				pickId = rs.getInt("PickId");
				winner = rs.getString("Winner");
				totalPts = rs.getInt("TotalPoints");
				champPick = new ChampPick(pickId, userId, gameId, winner, totalPts);
				picksMap.put(userId, champPick);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return picksMap;
	}
	
	public static User getUser(String name, Integer year) {
		User user = null;
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT * FROM User where userName = '" + name + "'" + (useYearClause(year) ? " and " + getYearClause(year): "");
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), (useYearClause(year) ? rs.getInt("Year") : 0), (useYearClause(year) ? rs.getBoolean("admin" ): false));
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
				pool = new Pool(rs.getInt("PoolId"), rs.getString("PoolName"), rs.getInt("Year"), rs.getBoolean("UsePointSpreads"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return pool;
	}
	
	public static List<User> getUsersList(Integer year) {
		List<User>userList = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User" + (useYearClause(year) ? " where " + getYearClause(year): ""));
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"), rs.getString("FirstName"), 
					rs.getString("Email"), (useYearClause(year) ? rs.getInt("Year") : 0), (useYearClause(year) ? rs.getBoolean("admin" ): false));
				userList.add(user);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return userList;
	}
	
	public static int getNumberOfCompletedGames(Integer year) {
		int numberOfCompletedGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where Completed = 1" + (useYearClause(year) ? " and " + getYearClause(year): ""));
			rs.next();
			numberOfCompletedGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfCompletedGames;
	}
	
	public static int getUsersCount(Integer year) {
		int numberOfUsers = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from User" + (useYearClause(year) ? " where " + getYearClause(year): ""));
			rs.next();
			numberOfUsers = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfUsers;
	}
	
	public static int getBowlGamesCount(Integer year) {
		int numberOfBowlGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame" + (useYearClause(year) ? " where " + getYearClause(year): ""));
			rs.next();
			numberOfBowlGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfBowlGames;
	}
	
	public static int getPicksCount(Integer year) {
		int numberOfPicks = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from Pick" + 
				(useYearClause(year) ? " p, BowlGame bg where p.gameId = bg.gameId and bg.year = " + year: ""));
			rs.next();
			numberOfPicks = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfPicks;
	}
	
	public static boolean isThereDataForAYear(int year) {
		int totalDataCount = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select (select count(*) from BowlGame where year = " +
				year + ") + (select count(*) from User where year = " + year + ") as total_rows from dual");
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
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where Completed = 1 and BowlName like '%Championship%'" + (useYearClause(year) ? " and " + getYearClause(year): ""));
			rs.next();
			numberOfCompletedGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfCompletedGames > 0;
	}
	
	public static void updateScore(Integer favoriteScore, Integer underDogScore, Integer gameId, String favorite, String underdog) {
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
	
	private static String getYearClause(Integer year) {
		return "year = " + year;
	}
	
	private static String getYearClause(String prefix, Integer year) {
		return prefix + ".year = " + year;
	}
	
	public static void setConnection(Integer year) {
		try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } 
		catch (Exception ex) {
        }
		try {
			String connString = "jdbc:mysql://localhost/bowlpool";
			if (year.intValue() < 17) { // only append year before 2017
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
