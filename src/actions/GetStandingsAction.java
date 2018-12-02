package actions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.struts2.interceptor.SessionAware;

import java.util.Map.Entry;

import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import data.ChampPick;
import data.Pick;
import data.Pool;
import data.Standings;
import data.User;

public class GetStandingsAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer year = null;
	private Integer poolId = null;
	private Pool pool;
	int numOfBowlGames = 1;
	int lastGamePlayedIndex = 9;
	Map<String, Object> userSession;
	
	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
	    
		userSession.put("year", year);
		DAO.setConnection(year);
		
		if (DAO.useYearClause(year)) {
			System.out.println("PoolID: " + poolId);
			pool = DAO.getPool(poolId);
			userSession.put("pool", pool);
		}
		
		System.out.println("Login: " + name);
		User user  = DAO.getUser(name, year, poolId);
		if (user != null || name.equalsIgnoreCase("admin")) { // Always allow admin to login to import users
			userSession.put("user", user);
		}
		else {
			context.put("errorMsg", "Invalid user!");
			stack.push(context);
			return "error";
		}
		
		Map<Integer, List<Pick>> picksMap = DAO.getPicksMap(year, poolId);
		Map<Integer, ChampPick> champPicksMap = null;
		if (DAO.useYearClause(year)) {
			champPicksMap = DAO.getChampPicksMap(year, poolId);
		}
		TreeMap<String, Integer> standings = DAO.getStandings(year, pool);
		TreeMap<String, Standings> displayStandings = new TreeMap<String, Standings>(Collections.reverseOrder());
		
		numOfBowlGames = DAO.getBowlGamesList(year).size();
		int numOfCompletedGames = DAO.getNumberOfCompletedGames(year);
		
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
					ChampPick userChampPick1 = null;
					ChampPick userChampPick2 = null;
					if (DAO.useYearClause(year)) {
						userChampPick1 = champPicksMap.get(userId);
						userChampPick2 = champPicksMap.get(userId2);
					}
					int diffPicks =  getUsersRemainingDifferentPicks(userPicks1, userPicks2, userChampPick1, userChampPick2, numOfCompletedGames);
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

	    context.put("standings", displayStandings);
	    boolean allowAdmin = false;
	    if ((user != null && user.isAdmin()) || name.equalsIgnoreCase("Jacobus")) {
	    	allowAdmin = true;
	    }
	    context.put("allowAdmin", allowAdmin);  
	    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
	    Date date1 = sdf.parse("12-15 07:30");
	    Calendar cal = Calendar.getInstance();
	    boolean makePicksLink = false;
	    //TBD check times of games
	    if (numOfBowlGames > 0 && cal.get(Calendar.MONTH) == Calendar.DECEMBER && date1.before(cal.getTime())) {
	    	makePicksLink = true;
	    }
	    context.put("numOfCompletedGames", numOfCompletedGames);
	    context.put("numOfRemainingGames", numOfBowlGames-numOfCompletedGames);
	    context.put("makePicksLink", makePicksLink);
	    stack.push(context);
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
	
	public Integer getPoolId() {
		return poolId;
	}

	public void setPoolId(Integer poolId) {
	   this.poolId = poolId;
	}
		
	private int getUsersRemainingDifferentPicks(List<Pick> userPicks1, List<Pick> userPicks2, 
		ChampPick userChampPick1, ChampPick userChampPick2, int numOfCompletedGames) {
		int diffPicks = 0;	
		if (userPicks1 == null) {
			return userPicks2.size();
		}
		else if (userPicks2 == null) {
			return userPicks1.size();
		}
		for (int i = numOfCompletedGames; i < numOfBowlGames; i++) {
			Pick p1 = userPicks1.get(i);
			Pick p2 = userPicks2.get(i);
			if (p1.getFavorite() != p2.getFavorite()) {
				diffPicks++;
			}
		}
		if (userChampPick1 != null && userChampPick2 != null) {
			if (!userChampPick1.getWinner().equalsIgnoreCase(userChampPick2.getWinner()) && !DAO.isChampGameCompleted(year)) {
				diffPicks++;
			}
		}
		return diffPicks;	
	}

	public void setSession(Map<String, Object> session) {
	   this.userSession = session ;
	}
}
