package actions;

import java.util.HashMap;
import java.util.List;
//import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import data.CFPGame;
import data.Pool;
import dao.DAO;

public class ExcludeCFPGameAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	Map<String, Object> userSession;
	Integer cfpGame;

	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		Pool pool = (Pool)userSession.get("pool");
		DAO.excludeGame(cfpGame, pool.getPoolId(), 1);
		@SuppressWarnings("unchecked")
		List<CFPGame> cfpGamesList = (List<CFPGame>) userSession.get("cfpGamesList");
		String description = null;
		Optional<CFPGame> gameMatch = 
				cfpGamesList
			.stream()
			.filter((p) -> p.getCfpGameId() == cfpGame.intValue())
			.findAny();
		if (gameMatch.isPresent()) {
			description = gameMatch.get().getDescription();
		}
		context.put("successMsg", description + " is excluded.");
	    stack.push(context);
	    return "success";
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }

	public Integer getCfpGame() {
		return cfpGame;
	}

	public void setCfpGame(Integer cfpGame) {
		this.cfpGame = cfpGame;
	}

}
