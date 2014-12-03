package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class LeaveGameRequest extends BasicRequest {
	final String TAG = "LeaveGameRequest";
	
	private Game game;
	
	public LeaveGameRequest(String username, String password, Game game){
		super (username, password);
		this.game = game;
	}

	public Game getGame() {
		return game;
	}
	
	@Override
	public String getURL() {
		return "/v1/gamedel/" + String.valueOf(game.getGameId());
	}
	  
	@Override
	public RequestType getRequestType()
	{
		return RequestType.POST;
	}

	@Override
	public List<NameValuePair> getParameters() {
	List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	urlParameters.add(new BasicNameValuePair("username", this.username));
	urlParameters.add(new BasicNameValuePair("password", this.password));
	urlParameters.add(new BasicNameValuePair("game_id", Integer.toString(this.game.getGameId())));
	return urlParameters;
	}

	public LeaveGameResponse processResponse(JSONObject jObject) throws JSONException
	{
		JSONObject jResults = jObject.getJSONObject("results");
		int gameID = jResults.getInt("game_id");
		
		Log.v(TAG, "THIS WORKED" + Integer.toString(gameID));

		return new LeaveGameResponse("success",
				"Successfully deleted game",
				gameID);
	}

	@Override
	public LeaveGameResponse execute(WherewolfNetworking net) {
	      
		try {
			JSONObject response = net.sendRequest(this);
	          
			String status = response.getString("status");
				          
			if (status.equals("success"))
			{
				Log.v(TAG, response.getString("status"));
				return new LeaveGameResponse("success", "left game", game);
			} else {
				String errorMessage = response.getString("status");
				Log.v(TAG, errorMessage);
				return new LeaveGameResponse("failure", errorMessage);
			}
	          
		} 
		
		catch (WherewolfNetworkException ex) {
			return new LeaveGameResponse("failure", "could not communicate with server.");
		}

		catch (JSONException e) {
			return new LeaveGameResponse("failure", "could not parse JSON.");
		}

	      
	}

}
