package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateGameRequest extends BasicRequest{
	
	protected String gamename;
	protected String description;
	
	public CreateGameRequest( String username, String password, String gameName, String description) {       
		super(username, password);
		this.gamename = gameName;
		this.description = description;       
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return "/v1/game";
	}

	@Override
	public RequestType getRequestType() {
		// TODO Auto-generated method stub
		return RequestType.POST;
	}

	@Override
	public List<NameValuePair> getParameters() {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("game_name", gamename));
		urlParameters.add(new BasicNameValuePair("description", description));
		urlParameters.add(new BasicNameValuePair("username", username));
		urlParameters.add(new BasicNameValuePair("password", password));
		return urlParameters;
	}

	public CreateGameResponse processResponse(JSONObject jObject) throws JSONException
	{

		JSONObject jResults = jObject.getJSONObject("results");
		long gameID = (long) jResults.getInt("game_id");

		return new CreateGameResponse("success",
				"Successfully created the game",
				gameID);
	}

	@Override
	public CreateGameResponse execute(WherewolfNetworking net) {

		try {
			JSONObject jObject = net.sendRequest(this);
			
			String status = jObject.getString("status");

			if (status.equals("success"))
			{
				CreateGameResponse successResponse;
				successResponse = this.processResponse(jObject);
				return successResponse;
//				return new CreateGameResponse("success", "created game");
			} else {
				String errorMessage = jObject.getString("status");
				return new CreateGameResponse("failure", errorMessage);
			}

		} catch (WherewolfNetworkException ex)
		{
			return new CreateGameResponse("failure", "could not communicate with server.");
		} catch (JSONException e) {
			return new CreateGameResponse("failure", "could not parse JSON.");
		}
	}

	// Basic getters and setters go here
	public String getGamename() {
		return gamename;
	}
	public void setGamename(String gamename) {
		this.gamename = gamename;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
