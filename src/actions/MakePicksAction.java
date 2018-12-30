package actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import data.ChampPick;
import data.Pick;
import data.User;

public class MakePicksAction extends ActionSupport implements SessionAware {
	
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
		User user = (User) userSession.get("user");
	    @SuppressWarnings("unchecked")
		Map<Integer, List<Pick>> picksMap = (Map<Integer, List<Pick>>) userSession.get("picksMap");
	    List<Pick> userPicks = picksMap.get(user.getUserId());
	    context.put("userPicks", userPicks);
	    @SuppressWarnings("unchecked")
		Map<Integer, ChampPick> champPicks = (Map<Integer, ChampPick>) userSession.get("champPicksMap");
	    if (champPicks != null) {
	    	ChampPick champPick = champPicks.get(user.getUserId());
	    	context.put("champPick", champPick);
	    }
	    stack.push(context);
	    return "success";
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }
}
