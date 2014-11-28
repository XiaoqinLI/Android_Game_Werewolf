package edu.utexas.LI.wherewolf;

public final class CreateGameResponse extends BasicResponse {

	protected int gameID;

	public CreateGameResponse (String status, String message)
	{
		super(status, message);
	}

	public CreateGameResponse (String status, String message, int gameID)
	{
		super(status, message);  
		this.gameID = gameID;
	}

	public int getGameID() {
		return gameID;
	}    


}
