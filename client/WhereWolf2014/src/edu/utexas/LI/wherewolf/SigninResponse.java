package edu.utexas.LI.wherewolf;

public class SigninResponse extends BasicResponse {

	private int playerID = -1;

	public SigninResponse(String status, String errorMessage) {
		super(status, errorMessage);
	}

	public SigninResponse(String status, String errorMessage, int playerID) {
		super(status, errorMessage);

		this.playerID = playerID;
	}


	public int getPlayerID()
	{
		return playerID;
	}

}
