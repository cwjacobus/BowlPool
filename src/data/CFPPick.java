package data;

import java.sql.Timestamp;

public class CFPPick {
	
	private int cfpPickId;
	private int userId;
	private int cfpGameId;
	private String winner;
	private int totalPoints;
	private int poolId;
	Timestamp createdTime;
	
	public CFPPick () {
	}
	
	public CFPPick (int cfpPickId, int userId, int cfpGameId, String winner, int totalPoints, int poolId, Timestamp createdTime) {
		this.cfpGameId = cfpGameId;
		this.userId = userId;
		this.cfpPickId = cfpPickId;
		this.winner = winner;
		this.totalPoints = totalPoints;
		this.poolId = poolId;
		this.createdTime = createdTime;
	}

	public int getCfpPickId() {
		return cfpPickId;
	}

	public void setCfpPickId(int cfpPickId) {
		this.cfpPickId = cfpPickId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCfpGameId() {
		return cfpGameId;
	}

	public void setCfpGameId(int cfpGameId) {
		this.cfpGameId = cfpGameId;
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

	public int getPoolId() {
		return poolId;
	}

	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

}
