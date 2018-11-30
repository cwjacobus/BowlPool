package actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import data.Pool;
import data.User;

public class SavePicksAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private List<Integer> favorite;
	private List<Integer> underdog;
	//private Integer gameId;
	private List<String> champGame;
	
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
		if (!Collections.disjoint(favorite, underdog)) {
			context.put("errorMsg", "Can not pick both teams in a game!");
			stack.push(context);
			return "error";
		}
		Integer year = (Integer)userSession.get("year");
		User user = (User)userSession.get("user");
		Pool pool = (Pool)userSession.get("pool");
		for (Integer f : favorite) {
			System.out.println(f + " " + user.getUserId() + " " + pool.getPoolName() + " " + year);
		}
		for (Integer u : underdog) {
			System.out.println(u + " " + user.getUserId() + " " + pool.getPoolName() + " " + year);
		}
	    stack.push(context);
	    return "success";
	}

	/*public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}*/

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

	public List<String> isChampGame() {
		return champGame;
	}

	public void setChampGame(List<String> champGame) {
		this.champGame = champGame;
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }

}
