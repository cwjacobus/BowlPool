package actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.CFPGame;

public class CreateCFPGamesAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private List<String> seed;
	Map<String, Object> userSession;
	Map<Integer, CFPGame> cfpGamesMap;

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
	    cfpGamesMap = (Map<Integer, CFPGame>)userSession.get("cfpGamesMap");
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		for (String seededTeam : seed) {
			if (seededTeam == null || seededTeam.length() == 0) {
				context.put("errorMsg", "Not all playoff teams have been added!");
				stack.push(context);
				return "error";
			}
		}
		Integer year = (Integer) userSession.get("year");
		if (cfpGamesMap != null && cfpGamesMap.size() != 0) {
			DAO.deleteCfpGamesByYear(year);
		}
		for (int s = 0; s < seed.size(); s++) {
			System.out.print(seed.get(s) + " ");
		}
		System.out.println();
		
		// Create 11 games for a 12 team playoff
		DAO.createCFPGame("CFP Round 1 Game 1", 1, 1, seed.get(4), seed.get(11), year, 5, 12);
		DAO.createCFPGame("CFP Round 1 Game 2", 1, 2, seed.get(7), seed.get(8), year, 8, 9);
		DAO.createCFPGame("CFP Round 1 Game 3", 1, 3, seed.get(5), seed.get(10), year, 6, 11);
		DAO.createCFPGame("CFP Round 1 Game 4", 1, 4, seed.get(6), seed.get(9), year, 7, 10);
		DAO.createCFPGame("CFP Quarters Game 1", 2, 1, seed.get(3), null, year, 4, null);
		DAO.createCFPGame("CFP Quarters Game 2", 2, 2, seed.get(0), null, year, 1, null);
		DAO.createCFPGame("CFP Quarters Game 3", 2, 3, seed.get(2), null, year, 3, null);
		DAO.createCFPGame("CFP Quarters Game 4", 2, 4, seed.get(1), null, year, 2, null);
		DAO.createCFPGame("CFP Semis Game 2", 3, 2, null, null, year, null, null);
		DAO.createCFPGame("CFP Semis Game 1", 3, 1, null, null, year, null, null);
		DAO.createCFPGame("CFP Championship", 4, 1, null, null, year, null, null);
		
		context.put("successMsg", "CFP Games successfully created for: 20" + year);
		stack.push(context);
	    return "success";
	}

	public List<String> getSeed() {
		return seed;
	}


	public void setSeed(List<String> seed) {
		this.seed = seed;
	}


	public Map<String, Object> getUserSession() {
		return userSession;
	}


	public void setUserSession(Map<String, Object> userSession) {
		this.userSession = userSession;
	}


	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }

}