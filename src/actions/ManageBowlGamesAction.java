package actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.BowlGame;

public class ManageBowlGamesAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String year;
	
	Map<String, Object> userSession;

	public String execute() throws Exception {
		if (userSession == null || userSession.size() == 0) {
			return "invalidSession";
		}
		Integer year = (Integer) userSession.get("year");
		List<BowlGame> bowlGameList = DAO.getBowlGamesList(year);
		
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();

	    context.put("bowlGameList", bowlGameList);
	    stack.push(context);
	    return "success";
	}
	   
	public String getName() {
		return name;
	}

	public void setName(String name) {
	   this.name = name;
	}
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
	   this.year = year;
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }
}
