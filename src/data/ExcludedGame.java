package data;

public class ExcludedGame {
	
	private int excludedGameId;
	private int poolId;
	private int bowlGameId;
	private int cfpGameId;
	
	public ExcludedGame () {
	}
	
	public ExcludedGame (int excludedGameId, int poolId, int bowlGameId, int cfpGameId) {
		this.excludedGameId = excludedGameId;
		this.poolId = poolId;
		this.bowlGameId = bowlGameId;
		this.cfpGameId = cfpGameId;
	}

	public int getExcludedGameId() {
		return excludedGameId;
	}

	public void setExcludedGameId(int excludedGameId) {
		this.excludedGameId = excludedGameId;
	}

	public int getPoolId() {
		return poolId;
	}

	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}

	public int getBowlGameId() {
		return bowlGameId;
	}

	public void setBowlGameId(int bowlGameId) {
		this.bowlGameId = bowlGameId;
	}

	public int getCfpGameId() {
		return cfpGameId;
	}

	public void setCfpGameId(int cfpGameId) {
		this.cfpGameId = cfpGameId;
	}

}
