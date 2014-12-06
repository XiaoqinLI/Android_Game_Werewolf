package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GetPlayersRequest extends BasicRequest {
	private static final String TAG = "GetGamesRequest";

	private final long gameId;
	
	public GetPlayersRequest(String username, String password, long gameId) {
		super(username, password);
		this.gameId = gameId;
	}

	@Override
	public String getURL() {
		return "/v1/game/" + Long.toString(gameId) + "/players";
	}

	@Override
	public List<NameValuePair> getParameters() {
	  List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	  urlParameters.add(new BasicNameValuePair("username", this.username));
	  urlParameters.add(new BasicNameValuePair("password", this.password));
	  urlParameters.add(new BasicNameValuePair("game_id", Long.toString(gameId)));
	  return urlParameters;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.POST;
	}

	
	@Override
	public GetPlayersResponse execute(WherewolfNetworking net) {
		try{
			JSONObject response = net.sendRequest(this);
			if (response.getString("status").equals("success"))
			{
				JSONArray players = response.getJSONArray("players");
				
				return new GetPlayersResponse("success", "retrieved players", players);
			} else {
				String errorMessage = response.getString("status");
				return new GetPlayersResponse("failure", errorMessage);
			}
		} catch (JSONException e) {
			return new GetPlayersResponse("failure", "could not retrieve players");
		} catch (WherewolfNetworkException ex)
		{
			return new GetPlayersResponse("failure", "could not communicate with server");
		}
	}

}
