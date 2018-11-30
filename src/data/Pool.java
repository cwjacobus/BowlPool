package data;

public class Pool {
	
	private int poolId;
	private String poolName;
	private int year;
	private boolean useSpreads;
	
	public Pool () {
	}
	
	public Pool (int poolId, String poolName, int year, boolean useSpreads) {
		this.poolId = poolId;
		this.poolName = poolName;
		this.year = year;
		this.useSpreads = useSpreads;
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

	public boolean isUseSpreads() {
		return useSpreads;
	}

	public void setUseSpreads(boolean useSpreads) {
		this.useSpreads = useSpreads;
	}
	

}
