package data;

public class User {
	
	private int userId;
	private String userName;
	private String lastName;
	private String firstName;
	private String email;
	private Integer year;
	private boolean admin;
	
	public User (int userId, String userName, String lastName, String firstName, String email, int year, boolean admin) {
		this.userId = userId;
		this.userName = userName;
		this.lastName = lastName;
		this.firstName = firstName;
		this.email = email;
		this.year = year;
		this.admin = admin;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	
}
