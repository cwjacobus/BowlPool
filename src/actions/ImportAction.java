package actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.BowlGame;
import data.ChampPick;
import data.Pick;
import data.User;

public class ImportAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private String usersCB;
	private String gamesCB;
	private String picksCB;
	private String inputFileName;
	private String fromWS;
	
	boolean usersImport = false;
	boolean picksImport = false;
	boolean bowlGamesImport = false;
	
	Map<String, Object> userSession;
	
	Integer year;

	public String execute() throws Exception {
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();
		if (userSession == null || userSession.size() == 0) {
			context.put("errorMsg", "Session has expired!");
			stack.push(context);
			return "error";
		}
		year = (Integer) userSession.get("year");
		System.out.println("Import: " + year);
		
		InputStream input = ServletActionContext.getServletContext().getResourceAsStream("/WEB-INF/BowlPool.properties");
		Properties prop = new Properties();
		prop.load(input);
		System.out.println("Input file path: " + prop.getProperty("inputFilePath"));
		
	    System.out.println("Import " + usersCB + " " + gamesCB + " " + picksCB + " " + fromWS + " " + inputFileName);
	    if (usersCB == null && gamesCB == null && picksCB == null) {
	    	context.put("errorMsg", "Nothing selected to import!");
	    	stack.push(context);
	    	return "error";
	    }
	    else if ((inputFileName == null || inputFileName.length() == 0) && 
	    		(usersCB != null || picksCB != null || (gamesCB != null && fromWS == null))) {
	    	context.put("errorMsg", "No file selected to import!");
	    	stack.push(context);
	    	return "error";
	    }
	    else {
	    	if (usersCB != null) {
	    		usersImport = true; 
	    		// Check for users already imported
	    		if (DAO.getUsersCount(year) > 0) {
	    			context.put("errorMsg", "Users already imported for 20" + year + "!  Delete and reimport.");
	    			stack.push(context);
	    			return "error";
	    		}
	    	}
	    	if (picksCB != null) {
	    		picksImport = true;
	    		// Check for games imported
	    		if (DAO.getBowlGamesCount(year) == 0) {
	    			context.put("errorMsg", "Bowl Games not imported for 20" + year + "!  Import Bowl Games.");
	    			stack.push(context);
	    			return "error";
	    		}
	    		// Check for picks already imported
	    		if (DAO.getPicksCount(year) > 0) {
	    			context.put("errorMsg", "Picks already imported for 20" + year + "!  Delete and reimport.");
	    			stack.push(context);
	    			return "error";
	    		}
	    	}
	    	if (gamesCB != null) {
	    		bowlGamesImport = true; 
	    		// Check for games already imported
	    		if (DAO.getBowlGamesCount(year) > 0) {
	    			context.put("errorMsg", "Bowl Games already imported for 20" + year + "!  Delete and reimport.");
	    			stack.push(context);
	    			return "error";
	    		}
	    	}
	    	if (usersImport || picksImport || (bowlGamesImport && fromWS == null)) {
	    		File inputFile = new File(prop.getProperty("inputFilePath") + inputFileName);
	    		FileInputStream spreadSheetFile = new FileInputStream(inputFile);
	     
	    		//Create Workbook instance holding reference to .xls file
	    		HSSFWorkbook hWorkbook = new HSSFWorkbook(spreadSheetFile);
	    		if (usersImport || picksImport) {
	    			importUsersAndPicks(hWorkbook);
	    		}
	    		if (bowlGamesImport) {
	    			importBowlGamesFromFile(hWorkbook);
	    		} 
	    	}
	    	else if (bowlGamesImport && fromWS != null) {
	    		importBowlGamesFromWS();
	    	}
	    }
	    stack.push(context);
		
	    return "success";
	}
	
	private void importUsersAndPicks(HSSFWorkbook hWorkbook) {
		List<BowlGame> bowlGameList = DAO.getBowlGamesList(year);
		List<User> userList = DAO.getUsersList(year);
		List<Pick> picksList = new ArrayList<Pick>();
		HashMap<Integer, ChampPick> champPicksMap = new HashMap<Integer, ChampPick>();
		try {  
			HashMap<Integer, String> bowlGameNameMap = null;
			HSSFSheet sheet = hWorkbook.getSheetAt(0);
	        System.out.println(sheet.getSheetName());
	        Iterator<Row> rowIterator = sheet.iterator();
	        boolean usersFound = false;
	        boolean userCreationStarted = false;
	        String prevUser = null;
	        while (rowIterator.hasNext()) {
	        	Row row = rowIterator.next();
	        	String userName = getStringFromCell(row, 0);
	        	String bowlGameName = getStringFromCell(row, 1);
	        	if (bowlGameName!= null && bowlGameName.equals("Bowl")) {
	        		bowlGameNameMap = createBowlGameNameMap(row);
	        	}
	        	if (userName!= null && userName.equals("NAME")) {
	        		usersFound = true;
	        		continue;
	        	}
	        	if (!usersFound) {
	        		continue;
	        	}
	        	if (usersFound && userName == null && prevUser == null && userCreationStarted) {
	        		break;
	        	}
	        	if (userName != null) {
	        		System.out.println(userName);
	        		if (usersImport) {
	        			DAO.createUser(userName, year);
	        			userCreationStarted = true;
	        		}
	        		if (picksImport) {
	        			//Iterator<Entry<Integer, String>> it = bowlGameNameMap.entrySet().iterator();
	        			//while (it.hasNext()) {
	        				//Map.Entry<Integer, String> game = (Map.Entry<Integer, String>)it.next();
	        				//System.out.println("game: " + game.getValue() + " " + game.getKey());
	        			//}
	        			User user = null;
	        			for (User u : userList) {
        					if (u.getUserName().equalsIgnoreCase(userName)) {
        						user = u;
        						break;
        					}
        				}
	        			Iterator<Cell> cellIter = row.cellIterator();
	        			int gameIndex = 0;
	        			while (cellIter.hasNext()){
	        				Cell cell = (Cell)cellIter.next();
	        				if (cell.getColumnIndex() == 0 || cell.getColumnIndex() == 1) {
	        					continue;
	        				}
	        				String pick = getStringFromCell(row, cell.getColumnIndex());
	        				BowlGame bowlGame = null;
	        				for (BowlGame bg : bowlGameList) {
	        					if (bg.getBowlName().equalsIgnoreCase(bowlGameNameMap.get(gameIndex))) {
	        						bowlGame = bg;
	        						break;
	        					}
	        				}
	        				boolean champPick = bowlGame.getBowlName().equalsIgnoreCase("Championship");
	        				if (!champPick) {
	        					if ((pick != null && pick.length() > 0) && (cell.getColumnIndex() % 2 == 0)) {
	        						System.out.print("FAV-" + bowlGameNameMap.get(gameIndex) + " ");
	        						// create fav Pick
	        						//DAO.createPick(user.getUserId(), bowlGame.getGameId(), true);
	        						picksList.add(new Pick(0, user.getUserId(), bowlGame.getGameId(), true));
	        					}
	        					if ((pick != null && pick.length() > 0) && (cell.getColumnIndex() % 2 != 0)) {
	        						System.out.print("DOG-" + bowlGameNameMap.get(gameIndex) + " ");
	        						// create dog Pick
	        						//DAO.createPick(user.getUserId(), bowlGame.getGameId(), false);
	        						picksList.add(new Pick(0, user.getUserId(), bowlGame.getGameId(), false));
	        					}
	        				}
	        				else {
	        					if (cell.getColumnIndex() % 2 == 0) {
	        						System.out.print("CHAMP WINNER-" + pick + " ");
	        						// create Winner ChampPick
	        						//DAO.createChampPick(user.getUserId(), bowlGame.getGameId(), pick);
	        						champPicksMap.put(new Integer(user.getUserId()), new ChampPick(0, user.getUserId(), bowlGame.getGameId(), pick, 0));
	        					}
	        					else if (cell.getColumnIndex() % 2 != 0) { // Assumes ChampPick record has been created previously to update
	        						System.out.print("CHAMP TOTAL POINTS-" + pick + " ");
	        						// create Winner ChampPick
	        						//DAO.updateChampPickTotPts(user.getUserId(), pick);
	        						ChampPick cp = champPicksMap.get(user.getUserId());
	        						if (cp != null) {
	        							cp.setTotalPoints(Double.parseDouble(pick));
	        							champPicksMap.put(cp.getUserId(), cp);
	        						}
	        					}
	        				}
	        				if (cell.getColumnIndex() % 2 != 0) {
	        					gameIndex++;
	        				}
	        				if (gameIndex == bowlGameNameMap.size()) {
	        					break;
	        				}
	        	        }
	        			System.out.println();
	        		}
	        	}
	        	prevUser = userName;
	        }
	        if (picksList.size() > 0) {
	        	DAO.createBatchPicks(picksList);
	        }
	        if (champPicksMap.size() > 0) {
	        	DAO.createBatchChampPicks(champPicksMap);
	        }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	private void importBowlGamesFromFile(HSSFWorkbook hWorkbook) {	
		HSSFSheet sheet = hWorkbook.getSheetAt(1);
		System.out.println(sheet.getSheetName());
		Iterator<Row> rowIterator = sheet.iterator();
		boolean gamesFound = false;
		String prevGame = null;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			String gameName = getStringFromCell(row, 1);
			if (gameName!= null && gameName.equalsIgnoreCase("Bowl")) {
				gamesFound = true;
				continue;
			}
			if (!gamesFound) {
				continue;
			}
			if (gamesFound && ((gameName == null && prevGame == null)) || (gameName != null && gameName.indexOf("Championship") == 0)) {
				// Create a blank Championship game place holder and break;
				DAO.createBowlGame("Championship", "", "", 0.0, year);
				break;
			}
			if (gameName != null) {
				System.out.println(gameName);
				String favorite = getStringFromCell(row, 3).trim();
				String lineString = getStringFromCell(row, 5).trim();
				double line = 0;
				if (!lineString.equalsIgnoreCase("pick")) {
					lineString = lineString.replace("-", "");
					line = Double.parseDouble(lineString);
				}
				String underdog = getStringFromCell(row, 7).trim();
				DAO.createBowlGame(gameName, favorite, underdog, line, year);
			}
			prevGame = gameName;
		}
	}
	
	private void importBowlGamesFromWS() {
		System.out.println("Import games from web service");
		try {
			String uRL;
			uRL = "https://api.fantasydata.net/v3/cfb/odds/json/GameOddsByWeek/2018/13?key=712e473edfa34aaf82cdf73469a772b7"; // 2017POST/1 
			URL obj = new URL(uRL);
			HttpURLConnection con = (HttpURLConnection)obj.openConnection();
			//int responseCode = con.getResponseCode();
			//System.out.println("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			JSONArray all = new JSONArray(in.readLine());
			in .close();
			System.out.println(all.length() + " games");
			for (int i = 0; i < all.length(); i++) {
				JSONObject game = all.getJSONObject(i);
				System.out.print(game.getString("AwayTeamName") + " at " + game.getString("HomeTeamName"));
				System.out.println(" (" + game.getString("DateTime") + ")");
				JSONArray preGameOddsArray = game.getJSONArray("PregameOdds");
				String ou = "O/U: ";
				String spread = "Spd: ";
				double avgHomeSpread = 0;
				for (int j = 0; j < preGameOddsArray.length(); j++) {
					JSONObject gameOdds = preGameOddsArray.getJSONObject(j);
					ou += formatNumber(gameOdds.getString("OverUnder")) + "(" + gameOdds.getString("Sportsbook") + ")"+ " ";
					spread += game.getString("HomeTeamName") + " " + formatSpread(gameOdds.getString("HomePointSpread")) + "(" + gameOdds.getString("Sportsbook") + ")"+ " ";
					avgHomeSpread += Double.parseDouble(gameOdds.getString("HomePointSpread"));
				}
				avgHomeSpread = avgHomeSpread/preGameOddsArray.length();
				System.out.println(ou);
				System.out.println(spread);
				System.out.println("Avg home spread: " + roundToHalf(avgHomeSpread));
				String favorite = avgHomeSpread < 0.0 ? game.getString("HomeTeamName") : game.getString("AwayTeamName");
				String underdog = avgHomeSpread < 0.0 ? game.getString("AwayTeamName") : game.getString("HomeTeamName");
				double pointSpread = avgHomeSpread != 0.0 ? Math.abs(roundToHalf(avgHomeSpread)) : 0.0;
				DAO.createBowlGame("", favorite, underdog, pointSpread, year);
			}
			// TBD Create Champ Game
		 }
		catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}
	
	private HashMap<Integer, String> createBowlGameNameMap(Row row) {	
		HashMap<Integer, String> bowlGameNameMap = new HashMap<Integer, String>();
		Iterator<Cell> cellIter = row.cellIterator();
		int gameIndex = 0;
		while (cellIter.hasNext()){
			Cell cell = (Cell)cellIter.next();
			String bowlGameName = cell.getStringCellValue().trim();
			if (bowlGameName != null && bowlGameName.length() > 0 && 
				!bowlGameName.equalsIgnoreCase("Bowl") && !bowlGameName.equalsIgnoreCase("BCS Champ") && !bowlGameName.equalsIgnoreCase("Championship")) {
					bowlGameNameMap.put(gameIndex, bowlGameName);
				gameIndex++;
			}
        }
		// Manually add Championship game since it appears twice (Winner and Total Points)
		bowlGameNameMap.put(gameIndex, "Championship");
		return bowlGameNameMap;
	}
	
	private String getStringFromCell(Row row, int index) {
		String cellString;
		   
		if (row.getCell(index) == null || row.getCell(index).getCellType() == CellType.BLANK) {
		      return null; 
		}
		   
		if (row.getCell(index).getCellType() == CellType.STRING) {
		    cellString = row.getCell(index).getStringCellValue().trim(); 
		}
		else  {
			String dblValString = Double.toString(row.getCell(index).getNumericCellValue());
		    if (dblValString.indexOf(".") != -1) {
		    	cellString = Double.toString((double)row.getCell(index).getNumericCellValue()); 
		    }
		    else {
		    	cellString = Long.toString((long)row.getCell(index).getNumericCellValue()); 
		    } 
		 }
		 return cellString;
	}
	
	public Double getNumberFromCell(Row row, int index) {
	    Double cellNumber;
	    
	    if (row.getCell(index) == null || row.getCell(index).getCellType() == CellType.BLANK) {
	       return null; 
	   }
	    
	    if (row.getCell(index).getCellType() == CellType.NUMERIC) {
	    cellNumber = row.getCell(index).getNumericCellValue(); 
	    }
	    else  {
	    try {
	       cellNumber = new Double(row.getCell(index).getStringCellValue().trim());
	    }
	    catch (NumberFormatException e) { return new Double(0);}
	   }
	    return cellNumber; 
	}
	
	private static String formatNumber(String oldNum) {
		String numPart1 = "";
		String numPart2 = "";
		String[] numArray = oldNum.split("\\.");
		numPart1 = numArray[0].replaceAll("-", ""); // Remove the -
		if (Integer.parseInt(numArray[1]) >= 4 && Integer.parseInt(numArray[1]) <= 7) {
			numPart2 = ".5";	
		}
		else if (Integer.parseInt(numArray[1]) >= 8){
			numPart1 = Integer.toString(Integer.parseInt(numPart1) + 1);
		}
		if (oldNum.indexOf("-") != -1) { // Put the - back on for favs
			numPart1 = "-" + numPart1;
		}
		return numPart1 + numPart2;
	}
	
	private static String formatSpread(String oldNum) { 
		String formattedNumber = formatNumber(oldNum);
		
		if (formattedNumber.indexOf("-") == -1) { // Add a + for dogs
			formattedNumber = "+" + formattedNumber;
		}
		return formattedNumber;
	}
	
	private double roundToHalf(double d) {
	    return Math.round(d * 2) / 2.0;
	}

	public String getUsersCB() {
		return usersCB;
	}

	public void setUsersCB(String usersCB) {
		this.usersCB = usersCB;
	}

	public String getGamesCB() {
		return gamesCB;
	}

	public void setGamesCB(String gamesCB) {
		this.gamesCB = gamesCB;
	}

	public String getPicksCB() {
		return picksCB;
	}

	public void setPicksCB(String picksCB) {
		this.picksCB = picksCB;
	}
	
	public String getFromWS() {
		return fromWS;
	}

	public void setFromWS(String fromWS) {
		this.fromWS = fromWS;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
	@Override
    public void setSession(Map<String, Object> sessionMap) {
        this.userSession = sessionMap;
    }
}
