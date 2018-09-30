package data;

public class ChampPick {
	
	private int pickId;
	private int userId;
	private int gameId;
	private String winner;
	private int totalPoints;
	
	public ChampPick () {
	}
	
	public ChampPick (int pickId, int userId, int gameId, String winner, int totalPoints) {
		this.gameId = gameId;
		this.userId = userId;
		this.pickId = pickId;
		this.winner = winner;
		this.totalPoints = totalPoints;
	}
	
	public int getPickId() {
		return pickId;
	}

	public void setPickId(int pickId) {
		this.pickId = pickId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGameId() {
		return gameId;
	}
	
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}

}
