package data;

public class Pool {
	
	private int poolId;
	private String poolName;
	private int year;
	private boolean usePointSpreads;
	private int ptsBowlGame;
	private int ptsRound1CFPGame;
	private int ptsQtrCFPGame;
	private int ptsSemiCFPGame;
	private int ptsChampCFPGame;
	
	public Pool () {
	}
	
	public Pool (int poolId, String poolName, int year, boolean usePointSpreads, int ptsBowlGame, int ptsRound1CFPGame, int ptsQtrCFPGame, int ptsSemiCFPGame, 
			int ptsChampCFPGame) {
		this.poolId = poolId;
		this.poolName = poolName;
		this.year = year;
		this.usePointSpreads = usePointSpreads;
		this.ptsBowlGame = ptsBowlGame;
		this.ptsRound1CFPGame = ptsRound1CFPGame;
		this.ptsQtrCFPGame = ptsQtrCFPGame;
		this.ptsSemiCFPGame = ptsSemiCFPGame;
		this.ptsChampCFPGame = ptsChampCFPGame;
	}

	public int getPoolId() {
		return poolId;
	}

	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public boolean isUsePointSpreads() {
		return usePointSpreads;
	}

	public void setUsePointSpreads(boolean usePointSpreads) {
		this.usePointSpreads = usePointSpreads;
	}

	public int getPtsBowlGame() {
		return ptsBowlGame;
	}

	public void setPtsBowlGame(int ptsBowlGame) {
		this.ptsBowlGame = ptsBowlGame;
	}

	public int getPtsRound1CFPGame() {
		return ptsRound1CFPGame;
	}

	public void setPtsRound1CFPGame(int ptsRound1CFPGame) {
		this.ptsRound1CFPGame = ptsRound1CFPGame;
	}

	public int getPtsQtrCFPGame() {
		return ptsQtrCFPGame;
	}

	public void setPtsQtrCFPGame(int ptsQtrCFPGame) {
		this.ptsQtrCFPGame = ptsQtrCFPGame;
	}

	public int getPtsSemiCFPGame() {
		return ptsSemiCFPGame;
	}

	public void setPtsSemiCFPGame(int ptsSemiCFPGame) {
		this.ptsSemiCFPGame = ptsSemiCFPGame;
	}

	public int getPtsChampCFPGame() {
		return ptsChampCFPGame;
	}

	public void setPtsChampCFPGame(int ptsChampCFPGame) {
		this.ptsChampCFPGame = ptsChampCFPGame;
	}
	

}
