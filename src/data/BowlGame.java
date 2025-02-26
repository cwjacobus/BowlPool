package data;

import java.sql.Timestamp;

public class BowlGame {
	
	private int gameId;
	private String bowlName;
	private String favorite;
	private String underdog;
	private double spread;
	private int favoriteScore;
	private int underDogScore;
	private boolean completed;
	private int year;
	private Timestamp dateTime;
	private boolean cancelled;
	private int favoriteTeamId;
	private int underdogTeamId;
	
	public BowlGame (int gameId, String bowlName, String favorite, String underdog, double spread, int favoriteScore, int underDogScore, boolean completed, int year, 
			Timestamp dateTime, boolean cancelled, int favoriteTeamId, int underdogTeamId) {
		this.gameId = gameId;
		this.bowlName = bowlName;
		this.favorite = favorite;
		this.underdog = underdog;
		this.spread = spread;
		this.favoriteScore = favoriteScore;
		this.underDogScore = underDogScore;
		this.completed = completed;
		this.year = year;
		this.dateTime = dateTime;
		this.cancelled = cancelled;
		this.favoriteTeamId = favoriteTeamId;
		this.underdogTeamId = underdogTeamId;
	}
	
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public String getBowlName() {
		return bowlName;
	}
	public void setBowlName(String bowlName) {
		this.bowlName = bowlName;
	}
	public String getFavorite() {
		return favorite;
	}
	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}
	public String getUnderdog() {
		return underdog;
	}
	public void setUnderdog(String underdog) {
		this.underdog = underdog;
	}
	public double getSpread() {
		return spread;
	}
	public void setSpread(double spread) {
		this.spread = spread;
	}
	public int getFavoriteScore() {
		return favoriteScore;
	}
	public void setFavoriteScore(int favoriteScore) {
		this.favoriteScore = favoriteScore;
	}
	public int getUnderDogScore() {
		return underDogScore;
	}
	public void setUnderDogScore(int underDogScore) {
		this.underDogScore = underDogScore;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public int getFavoriteTeamId() {
		return favoriteTeamId;
	}

	public void setFavoriteTeamId(int favoriteTeamId) {
		this.favoriteTeamId = favoriteTeamId;
	}

	public int getUnderdogTeamId() {
		return underdogTeamId;
	}

	public void setUnderdogTeamId(int underdogTeamId) {
		this.underdogTeamId = underdogTeamId;
	}
	
	public String toString() {
		return bowlName + " (" + underdog + " v " + favorite + ")";
	}

}
