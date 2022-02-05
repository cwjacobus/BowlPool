package actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import data.BowlGame;

import dao.DAO;

public class CancelGameAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	Integer bowlGame;
	Map<String, Object> userSession;

	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		DAO.cancelBowlGame(bowlGame);
		@SuppressWarnings("unchecked")
		List<BowlGame> bowlGameList = (List<BowlGame>) userSession.get("bowlGamesList");
		String bowlName = null;
		Optional<BowlGame> gameMatch = 
				bowlGameList
			.stream()
			.filter((p) -> p.getGameId() == bowlGame.intValue())
			.findAny();
		if (gameMatch.isPresent()) {
			bowlName = gameMatch.get().getBowlName();
		}
		context.put("successMsg", bowlName + " is cancelled.");
	    stack.push(context);
	    return "success";
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }
	
	public Integer getBowlGame() {
		return bowlGame;
	}

	public void setBowlGame(Integer bowlGame) {
		this.bowlGame = bowlGame;
	}

}
