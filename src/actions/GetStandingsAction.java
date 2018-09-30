package actions;

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

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import data.ChampPick;
import data.Pick;
import data.Standings;
import data.User;

public class GetStandingsAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String year;
	int numOfBowlGames = 1;
	int lastGamePlayedIndex = 9;

	public String execute() throws Exception {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
        }
		Connection conn = null;
		try {
		    conn = DriverManager.getConnection("jdbc:mysql://localhost/bowlpool" + this.year + "?" +
		    	"user=root&password=PASSWORD");
		}
		catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		Map<Integer, List<Pick>> picksMap = getPicksMap(conn);
		Map<Integer, ChampPick> champPicksMap = getChampPicksMap(conn);
		TreeMap<String, Integer> standings = getStandings(conn);
		TreeMap<String, Standings> displayStandings = new TreeMap<String, Standings>(Collections.reverseOrder());
		
		//Iterate through standings to make formatted display string
		Iterator<Entry<String, Integer>> it = standings.entrySet().iterator();
    	int standingsIndex = 1;
    	int prevWins= 0;
    	int place = 1;
    	int eliminatedByCount = 0;
		while (it.hasNext()) {
			Map.Entry<String, Integer> line = (Map.Entry<String, Integer>)it.next();
			String[] lineKeyArray = line.getKey().split(":");
			int userId = line.getValue();
			String userName = lineKeyArray[1];
			int wins = Integer.parseInt(lineKeyArray[0]);
			if (wins != prevWins) {
				place = standingsIndex;
			}
			// Determine eliminated by count
			if (!userName.equalsIgnoreCase("dummy")) { // ignore dummy user
				eliminatedByCount = 0;
				Iterator<Entry<String, Integer>> it2 = standings.entrySet().iterator();
				while (it2.hasNext()) {
					Map.Entry<String, Integer> line2 = (Map.Entry<String, Integer>)it2.next();
					String[] lineKeyArray2 = line2.getKey().split(":");
					int userId2 = line2.getValue();
					int wins2 = Integer.parseInt(lineKeyArray2[0]);
					if (wins == wins2) {
						break;
					}
					List<Pick> userPicks1 = picksMap.get(userId);
					List<Pick> userPicks2 = picksMap.get(userId2);
					ChampPick userChampPick1 = champPicksMap.get(userId);
					ChampPick userChampPick2 = champPicksMap.get(userId2);
					int diffPicks =  getUsersRemainingDifferentPicks(conn, userPicks1, userPicks2, userChampPick1, userChampPick2);
					if ((wins + diffPicks) < wins2) {
						eliminatedByCount++;
					}
				}
			}
			Standings s = new Standings();
			s.setUserName(userName);
			s.setRank(place);
			s.setCorrect(wins);
			s.setEliminatedBy(eliminatedByCount);
			displayStandings.put(line.getKey(), s);
			standingsIndex++;
			prevWins = wins;
		}
		
		/*
		// Print eliminated map
		Iterator<Entry<Integer, Integer>> elimIt = eliminatedMap.entrySet().iterator();
		while (elimIt.hasNext()) {
			Map.Entry<Integer, Integer> userPicks = (Map.Entry<Integer, Integer>)elimIt.next();
			System.out.println("User: " + userPicks.getKey() + " elimCount: " + userPicks.getValue());
		}
				
		// Print picks map
		Iterator<Entry<Integer, List<Pick>>> it2 = picksMap.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry<Integer, List<Pick>> userPicks = (Map.Entry<Integer, List<Pick>>)it2.next();
			System.out.println("User: " + userPicks.getKey() + " num: " + userPicks.getValue().size());
		}*/
		
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();

	    context.put("standings", displayStandings);
	    stack.push(context);
	    //System.out.println("User: " + this.name + " Year: " + this.year);
	    return "success";
	}
	   
	public String getName() {
		return name;
	}

	public void setName(String name) {
	   this.name = name;
	}
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
	   this.year = year;
	}
	
	// DB
	public TreeMap<String, Integer> getStandings(Connection conn) {
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
		List<User> usersList = getUsersList(conn);
		// Merge any users with 0 wins
		for (User u : usersList) {
			if (!standings.containsValue(u.getUserId())) {
				standings.put("00:" + u.getUserName(), u.getUserId());
			}
		}
		return standings;
	}
	
	// DB
	public Map<Integer, List<Pick>> getPicksMap(Connection conn) {
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
					if (numOfBowlGames == 1) {
						numOfBowlGames = picksList.size();
					}
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
	
	public Map<Integer, ChampPick> getChampPicksMap(Connection conn) {
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
	
	private List<User> getUsersList(Connection conn) {
		List<User>userList = new ArrayList<User>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM User");
			User user;
			while (rs.next()) {
				user = new User(rs.getInt("UserId"), rs.getString("UserName"), rs.getString("LastName"),
					rs.getString("FirstName"), rs.getString("Email"));
				userList.add(user);
			}
		}
		catch (SQLException e) {
		}
		return userList;
	}
	
	private int getNumberOfCompletedGames(Connection conn) {
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
	
	private boolean isChampGameCompleted(Connection conn) {
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
	
	private int getUsersRemainingDifferentPicks(Connection conn, List<Pick> userPicks1, List<Pick> userPicks2, ChampPick userChampPick1, ChampPick userChampPick2) {
		int diffPicks = 0;
		int afterGameIndex = getNumberOfCompletedGames(conn);
		
		for (int i = afterGameIndex; i < numOfBowlGames; i++) {
			Pick p1 = userPicks1.get(i);
			Pick p2 = userPicks2.get(i);
			if (p1.getFavorite() != p2.getFavorite()) {
				diffPicks++;
			}
		}
		if (!userChampPick1.getWinner().equalsIgnoreCase(userChampPick2.getWinner()) && !isChampGameCompleted(conn)) {
			diffPicks++;
		}
		return diffPicks;	
	}
}
