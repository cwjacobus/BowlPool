package actions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import data.ChampPick;
import data.Pick;
import data.Standings;

public class GetStandingsAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer year = null;
	int numOfBowlGames = 1;
	int lastGamePlayedIndex = 9;

	public String execute() throws Exception {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
        }
		Connection conn = null;
		try {
			String connString = "jdbc:mysql://localhost/bowlpool";
		    if (this.year.intValue() < 17) { // only append year before 2017
		    	connString += this.year;
		    }
		    connString += "?user=root&password=PASSWORD";
		    conn = DriverManager.getConnection(connString);
		}
		catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		DAO.year = year;
		Map<Integer, List<Pick>> picksMap = DAO.getPicksMap(conn);
		Map<Integer, ChampPick> champPicksMap = DAO.getChampPicksMap(conn);
		TreeMap<String, Integer> standings = DAO.getStandings(conn);
		TreeMap<String, Standings> displayStandings = new TreeMap<String, Standings>(Collections.reverseOrder());
		
		numOfBowlGames = DAO.getBowlGamesList(conn).size();
		
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
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
	   this.year = year;
	}
		
	private int getUsersRemainingDifferentPicks(Connection conn, List<Pick> userPicks1, List<Pick> userPicks2, ChampPick userChampPick1, ChampPick userChampPick2) {
		int diffPicks = 0;
		int afterGameIndex = DAO.getNumberOfCompletedGames(conn);
		
		for (int i = afterGameIndex; i < numOfBowlGames; i++) {
			Pick p1 = userPicks1.get(i);
			Pick p2 = userPicks2.get(i);
			if (p1.getFavorite() != p2.getFavorite()) {
				diffPicks++;
			}
		}
		if (!userChampPick1.getWinner().equalsIgnoreCase(userChampPick2.getWinner()) && !DAO.isChampGameCompleted(conn)) {
			diffPicks++;
		}
		return diffPicks;	
	}
}
