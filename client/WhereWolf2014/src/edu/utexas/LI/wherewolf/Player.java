package edu.utexas.LI.wherewolf;

public class Player {
	private int PlayerId;
	private String name;
	private String profilePicUrl;
	private int numVotes;
	
	private Player(int playerId, String name, String profilePicUrl, int numVotes) {
		this.PlayerId = playerId;
		this.name = name;
		this.profilePicUrl = profilePicUrl;
		this.numVotes = numVotes;
	}

	public int getPlayerId() {
		return PlayerId;
	}

	public void setPlayerId(int playerId) {
		PlayerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	public int getNumVotes() {
		return numVotes;
	}

	public void setNumVotes(int numVotes) {
		this.numVotes = numVotes;
	}	
}
