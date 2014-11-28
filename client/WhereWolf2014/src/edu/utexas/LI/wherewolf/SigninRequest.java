package edu.utexas.LI.wherewolf;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import edu.utexas.LI.wherewolf.WherewolfNetworking;

public class SigninRequest extends BasicRequest {

	public SigninRequest (String username, String password)
	{
		super(username, password);
	}

	/**
	 * Put the URL to your API endpoint here
	 */
	@Override
	public String getURL() {
		return "/v1/checkpassword";
	}

	@Override
	public List<NameValuePair> getParameters() {
		return null;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.GET;
	}

	@Override
	public SigninResponse execute(WherewolfNetworking net) {

		try {
			JSONObject response = net.sendRequest(this);

			if (response.getString("status").equals("success"))
			{
				// int playerID = response.getInt("playerid");
				return new SigninResponse("success", "signed in successfully");
			} else {

				String errorMessage = response.getString("error");
				return new SigninResponse("failure", errorMessage);
			}
		} catch (JSONException e) {
			return new SigninResponse("failure", "sign in not working");
		} catch (WherewolfNetworkException ex)
		{
			return new SigninResponse("failure", "could not communicate with the server");
		}
	}

}
