package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GetGamesRequest extends BasicRequest {
	private static final String TAG = "GetGamesRequest";

	public GetGamesRequest(String username, String password) {
		super(username, password);
	}

	@Override
	public String getURL() {
		return "/v1/games";
	}

	@Override
	public List<NameValuePair> getParameters() {
	  List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	  urlParameters.add(new BasicNameValuePair("username", this.username));
	  urlParameters.add(new BasicNameValuePair("password", this.password));
	  return urlParameters;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.GET;
	}

	
	@Override
	public GetGamesResponse execute(WherewolfNetworking net) {
		try{
			JSONObject response = net.sendRequest(this);
			if (response.getString("status").equals("success"))
			{
				JSONArray games = response.getJSONArray("games");
				
				//Log.v(TAG, games.toString(1));
				
				return new GetGamesResponse("success", "retrieved games", games);
			} else {
				String errorMessage = response.getString("status");
				return new GetGamesResponse("failure", errorMessage);
			}
		} catch (JSONException e) {
			return new GetGamesResponse("failure", "could not retrieve games");
		} catch (WherewolfNetworkException ex)
		{
			return new GetGamesResponse("failure", "could not communicate with server");
		}
	}

}
