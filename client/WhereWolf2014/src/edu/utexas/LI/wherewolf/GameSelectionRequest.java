package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class GameSelectionRequest extends BasicRequest {
	protected long gameID;
	
	public GameSelectionRequest(String username, String password, long gameId) {
		super(username, password);
		// TODO Auto-generated constructor stub
		this.gameID = gameId;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		String url = "/v1/game/" + Long.toString(gameID) + "/lobby";
		return url;
	}

	@Override
	public List<NameValuePair> getParameters() {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", username));
		urlParameters.add(new BasicNameValuePair("game_id", Long.toString(gameID)));
		urlParameters.add(new BasicNameValuePair("password", password));
		return urlParameters;
	}

	@Override
	public RequestType getRequestType() {
		// TODO Auto-generated method stub
		return RequestType.POST;
	}

	@Override
	public GameSelectionResponse execute(WherewolfNetworking net) {
		// TODO Auto-generated method stub
		try{
			JSONObject response = net.sendRequest(this);
			if (response.getString("status").equals("success"))
			{
				return new GameSelectionResponse("success", "joined game successfully");
			} else {
				String errorMessage = response.getString("status");
				return new GameSelectionResponse("failure", errorMessage);
			}
		} catch (JSONException e){
			return new GameSelectionResponse ("failure", "could not join game"); 
		} catch (WherewolfNetworkException ex){
			return new GameSelectionResponse ("faillure", "could not communicate with the server");
		}
	}

	public long getGameId() {
		return gameID;
	}

	public void setGameId(int gameId) {
		this.gameID = gameId;
	}
}
