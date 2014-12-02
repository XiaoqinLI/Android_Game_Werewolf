package edu.utexas.LI.wherewolf;

public class LoginResponse extends BasicResponse {

	private int playerID = -1;

	public LoginResponse(String status, String errorMessage) {
		super(status, errorMessage);
	}

	public LoginResponse(String status, String errorMessage, int playerID) {
		super(status, errorMessage);

		this.playerID = playerID;
	}


	public int getPlayerID()
	{
		return playerID;
	}

}
