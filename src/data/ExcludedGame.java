package data;

public class ExcludedGame {
	
	private int excludedGameId;
	private int poolId;
	private int gameId;
	private int cfpGameId;
	private boolean cfpGame;
	
	public ExcludedGame () {
	}
	
	public ExcludedGame (int excludedGameId, int poolId, int gameId, int cfpGameId, boolean cfpGame) {
		this.excludedGameId = excludedGameId;
		this.poolId = poolId;
		this.gameId = gameId;
		this.cfpGameId = cfpGameId;
		this.cfpGame = cfpGame;
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

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getCfpGameId() {
		return cfpGameId;
	}

	public void setCfpGameId(int cfpGameId) {
		this.cfpGameId = cfpGameId;
	}

	public boolean isCfpGame() {
		return cfpGame;
	}

	public void setCfpGame(boolean cfpGame) {
		this.cfpGame = cfpGame;
	}

}
