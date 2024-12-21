package data;

import java.sql.Timestamp;

public class CFPPick {
	
	private int cFPPickId;
	private int userId;
	private int cFPGameId;
	private String winner;
	private int totalPoints;
	private int poolId;
	Timestamp createdTime;
	
	public CFPPick () {
	}
	
	public CFPPick (int cFPPickId, int userId, int cFPGameId, String winner, int totalPoints, int poolId, Timestamp createdTime) {
		this.cFPGameId = cFPGameId;
		this.userId = userId;
		this.cFPPickId = cFPPickId;
		this.winner = winner;
		this.totalPoints = totalPoints;
		this.poolId = poolId;
		this.createdTime = createdTime;
	}

	public int getcFPPickId() {
		return cFPPickId;
	}

	public void setcFPPickId(int cFPPickId) {
		this.cFPPickId = cFPPickId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getcFPGameId() {
		return cFPGameId;
	}

	public void setcFPGameId(int cFPGameId) {
		this.cFPGameId = cFPGameId;
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
