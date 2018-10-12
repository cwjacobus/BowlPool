package actions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import dao.DAO;
import data.BowlGame;

public class ManageBowlGamesAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String year;

	public String execute() throws Exception {
		try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } 
		catch (Exception ex) {
        }
		Connection conn = null;
		try {
			String connString = "jdbc:mysql://localhost/bowlpool";
			if (Integer.parseInt(this.year) < 17) { // only append year before 2017
		    	connString += this.year;
		    }
		    connString += "?user=root&password=PASSWORD";
		    conn = DriverManager.getConnection(connString);
		}
		catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		DAO.year = Integer.parseInt(year);
		List<BowlGame> bowlGameList = DAO.getBowlGamesList(conn);
		
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
}
