package edu.utexas.LI.wherewolf;

public class ChangedLocationResponse extends BasicResponse{
	
	private final long currentTime;
	// list of nearby players
	// any important game messages (killed, voted off)
	
	public ChangedLocationResponse(String status, String message){
		super (status, message);
		currentTime = 0;
	}
	
	public ChangedLocationResponse(String status, String message, long currentTime){
		super (status, message);
		this.currentTime = currentTime;
	}

	public long getCurrentTime() {
		return currentTime;
	}	
}

