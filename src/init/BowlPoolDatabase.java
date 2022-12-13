package init;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.SQLException;
public class BowlPoolDatabase { 
	
	private  Connection con;
	private String connectionString;
	
	public BowlPoolDatabase(String url, String username, String password) {  
        try {  
              Class.forName("com.mysql.cj.jdbc.Driver"); 
              connectionString = url + "?user=" + username + "&password=" + password + "&useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true";
              this.con = DriverManager.getConnection(connectionString); 
              
              /*Statement stmt = con.createStatement();
              stmt.executeQuery("SET GLOBAL interactive_timeout=604800");
              stmt.executeQuery("SET GLOBAL wait_timeout=604800");*/
              
        } catch (ClassNotFoundException e) {   
              e.printStackTrace();  
        } 
        catch (SQLException e) {    
        	System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());  
        }  
	}
	  
	public Connection getCon() {  
		return con;  
    }
	
	public Connection reconnectAfterTimeout() {  
		try {
			this.con = DriverManager.getConnection(connectionString); 
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return con;  
    }
        
}
