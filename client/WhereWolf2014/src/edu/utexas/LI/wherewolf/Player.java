package edu.utexas.LI.wherewolf;
import android.graphics.drawable.Drawable;
public class Player {
	private int PlayerId;
	private String playerName;
	private String profilePicUrl;
	private int numVotes;
	private Drawable profPic;
	
	public Player(int playerId, Drawable profPic, String profilePicUrl, String name, int numVotes) {
		this.PlayerId = playerId;
		this.profilePicUrl = profilePicUrl;
		this.playerName = name;
		this.numVotes = numVotes;
		this.profPic = profPic;
	}

	public Drawable getProfPic() {
		return profPic;
	}

	public void setProfPic(Drawable profPic) {
		this.profPic = profPic;
	}

	public int getPlayerId() {
		return this.PlayerId;
	}

	public void setPlayerId(int playerId) {
		this.PlayerId = playerId;
	}

	public String getName() {
		return this.playerName;
	}

	public void setName(String name) {
		this.playerName = name;
	}

	public String getProfilePicUrl() {
		return this.profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	public int getNumVotes() {
		return this.numVotes;
	}

	public void setNumVotes(int numVotes) {
		this.numVotes = numVotes;
	}	
}
