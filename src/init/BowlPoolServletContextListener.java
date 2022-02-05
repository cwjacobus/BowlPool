package init;

import javax.servlet.ServletContext;  
import javax.servlet.ServletContextEvent;  
import javax.servlet.ServletContextListener;  
public class BowlPoolServletContextListener implements ServletContextListener{  
      public void contextInitialized(ServletContextEvent event) {  
            ServletContext context = event.getServletContext();  
            String url = context.getInitParameter("URL");  
            String database=context.getInitParameter("Database name");  
            String username=context.getInitParameter("Username");  
            String password=context.getInitParameter("password");  
            BowlPoolDatabase mydb  = new BowlPoolDatabase(url+database, username, password);  
            context.setAttribute("Database", mydb); 
            System.out.println("context initialized - DB Info: " + url + database + " " + username);
      }  
      public void contextDestroyed(ServletContextEvent event) {  
    	  System.out.println("context destroyed");
      }  
}
