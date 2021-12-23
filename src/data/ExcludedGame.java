package data;

public class ExcludedGame {
	
	private int excludedGameId;
	private int poolId;
	private int gameId;
	
	public ExcludedGame () {
	}
	
	public ExcludedGame (int excludedGameId, int poolId, int gameId) {
		this.excludedGameId = excludedGameId;
		this.poolId = poolId;
		this.gameId = gameId;
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

}
