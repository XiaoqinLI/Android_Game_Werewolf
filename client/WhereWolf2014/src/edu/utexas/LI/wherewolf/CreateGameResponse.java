package edu.utexas.LI.wherewolf;

public final class CreateGameResponse extends BasicResponse {

	protected long gameID;

	public CreateGameResponse (String status, String errorMessage)
	{
		super(status, errorMessage);
	}

	public CreateGameResponse (String status, String message, long gameID)
	{
		super(status, message);  
		this.gameID = gameID;
	}

	public long getGameID() {
		return gameID;
	}    


}
