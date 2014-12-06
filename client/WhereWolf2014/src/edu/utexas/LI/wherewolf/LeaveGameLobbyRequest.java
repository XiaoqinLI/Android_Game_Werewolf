package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


public class LeaveGameLobbyRequest extends BasicRequest{
	protected long gameID;
	public LeaveGameLobbyRequest(String username, String password, long gameId) {
		super(username, password);
		this.gameID = gameId;
	}

	@Override
	public String getURL() {
		return "/v1/gamequit/"+Long.toString(gameID);
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
		return RequestType.POST;
	}

	@Override
	public LeaveGameLobbyResponse execute(WherewolfNetworking net) {
		try{
			JSONObject response = net.sendRequest(this);
			if (response.getString("status").equals("success"))
			{
				return new LeaveGameLobbyResponse("success", "quit game successfully");
			} else {
				String errorMessage = response.getString("status");
				return new LeaveGameLobbyResponse("failure", errorMessage);
			}
		} catch (JSONException e){
			return new LeaveGameLobbyResponse ("failure", "could not join game"); 
		} catch (WherewolfNetworkException ex){
			return new LeaveGameLobbyResponse ("faillure", "could not communicate with the server");
		}	
	}

}
