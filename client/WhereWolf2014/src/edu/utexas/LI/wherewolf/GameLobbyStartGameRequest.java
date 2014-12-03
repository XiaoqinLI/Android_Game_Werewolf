package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class GameLobbyStartGameRequest extends BasicRequest{

	protected long gameID;
	protected int gameStatus;

	public GameLobbyStartGameRequest(String username, String password, long game_id, int game_status) {
		super(username, password);
		this.gameID = game_id;
		this.gameStatus = game_status;
	}

	@Override
	public String getURL() {
		String url = "/v1/game/"+String.valueOf(this.getGameID())+"/status";
		return url;
	}

	@Override
	public List<NameValuePair> getParameters() {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("game_id", String.valueOf(gameID)));
		urlParameters.add(new BasicNameValuePair("game_status", String.valueOf(gameStatus)));
		urlParameters.add(new BasicNameValuePair("username", username));
		urlParameters.add(new BasicNameValuePair("password", password));
		return urlParameters;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.POST;
	}

	@Override
	public GameLobbyStartGameResponse execute(WherewolfNetworking net) {
		try {
			JSONObject jObject = net.sendRequest(this);

			String status = jObject.getString("status");

			if (status.equals("success"))
			{				
				return new GameLobbyStartGameResponse("success", "game started");
			} else {
				String errorMessage = jObject.getString("status");
				return new GameLobbyStartGameResponse("failure", errorMessage);
			}

		} catch (WherewolfNetworkException ex)
		{
			return new GameLobbyStartGameResponse("failure", "could not communicate with server.");
		} catch (JSONException e) {
			return new GameLobbyStartGameResponse("failure", "could not parse JSON.");
		}
	}

	// Basic getters and setters go here
	public long getGameID() {
		return gameID;
	}

	public void setGameID(long gameID) {
		this.gameID = gameID;
	}

	public int getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(int gameStatus) {
		this.gameStatus = gameStatus;
	}


}
