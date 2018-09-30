package data;

public class Standings {
	
	private int rank;
	private String userName;
	private int correct;
	private int eliminatedBy;
	
	public Standings () {
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getCorrect() {
		return correct;
	}

	public void setCorrect(int correct) {
		this.correct = correct;
	}

	public int getEliminatedBy() {
		return eliminatedBy;
	}

	public void setEliminatedBy(int eliminatedBy) {
		this.eliminatedBy = eliminatedBy;
	}
	

}
