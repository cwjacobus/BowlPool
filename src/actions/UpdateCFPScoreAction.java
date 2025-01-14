package actions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.BowlGame;
import data.CFPGame;

public class UpdateCFPScoreAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Integer homeScore;
	private Integer visScore;
	private String home;
	private String visitor;
	private Integer cfpGameId;
	private String year;
	
	Map<String, Object> userSession;

	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		Integer year = (Integer) userSession.get("year");
		DAO.updateCFPGameScore(homeScore, visScore, cfpGameId, home, visitor);
		Map<Integer, CFPGame> cfpGamesMap = DAO.getCfpGamesMap(year);
	    List<CFPGame> cfpGamesList = new ArrayList<CFPGame>(cfpGamesMap.values());
	    Comparator<CFPGame> comparator = Comparator.comparing(CFPGame::getRound).thenComparing(CFPGame::getGameIndex);
	    cfpGamesList.sort(comparator);
	    context.put("cfpGamesList", cfpGamesList);
	    List<BowlGame> bowlGamesList = DAO.getBowlGamesList(year);
	    context.put("bowlGamesList", bowlGamesList);
	    stack.push(context);
	    return "success";
	}
	   
	
	public Integer getHomeScore() {
		return homeScore;
	}


	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}


	public Integer getVisScore() {
		return visScore;
	}


	public void setVisScore(Integer visScore) {
		this.visScore = visScore;
	}


	public String getHome() {
		return home;
	}


	public void setHome(String home) {
		this.home = home;
	}


	public String getVisitor() {
		return visitor;
	}


	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}


	public Integer getCfpGameId() {
		return cfpGameId;
	}


	public void setCfpGameId(Integer cfpGameId) {
		this.cfpGameId = cfpGameId;
	}


	public String getYear() {
		return year;
	}


	public void setYear(String year) {
		this.year = year;
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