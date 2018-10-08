package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import data.BowlGame;
import data.ChampPick;
import data.Pick;
import data.User;

public class DAO {
	
	public static TreeMap<String, Integer> getStandings(Connection conn) {
		TreeMap<String, Integer> standings = new TreeMap<String, Integer>(Collections.reverseOrder());
		HashMap<Integer, Integer> champGameWinners = new HashMap<Integer, Integer>();
		try { 
			Statement stmt1 = conn.createStatement();
			ResultSet rs1 = stmt1.executeQuery("SELECT u.UserName, u.UserId, count(*) from Pick p, User u, BowlGame bg where  " +
				"p.userId= u.userId and bg.gameId = p.gameId and bg.completed = true and (p.Favorite = true and " + 
				"(bg.FavoriteScore - bg.Spread > bg.UnderdogScore) or (p.Favorite = false and (bg.UnderdogScore + bg.Spread > bg.FavoriteScore))) " + 
				"group by u.UserName");
			// Get who picked champ game correct
			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery("SELECT u.UserId, count(*) from ChampPick p, User u, BowlGame bg where " + 
				"p.userId = u.userId and bg.gameId = p.gameId and bg.completed = true and " +
				"(bg.Favorite = p.Winner and (bg.FavoriteScore > bg.UnderdogScore) or (bg.Underdog = p.Winner and (bg.UnderdogScore > bg.FavoriteScore))) group by u.UserName");
			while (rs2.next()) {
				champGameWinners.put(rs2.getInt(1), rs2.getInt(2));
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
		}
		List<User> usersList = DAO.getUsersList(conn);
		// Merge any users with 0 wins
		for (User u : usersList) {
			if (!standings.containsValue(u.getUserId())) {
				standings.put("00:" + u.getUserName(), u.getUserId());
			}
		}
		return standings;
	}
	
	public static List<BowlGame> getBowlGamesList(Connection conn) {
		List<BowlGame>bowlGameList = new ArrayList<BowlGame>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM BowlGame");
			BowlGame bowlGame;
			while (rs.next()) {
				bowlGame = new BowlGame(rs.getInt("GameId"), rs.getString("BowlName"), rs.getString("Favorite"),
						rs.getString("Underdog"), rs.getDouble("Spread"), rs.getInt("FavoriteScore"), 
						rs.getInt("UnderDogScore"), rs.getBoolean("Completed"), rs.getInt("Year"));
				bowlGameList.add(bowlGame);
			}
		}
		catch (SQLException e) {
		}
		return bowlGameList;
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
			ResultSet rs = stmt.executeQuery("select * from Pick order by UserId, GameId");
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
		}
		return picksMap;
	}
	
	public static Map<Integer, ChampPick> getChampPicksMap(Connection conn) {
		Map<Integer, ChampPick> picksMap = new HashMap<Integer, ChampPick>();
		ChampPick champPick = new ChampPick();
		Integer userId = null;
		Integer gameId = null;
		Integer pickId = null;
		String winner = null;
		Integer totalPts = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from ChampPick order by UserId");
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
		}
		return picksMap;
	}
	
	public static List<User> getUsersList(Connection conn) {
		List<User>userList = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User");
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"),
					rs.getString("FirstName"), rs.getString("Email"), rs.getInt("Year"));
				userList.add(user);
			}
		}
		catch (SQLException e) {
		}
		return userList;
	}
	
	public static int getNumberOfCompletedGames(Connection conn) {
		int numberOfCompletedGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where Completed = 1");
			rs.next();
			numberOfCompletedGames = rs.getInt(1);
		}
		catch (SQLException e) {
		}
		return numberOfCompletedGames;
	}
	
	public static boolean isChampGameCompleted(Connection conn) {
		int numberOfCompletedGames = 0;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from BowlGame where Completed = 1 and BowlName like '%Championship%'");
			rs.next();
			numberOfCompletedGames = rs.getInt(1);
		}
		catch (SQLException e) {
		}
		return numberOfCompletedGames > 0;
	}
	
	public static void updateScore(Connection conn, Integer favoriteScore, Integer underDogScore, Integer gameId, String favorite, String underdog) {
		String champUpdate = "";
		if (favorite != null && favorite.length() > 0 && underdog != null && underdog.length() > 0) {
			champUpdate += ", Favorite = '" + favorite + "', Underdog = '" + underdog + "'";
		}
		try {
			Statement stmt = conn.createStatement();
			stmt.execute("UPDATE BowlGame SET FavoriteScore = " + favoriteScore + ", UnderDogScore = " +  underDogScore + ", Completed = true" + champUpdate + " WHERE GameId = " + gameId);
		}
		catch (SQLException e) {
		}
		return;
	}
}
