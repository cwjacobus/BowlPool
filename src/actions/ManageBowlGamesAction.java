package actions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.BowlGame;
import data.CFPGame;

public class ManageBowlGamesAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	
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
		// Want to get the games list each time after an update, dont use session
		List<BowlGame> bowlGamesList = DAO.getBowlGamesList(year);
	    context.put("bowlGamesList", bowlGamesList);
	    Map<Integer, CFPGame> cfpGamesMap = DAO.getCfpGamesMap(year);
	    List<CFPGame> cfpGamesList = new ArrayList<CFPGame>(cfpGamesMap.values());
	    Comparator<CFPGame> comparator = Comparator.comparing(CFPGame::getRound).thenComparing(CFPGame::getGameIndex);
	    cfpGamesList.sort(comparator);
	    context.put("cfpGamesList", cfpGamesList);
	    stack.push(context);
	    return "success";
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }
}
