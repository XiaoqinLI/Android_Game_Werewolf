package edu.utexas.LI.wherewolf;

import org.json.JSONArray;

public class GetPlayersResponse extends BasicResponse {
	
	protected JSONArray players;
	
	public GetPlayersResponse(String status, String errorMessage) {
		super(status, errorMessage);
		// TODO Auto-generated constructor stub
	}

	public GetPlayersResponse(String status, String errorMessage, JSONArray players) {
		super(status, errorMessage);
		this.players = players;
	}
	
	public JSONArray getPlayers() {
		return players;
	}
	

}
