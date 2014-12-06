package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


public class RegisterRequest extends BasicRequest{
	
	protected String firstname;
	protected String lastname;

	public RegisterRequest(String username, String password, String firstname, String lastname) {
		super(username, password);
		// TODO Auto-generated constructor stub
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	/**
	 * Put the URL to your API endpoint here
	 */
	@Override
	public String getURL() {
		return "/v1/register";
	}
	
	@Override
	public List<NameValuePair> getParameters() {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", username));
		urlParameters.add(new BasicNameValuePair("password", password));
		urlParameters.add(new BasicNameValuePair("firstname", firstname));
		urlParameters.add(new BasicNameValuePair("lastname", lastname));
		return urlParameters;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.POST;
	}

	@Override
	public RegisterResponse execute(WherewolfNetworking net) {
		// TODO Auto-generated method stub
		try {
			JSONObject jObject = net.sendRequest(this);

			String status = jObject.getString("status");

			if (status.equals("success"))
			{
				return new RegisterResponse("success", "Registered");
			} else {
				String errorMessage = jObject.getString("status");
				return new RegisterResponse("failure", errorMessage);
			}

		} catch (WherewolfNetworkException ex)
		{
			return new RegisterResponse("failure", "could not communicate with server.");
		} catch (JSONException e) {
			return new RegisterResponse("failure", "could not parse JSON.");
		}
	}
	
	// Basic getters and setters go here
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	
}
