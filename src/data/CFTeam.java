package data;

public class CFTeam {
	
	private int cfTeamId;
	private String school;
	private String mascot;
	private String conference;
	private String shortName;
	
	public CFTeam () {
	}

	public CFTeam(int cfTeamId, String school, String mascot, String conference, String shortName) {
		this.cfTeamId = cfTeamId;
		this.school = school;
		this.mascot = mascot;
		this.conference = conference;
		this.shortName = shortName;
	}

	public int getCfTeamId() {
		return cfTeamId;
	}

	public void setCfTeamId(int cfTeamId) {
		this.cfTeamId = cfTeamId;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getMascot() {
		return mascot;
	}

	public void setMascot(String mascot) {
		this.mascot = mascot;
	}

	public String getConference() {
		return conference;
	}

	public void setConference(String conference) {
		this.conference = conference;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
