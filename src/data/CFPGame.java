package data;

import java.sql.Timestamp;

public class CFPGame {
	
	private int cfpGameId;
	private String description;
	private int round;
	private int gameIndex;
	private int pointsValue;
	private boolean completed;
	private String home;
	private String visitor;
	private int homeScore;
	private int visScore;
	private int homeSeed;
	private int visSeed;
	private Timestamp dateTime;
	private int year;
	
	public CFPGame (int cfpGameId, String description, int round, int gameIndex, int pointsValue, boolean completed, String home, String visitor, int homeScore, int visScore, 
			int homeSeed, int visSeed, Timestamp dateTime, int year) {
		this.cfpGameId = cfpGameId;
		this.description = description;
		this.round = round;
		this.gameIndex = gameIndex;
		this.pointsValue = pointsValue;
		this.home = home;
		this.visitor = visitor;
		this.homeScore = homeScore;
		this.visScore = visScore;
		this.completed = completed;
		this.year = year;
		this.dateTime = dateTime;
		this.homeSeed = homeSeed;
		this.visSeed = visSeed;
	}

	public int getCfpGameId() {
		return cfpGameId;
	}

	public void setCfpGameId(int cfpGameId) {
		this.cfpGameId = cfpGameId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getGameIndex() {
		return gameIndex;
	}

	public void setGameIndex(int gameIndex) {
		this.gameIndex = gameIndex;
	}

	public int getPointsValue() {
		return pointsValue;
	}

	public void setPointsValue(int pointsValue) {
		this.pointsValue = pointsValue;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getVisitor() {
		return visitor;
	}

	public void setVisitor(String visitor) {
		this.visitor = visitor;
	}

	public int getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}

	public int getVisScore() {
		return visScore;
	}

	public void setVisScore(int visScore) {
		this.visScore = visScore;
	}

	public int getHomeSeed() {
		return homeSeed;
	}

	public void setHomeSeed(int homeSeed) {
		this.homeSeed = homeSeed;
	}

	public int getVisSeed() {
		return visSeed;
	}

	public void setVisSeed(int visSeed) {
		this.visSeed = visSeed;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	public String toString() {
		return visitor + "(" + visSeed + ")" + " v " + home + "(" + homeSeed + ")";
	}

}
