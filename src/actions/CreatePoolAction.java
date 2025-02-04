package actions;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import init.BowlPoolDatabase;

public class CreatePoolAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private Integer year;
	private String poolName;
	private Integer ptsBowlGame;
	private Integer ptsRound1CFPGame;
	private Integer ptsQtrCFPGame;
	private Integer ptsSemiCFPGame;
	private Integer ptsChampCFPGame;
	private String usePointSpreads;
	private String copyUsers;
	
	public String execute() throws Exception {	
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
	    
	    BowlPoolDatabase bowlPoolDB = (BowlPoolDatabase)ServletActionContext.getServletContext().getAttribute("Database");  
        Connection con = bowlPoolDB.getCon();
		DAO.setConnection(con);
		
		if (!((year >= 24 && year <= 99) || (year >= 2024 && year <= 2099))) {   // years must be 24-99 or 2024-2099
			context.put("errorMsg", "Invalid year: " + year);
			stack.push(context);
			return "error";
		}
		if (year >= 2024) {  // Adjust 2 digit years
			year = year - 2000;
		}
		System.out.println("Create Pool: " + year + " Copy Users: " + (copyUsers != null ? "true" : "false"));
		if (DAO.createPool(poolName + " 20" + year, year, ptsBowlGame, ptsRound1CFPGame, ptsQtrCFPGame, ptsSemiCFPGame, 
				ptsChampCFPGame, usePointSpreads != null ? true : false)) {
			if (copyUsers != null && copyUsers.length() > 0) {
				DAO.copyUsersFromPreviousYear(year, poolName); // Populate the pool with previous years users
			}
			return "success";
		}
		else {
			context.put("errorMsg", "Pool already exists for: " + poolName + " " + year);
			stack.push(context);
			return "error";
		}
	}
		
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public Integer getPtsBowlGame() {
		return ptsBowlGame;
	}

	public void setPtsBowlGame(Integer ptsBowlGame) {
		this.ptsBowlGame = ptsBowlGame;
	}

	public Integer getPtsRound1CFPGame() {
		return ptsRound1CFPGame;
	}

	public void setPtsRound1CFPGame(Integer ptsRound1CFPGame) {
		this.ptsRound1CFPGame = ptsRound1CFPGame;
	}

	public Integer getPtsQtrCFPGame() {
		return ptsQtrCFPGame;
	}

	public void setPtsQtrCFPGame(Integer ptsQtrCFPGame) {
		this.ptsQtrCFPGame = ptsQtrCFPGame;
	}

	public Integer getPtsSemiCFPGame() {
		return ptsSemiCFPGame;
	}

	public void setPtsSemiCFPGame(Integer ptsSemiCFPGame) {
		this.ptsSemiCFPGame = ptsSemiCFPGame;
	}

	public Integer getPtsChampCFPGame() {
		return ptsChampCFPGame;
	}

	public void setPtsChampCFPGame(Integer ptsChampCFPGame) {
		this.ptsChampCFPGame = ptsChampCFPGame;
	}

	public String getUsePointSpreads() {
		return usePointSpreads;
	}

	public void setUsePointSpreads(String usePointSpreads) {
		this.usePointSpreads = usePointSpreads;
	}

	public String getCopyUsers() {
		return copyUsers;
	}

	public void setCopyUsers(String copyUsers) {
		this.copyUsers = copyUsers;
	}

	
}
