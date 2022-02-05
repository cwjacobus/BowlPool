package init;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.SQLException;  
public class BowlPoolDatabase { 
	
	private  Connection con;
	
	public BowlPoolDatabase(String url,String username,String password) {  
        try {  
              Class.forName("com.mysql.cj.jdbc.Driver"); 
              String connString = url + "?user=" + username + "&password=" + password + "&useSSL=false&allowPublicKeyRetrieval=true";
              this.con = DriverManager.getConnection(connString);  
        } catch (ClassNotFoundException e) {   
              e.printStackTrace();  
        } catch (SQLException e) {    
        	System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());  
        }  
	}
	  
	public Connection getCon() {  
		return con;  
    }  
        
}
