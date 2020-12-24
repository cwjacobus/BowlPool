package actions;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map.Entry;

import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import data.BowlGame;
import data.ChampPick;
import data.Pick;
import data.Pool;
import data.Standings;
import data.User;

class SortbyDate implements Comparator<BowlGame> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(BowlGame a, BowlGame b) 
    { 
    	if (a.getDateTime() == null || b.getDateTime() == null) {
    		return a.getGameId() < b.getGameId() ? -1 : 1;
    	}
    	else {
    		return a.getDateTime().before(b.getDateTime()) ? -1 : 1; 
    	}
    } 
}

public class GetStandingsAction extends ActionSupport implements Serializable, SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer poolId = null;
	private Pool pool;
	int numOfBowlGames = 1;
	int lastGamePlayedIndex = 9;
	Map<String, Object> userSession;
	Map<Integer, BowlGame> bowlGamesMap;
	
	final static Logger logger = Logger.getLogger(GetStandingsAction.class);
	
	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		
		DAO.setConnection();
		pool = DAO.getPool(poolId);
		if (pool == null) {
			context.put("errorMsg", "Pool does not exist!");
			stack.push(context);
			return "error";
		}
		userSession.put("pool", pool);
		userSession.put("year", pool.getYear());
		
		System.out.println("Login: " + name + " year: " + pool.getYear() + " poolId: " + poolId + " time: " + new Timestamp(new Date().getTime()));
		logger.info("Login: " + name + " year: " + pool.getYear() + " poolId: " + poolId);
		User user  = DAO.getUser(name, pool.getYear(), poolId);
		if (user != null || name.equalsIgnoreCase("admin")) { // Always allow admin to login to import users
			userSession.put("user", user);
		}
		else {
			context.put("errorMsg", "Invalid user!");
			stack.push(context);
			return "error";
		}
		Map<Integer, List<Pick>> picksMap = DAO.getPicksMap(pool.getYear(), poolId);
		userSession.put("picksMap", picksMap);
		Map<Integer, ChampPick> champPicksMap = null;
		if (DAO.useYearClause(pool.getYear())) {
			champPicksMap = DAO.getChampPicksMap(pool.getYear(), poolId);
			userSession.put("champPicksMap", champPicksMap);
		}
		TreeMap<String, Integer> standings = DAO.getStandings(pool.getYear(), pool);
		TreeMap<String, Standings> displayStandings = new TreeMap<String, Standings>(Collections.reverseOrder());
		bowlGamesMap = DAO.getBowlGamesMap(pool.getYear(), pool.getFirstGameDate());
		numOfBowlGames = bowlGamesMap.size();
		List<BowlGame> bowlGamesList = new ArrayList<BowlGame>(bowlGamesMap.values());
		Collections.sort(bowlGamesList, new SortbyDate()); 
		userSession.put("bowlGamesList", bowlGamesList);
		List<String> potentialChampionsList = DAO.getPotentialChampionsList(pool.getYear());
		userSession.put("potentialChampionsList", potentialChampionsList);
		
		int numOfCompletedGames = DAO.getNumberOfCompletedOrCancelledGames(pool.getYear(), pool.getFirstGameDate());
		if (pool != null && pool.getPoolId() == 5) {
			// special case Sculley 2018 does not use first game
			// TBD Create an OptOut table that allows a pool to skip a game gameId, poolId
			numOfBowlGames -= 1; 
			if (numOfCompletedGames > 0) {
				numOfCompletedGames -= 1;
			}
	    }
		//Iterate through standings to make formatted display string
		Iterator<Entry<String, Integer>> it = standings.entrySet().iterator();
    	int standingsIndex = 1;
    	int prevWins= 0;
    	int place = 1;
    	int eliminatedByCount = 0;
    	Map <String, Integer> eliminatedMap = new HashMap<String, Integer>();
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
				if (DAO.useYearClause(pool.getYear())) {
					userChampPick1 = champPicksMap.get(userId);
					userChampPick2 = champPicksMap.get(userId2);
				}
				int diffPicks =  getUsersRemainingDifferentPicks(userPicks1, userPicks2, userChampPick1, userChampPick2, numOfCompletedGames);
				if ((wins + diffPicks) < wins2) {
					eliminatedByCount++;
				}
			}
			Standings s = new Standings();
			s.setUserName(userName);
			s.setRank(place);
			s.setCorrect(wins);
			eliminatedMap.put(userName, eliminatedByCount);
			s.setEliminatedBy(eliminatedByCount);
			displayStandings.put(line.getKey(), s);
			standingsIndex++;
			prevWins = wins;
		}
		
		/*
		// Print eliminated map
		Iterator<Entry<String, Integer>> elimIt = eliminatedMap.entrySet().iterator();
		while (elimIt.hasNext()) {
			Map.Entry<String, Integer> userPicks = (Map.Entry<String, Integer>)elimIt.next();
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
	    if ((user != null && user.isAdmin()) || name.equalsIgnoreCase("admin") || name.equalsIgnoreCase("Jacobus")) {
	    	allowAdmin = true;
	    }
	    context.put("allowAdmin", allowAdmin);  
	    // SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
	    // Date date1 = sdf.parse("12-15-21" + pool.getYear() + " 11:00"); // Time of first game
	    Timestamp ts = pool.getFirstGameDate() == null ? DAO.getFirstGameDateTime(pool.getYear()) : pool.getFirstGameDate();
	    Date date1 = ts != null ? new Date(ts.getTime()) : null; // Time of first game
	    Calendar cal = Calendar.getInstance();
	   //TBD check times of games
	    if ((user != null && user.isAdmin()) || (numOfBowlGames > 0 && date1.after(cal.getTime()))) {
	    	userSession.put("readOnly", false);
	    }
	    else {
	    	userSession.put("readOnly", true);
	    }
	    context.put("numOfCompletedGames", numOfCompletedGames);
	    int numOfRemainingGames = numOfBowlGames-numOfCompletedGames;
	    context.put("numOfRemainingGames", numOfRemainingGames);
	    stack.push(context);
	    return "success";
	}
	   
	public String getName() {
		return name;
	}

	public void setName(String name) {
	   this.name = name;
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
		
		for (Pick up1 : userPicks1) {
			for (Pick up2 : userPicks2) {
				if (up1.getGameId() == up2.getGameId() && up1.getFavorite() != up2.getFavorite() && 
						!bowlGamesMap.get(up1.getGameId()).isCompleted() && !bowlGamesMap.get(up1.getGameId()).isCancelled()) {
					diffPicks++;
				}
			}
		}
		/*
		for (int i = numOfCompletedGames; i < numOfBowlGames - 1; i++) {
			Pick p1 = userPicks1.get(i);
			Pick p2 = userPicks2.get(i);
			if (p1.getFavorite() != p2.getFavorite()) {
				diffPicks++;
			}
		}*/
		
		if (userChampPick1 != null && userChampPick2 != null) {
			if (!userChampPick1.getWinner().equalsIgnoreCase(userChampPick2.getWinner()) && !DAO.isChampGameCompleted(pool.getYear())) {
				diffPicks++;
			}
		}
		return diffPicks;	
	}

	public void setSession(Map<String, Object> session) {
	   this.userSession = session ;
	}
}
