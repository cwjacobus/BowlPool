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
	private String cfp1;
	private String cfp2;
	private String cfp3;
	private String cfp4;
	private String cfp5;
	private String cfp6;
	private String cfp7;
	private String cfp8;
	private String cfpSemi1;
	private String cfpSemi2;
	private String cfpChamp;
	//private String champGame;
	//private Integer champTotPts;
	//private Integer champGameId;
	
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
		if (favorite == null && underdog == null/* && champGame.length() == 0*/) {
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
				picksList.add(new Pick(0, user.getUserId(), f, true, pool.getPoolId(), null));
			}
		}
		if (underdog != null) {
			for (Integer u : underdog) {
				System.out.println(u + " " + user.getUserId() + " " + pool.getPoolId() + " " + year);
				picksList.add(new Pick(0, user.getUserId(), u, false, pool.getPoolId(), null));
			}
		}
		if (picksList.size() > 0) {
			DAO.deletePicksByUserIdAndPoolId(user.getUserId(), pool.getPoolId());
		}
		Thread.sleep(1000);
		DAO.createBatchPicks(picksList, pool.getPoolId());
		/*
		if (champGame != null && champGame.trim().length() > 0) {
			System.out.println(champGame + " " + champGameId + " " + champTotPts + " " + user.getUserId() + " " + pool.getPoolId() + " " + year);
			DAO.deleteChampPickByUserIdAndPoolId(user.getUserId(), pool.getPoolId());
			Thread.sleep(1000);
			DAO.createChampPick(user.getUserId(), champGameId, champGame, champTotPts, pool.getPoolId());
		}
		*/
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

	/*public String getChampGame() {
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
	}*/

	public String getCfp1() {
		return cfp1;
	}

	public void setCfp1(String cfp1) {
		this.cfp1 = cfp1;
	}

	public String getCfp2() {
		return cfp2;
	}

	public void setCfp2(String cfp2) {
		this.cfp2 = cfp2;
	}

	public String getCfp3() {
		return cfp3;
	}

	public void setCfp3(String cfp3) {
		this.cfp3 = cfp3;
	}

	public String getCfp4() {
		return cfp4;
	}

	public void setCfp4(String cfp4) {
		this.cfp4 = cfp4;
	}

	public String getCfp5() {
		return cfp5;
	}

	public void setCfp5(String cfp5) {
		this.cfp5 = cfp5;
	}

	public String getCfp6() {
		return cfp6;
	}

	public void setCfp6(String cfp6) {
		this.cfp6 = cfp6;
	}

	public String getCfp7() {
		return cfp7;
	}

	public void setCfp7(String cfp7) {
		this.cfp7 = cfp7;
	}

	public String getCfp8() {
		return cfp8;
	}

	public void setCfp8(String cfp8) {
		this.cfp8 = cfp8;
	}

	public String getCfpSemi1() {
		return cfpSemi1;
	}

	public void setCfpSemi1(String cfpSemi1) {
		this.cfpSemi1 = cfpSemi1;
	}

	public String getCfpSemi2() {
		return cfpSemi2;
	}

	public void setCfpSemi2(String cfpSemi2) {
		this.cfpSemi2 = cfpSemi2;
	}

	public String getCfpChamp() {
		return cfpChamp;
	}

	public void setCfpChamp(String cfpChamp) {
		this.cfpChamp = cfpChamp;
	}

	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }

}
