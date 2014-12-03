package edu.utexas.LI.wherewolf;

import org.json.JSONArray;

public class GetGamesResponse extends BasicResponse {
	
	protected JSONArray games;
	
	public GetGamesResponse(String status, String errorMessage) {
		super(status, errorMessage);
		// TODO Auto-generated constructor stub
	}

	public GetGamesResponse(String status, String errorMessage, JSONArray games) {
		super(status, errorMessage);
		this.games = games;
	}
	
	public JSONArray getGames() {
		return games;
	}
	

}
