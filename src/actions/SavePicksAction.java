package actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.Pick;
import data.Pool;
import data.User;

public class SavePicksAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private List<Integer> favorite;
	private List<Integer> underdog;
	private String champGame;
	private Integer champTotPts;
	private Integer champGameId;
	
	Map<String, Object> userSession;

	public String execute() throws Exception {
		System.out.println("Save picks");
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		if (favorite == null && underdog == null && champGame.length() == 0) {
			context.put("errorMsg", "No picks selected!");
			stack.push(context);
			return "error";
		}
		if (favorite != null && underdog != null && !Collections.disjoint(favorite, underdog)) {
			context.put("errorMsg", "Can not pick both teams in a game!");
			stack.push(context);
			return "error";
		}
		Integer year = (Integer)userSession.get("year");
		User user = (User)userSession.get("user");
		Pool pool = (Pool)userSession.get("pool");
		List<Pick> picksList = new ArrayList<Pick>();
		if (favorite != null) {
			for (Integer f : favorite) {
				System.out.println(f + " " + user.getUserId() + " " + pool.getPoolId() + " " + year);
				picksList.add(new Pick(0, user.getUserId(), f, true, pool.getPoolId()));
			}
		}
		if (underdog != null) {
			for (Integer u : underdog) {
				System.out.println(u + " " + user.getUserId() + " " + pool.getPoolId() + " " + year);
				picksList.add(new Pick(0, user.getUserId(), u, false, pool.getPoolId()));
			}
		}
		DAO.createBatchPicks(picksList, pool.getPoolId());
		if (champGame != null && champGame.trim().length() > 0) {
			System.out.println(champGame + " " + champGameId + " " + champTotPts + " " + user.getUserId() + " " + pool.getPoolId() + " " + year);
			DAO.createChampPick(user.getUserId(), champGameId, champGame, champTotPts, pool.getPoolId());
		}
	    stack.push(context);
	    return "success";
	}

	public List<Integer> getFavorite() {
		return favorite;
	}

	public void setFavorite(List<Integer> favorite) {
		this.favorite = favorite;
	}

	public List<Integer> getUnderdog() {
		return underdog;
	}

	public void setUnderdog(List<Integer> underdog) {
		this.underdog = underdog;
	}

	public String getChampGame() {
		return champGame;
	}

	public void setChampGame(String champGame) {
		this.champGame = champGame;
	}
	
	public Integer getChampTotPts() {
		return champTotPts;
	}

	public void setChampTotPts(Integer champTotPts) {
		this.champTotPts = champTotPts;
	}

	public Integer getChampGameId() {
		return champGameId;
	}

	public void setChampGameId(Integer champGameId) {
		this.champGameId = champGameId;
	}

	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }

}
