package dao;

import java.sql.Connection;
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
import data.CFTeam;
import data.ChampPick;
import data.Pick;
import data.Pool;
import data.User;

public class DAO {
	
	public static Connection conn; 
	
	public static void createBowlGame(String gameName, String favorite, String underdog, Double line, Integer year, Timestamp dateTime, Integer favScore, 
		Integer dogScore, boolean completed, boolean cancelled, Integer favoriteTeamId, Integer underdogTeamId, boolean cfpSemiGame, boolean cfpChampGame) {
		gameName = gameName.replaceAll("'", "");
		favorite = favorite.replaceAll("'", "");
		underdog = underdog.replaceAll("'", "");
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO BowlGame (BowlName, Favorite, Underdog, Spread, FavoriteScore, UnderdogScore, Completed, Year, DateTime, Cancelled, " +
				"FavoriteTeamId, UnderdogTeamId, CFPSemiGame, CFPChampGame) VALUES ('" + gameName + "', '" + favorite + "', '" + underdog + "' , " + line + ", " + 
				favScore + ", " +  dogScore + ", " + completed + ", " + year + "," + (dateTime != null ? "'" + dateTime + "'" : null) + ", " + cancelled + 
				", " + favoriteTeamId +  ", " + underdogTeamId +  ", " + cfpSemiGame +  ", " + cfpChampGame + ");";
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
	
	public static void createBatchCFTeams(List<CFTeam> cfTeamsList) {
		try {
			conn.setAutoCommit(false);
			Statement stmt = conn.createStatement();
			int cfTeamsCount = 0;
			for (CFTeam t : cfTeamsList) {
				String conference = t.getConference() != null ? "'" + t.getConference() + "'" : null;
				String insertSQL = "INSERT INTO CFTeam VALUES (" + 
					t.getCfTeamId() + ", '" + t.getSchool() + "', '" + t.getMascot() + "', " + conference + ", '" + t.getShortName() + "');";
				stmt.addBatch(insertSQL);
				cfTeamsCount++;
				if (cfTeamsCount % 250 == 0) {
					System.out.println("Insert cf teams " + (cfTeamsCount - 250) + " : " + cfTeamsCount);
					stmt.executeBatch();
					conn.commit();
					stmt.close();
					stmt = conn.createStatement();
				}
			}
			// Insert the remaining records
			System.out.println("Insert remaining cf teams " + (cfTeamsCount - (cfTeamsCount % 250)) + " : " + cfTeamsCount);
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
				"p.userId= u.userId and bg.gameId = p.gameId and bg.completed = true and bg.cancelled = false and " + 
				"bg.gameId not in (select gameId from ExcludedGame where poolId = " + pool.getPoolId() + ") and " +
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
				if ((rs1.getInt(3)  + champWin) < 10) {
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
			ResultSet rs = stmt.executeQuery("SELECT * FROM BowlGame where " + getYearClause(year, null) + 
				" and Cancelled = 0 order by DateTime");
			BowlGame bowlGame;
			while (rs.next()) {
				bowlGame = new BowlGame(rs.getInt("GameId"), rs.getString("BowlName"), rs.getString("Favorite"), rs.getString("Underdog"), 
					rs.getDouble("Spread"), rs.getInt("FavoriteScore"), rs.getInt("UnderDogScore"), rs.getBoolean("Completed"), rs.getInt("Year"), 
					rs.getTimestamp("DateTime"), rs.getBoolean("Cancelled"), rs.getInt("FavoriteTeamId"), rs.getInt("UnderdogTeamId"), 
					rs.getBoolean("CFPSemiGame"), rs.getBoolean("CFPChampGame"));
				bowlGameList.add(bowlGame);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return bowlGameList;
	}
	
	public static Map<Integer, BowlGame> getBowlGamesMap(Integer year) {
		Map<Integer, BowlGame> bowlGamesMap = new HashMap<Integer, BowlGame>();
		try {
			Statement stmt = conn.createStatement();
			// Only get games after first game start date
			//String firstGameStartSQL = firstGameStart != null ? "DateTime > '" + firstGameStart + "' and " : "";
			ResultSet rs = stmt.executeQuery("SELECT * FROM BowlGame where " + /*firstGameStartSQL +*/ getYearClause(year, null) + 
				" and Cancelled = 0 order by DateTime");
			BowlGame bowlGame;
			while (rs.next()) {
				bowlGame = new BowlGame(rs.getInt("GameId"), rs.getString("BowlName"), rs.getString("Favorite"), rs.getString("Underdog"), 
					rs.getDouble("Spread"), rs.getInt("FavoriteScore"), rs.getInt("UnderDogScore"), rs.getBoolean("Completed"), rs.getInt("Year"), 
					rs.getTimestamp("DateTime"), rs.getBoolean("Cancelled"), rs.getInt("FavoriteTeamId"), rs.getInt("UnderdogTeamId"),
					rs.getBoolean("CFPSemiGame"), rs.getBoolean("CFPChampGame"));
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
	
	public static List<Integer> getExcludedGamesList(Integer poolId) {
		List<Integer> excludedGameList = new ArrayList<Integer>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ExcludedGame where poolId = " + poolId);
			while (rs.next()) {
				excludedGameList.add(new Integer(rs.getInt("GameId")));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return excludedGameList;
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
				pool = new Pool(rs.getInt("PoolId"), rs.getString("PoolName"), rs.getInt("Year"), rs.getBoolean("UsePointSpreads")/*, rs.getTimestamp("FirstGameDate")*/);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return pool;
	}
	
	public static void pingDatabase() throws SQLException {
		Statement stmt = conn.createStatement();
		String sql = "SELECT * FROM Pool";
		stmt.executeQuery(sql);
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
	
	public static int getNumberOfCompletedGames(Pool pool) {
		int numberOfCompletedGames = 0;
		try {
			Statement stmt = conn.createStatement();
			//String firstGameStartSQL = pool.getFirstGameDate() != null ? "DateTime > '" + pool.getFirstGameDate() + "' and " : "";
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where Completed = 1 and GameId not in (select GameId from ExcludedGame where poolId = " + 
				pool.getPoolId() + ") and " + getYearClause(pool.getYear(), null)); // and " + firstGameStartSQL + getYearClause(pool.getYear(), null));
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
	
	public static int getBowlGamesCount(Integer year) {
		int numberOfBowlGames = 0;
		try {
			Statement stmt = conn.createStatement();
			//String firstGameStartSQL = firstGameStart != null ? "DateTime > '" + firstGameStart + "' and " : "";
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where " + /*firstGameStartSQL +*/ getYearClause(year, null) + " and Cancelled = 0");
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
	
	public static Map<String, CFTeam> getCFTeamsMap() {
		Map<String, CFTeam> cfTeamsMap = new HashMap<>();
		CFTeam cfTeam = new CFTeam();
		Integer cfTeamId = null;
		String school = null;
		String mascot = null;
		String conference = null;
		String shortName = null;
		try {
			Statement stmt = conn.createStatement();
			String queryString = "select * from CFTeam";
			ResultSet rs = stmt.executeQuery(queryString);
			while (rs.next()) {
				cfTeamId = rs.getInt("CFTeamId");
				school = rs.getString("School");
				mascot = rs.getString("Mascot");
				conference = rs.getString("Conference");
				shortName = rs.getString("ShortName");
				cfTeam = new CFTeam(cfTeamId, school, mascot, conference, shortName);
				cfTeamsMap.put(school.toUpperCase(), cfTeam);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return cfTeamsMap;
	}
	
	public static int getCFTeamsCount() {
		int numberOfCFTeams = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from CFTeam");
			rs.next();
			numberOfCFTeams = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfCFTeams;
	}
	
	public static Timestamp getFirstGameDateTime(int year) {
		Timestamp dt = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select min(dateTime) from BowlGame where year = " + year + " and Cancelled = 0");
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
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Favorite, Underdog FROM BowlGame where CFPSemiGame=1 and year = " + year);
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
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where Completed = 1 and CFPChampGame=1 and " + getYearClause(year, null));
			rs.next();
			numberOfCompletedGames = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return numberOfCompletedGames > 0;
	}
	
	// Create List of userIds with eliminated champ picks (lost in CFP Semi) for a pool
	public static List<Integer> getChampPickEliminatedList(Integer poolId) {
		List<Integer> champPickEliminatedList = new ArrayList<>();
		try {
			Statement stmt = conn.createStatement();
			//select count(*) from BowlGame where CFPSemiGame = 1 and " + getYearClause(year, null) + 
			// " and ((favorite = '" + champPick + "' and favoritescore <= underdogscore) or (underdog = '" + champPick + "' and underdogscore <= favoritescore));
			ResultSet rs = stmt.executeQuery("select cp.userid from bowlgame bg, champpick cp where bg.cfpsemigame = 1 and bg.completed = true and cp.poolid = " + poolId + 
				" and ((bg.favorite like concat('%', cp.winner, '%') and bg.favoritescore <= bg.underdogscore) or (bg.underdog like concat('%', cp.winner, '%') and bg.underdogscore <= bg.favoritescore));");
			while (rs.next()) {
				champPickEliminatedList.add(rs.getInt(1));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return champPickEliminatedList;
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
	
	public static void cancelBowlGame(Integer gameId) {
		try {
			Statement stmt = conn.createStatement();
			String updateSQL = "UPDATE BowlGame SET Cancelled = 1 WHERE GameId = " + gameId;
			stmt.execute(updateSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void excludeBowlGame(Integer gameId, Integer poolId) {
		try {
			Statement stmt = conn.createStatement();
			String insertSQL = "INSERT INTO ExcludedGame (GameId, PoolId) VALUES (" + gameId + ", " + poolId + ");";
			stmt.execute(insertSQL);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	public static void setConnection(Connection connection) {
		conn = connection;
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
