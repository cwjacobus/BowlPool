package data;

public class Pick {
	
	private int pickId;
	private int userId;
	private int gameId;
	private boolean favorite;
	int poolId;
	
	public Pick () {
	}
	
	public Pick (int pickId, int userId, int gameId, boolean favorite, int poolId) {
		this.gameId = gameId;
		this.userId = userId;
		this.pickId = pickId;
		this.favorite = favorite;
		this.poolId = poolId;
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
	
	public boolean getFavorite() {
		return favorite;
	}
	
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public int getPoolId() {
		return poolId;
	}

	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}

}
