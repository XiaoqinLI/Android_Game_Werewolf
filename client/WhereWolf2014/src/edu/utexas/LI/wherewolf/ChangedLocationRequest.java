package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ChangedLocationRequest extends BasicRequest {
	final String TAG = "CreateGameRequest";
	
	private final long gameId;
	private final double lat;
	private final double lng;
	
	public ChangedLocationRequest(String username, String password, long gameId, double lat, double lng){
		super (username, password);
		this.gameId = gameId;
		this.lat = lat;
		this.lng = lng;
	}

	@Override
	public String getURL() {
		return "/v1/game/" + this.gameId + "/locationInfo";
	}
	  
	public long getGameId() {
		return gameId;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	@Override
	public RequestType getRequestType()
	{
		return RequestType.POST;
	}

	@Override
	public List<NameValuePair> getParameters() {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", username));
		urlParameters.add(new BasicNameValuePair("password", password));
		urlParameters.add(new BasicNameValuePair("lat", Double.toString(this.lat)));
		urlParameters.add(new BasicNameValuePair("lng", Double.toString(this.lng)));
		return urlParameters;
	}


	@Override
	public ChangedLocationResponse execute(WherewolfNetworking net) {
		try {
		JSONObject jsonObject = net.sendRequest(this);
		
		
		if (jsonObject.getString("status").equals("success"))
		{
			String currentTime = jsonObject.getString("current_time");
//			Long lCurTime = Long.parseLong(currentTime);
			Long lCurTime = Double.valueOf(currentTime).longValue();
			return new ChangedLocationResponse("success", "successfully updated position", lCurTime);
		}
		
		return new ChangedLocationResponse("failure", jsonObject.getString("info"));
		} catch (JSONException ex)
		{
			return new ChangedLocationResponse("failure", "could not parse JSON");
		} catch (WherewolfNetworkException ex)
		{
			return new ChangedLocationResponse("failure", "could not communicate with server");
		}
		
	}

}