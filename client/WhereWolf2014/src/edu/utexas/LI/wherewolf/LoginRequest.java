package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import edu.utexas.LI.wherewolf.WherewolfNetworking;

public class LoginRequest extends BasicRequest {

	public LoginRequest (String username, String password)
	{
		super(username, password);
	}

	@Override
	public String getURL() {
		return "/v1/checkpassword";
	}

	@Override
	public List<NameValuePair> getParameters() {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", username));
		urlParameters.add(new BasicNameValuePair("password", password));
		return urlParameters;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.POST;
	}

	@Override
	public LoginResponse execute(WherewolfNetworking net) {

		try {
			JSONObject response = net.sendRequest(this);

			if (response.getString("status").equals("success"))
			{
				// int playerID = response.getInt("playerid");
				return new LoginResponse("success", "signed in successfully");
			} else {
				String errorMessage = response.getString("status");
				return new LoginResponse("failure", errorMessage);
			}
		} catch (JSONException e) {
			return new LoginResponse("failure", "sign in not working");
		} catch (WherewolfNetworkException ex)
		{
			return new LoginResponse("failure", "could not communicate with the server");
		}
	}

}
