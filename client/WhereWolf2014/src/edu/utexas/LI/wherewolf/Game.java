package edu.utexas.LI.wherewolf;

public class Game {
	private int gameId;
	private String gameName;
	private String adminName;
	private String description;
	
	public Game(int gameId, String gameName, String adminName, String description) {
		this.gameId = gameId;
		this.gameName = gameName;
		this.adminName = adminName;
		this.description = description;
	}

	public int getGameId() {
		return this.gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getGameName() {
		return this.gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getAdminName() {
		return this.adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

}
