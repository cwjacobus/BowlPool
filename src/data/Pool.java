package data;

public class Pool {
	
	private int poolId;
	private String poolName;
	private int year;
	private boolean usePointSpreads;
	
	public Pool () {
	}
	
	public Pool (int poolId, String poolName, int year, boolean usePointSpreads) {
		this.poolId = poolId;
		this.poolName = poolName;
		this.year = year;
		this.usePointSpreads = usePointSpreads;
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
	

}
