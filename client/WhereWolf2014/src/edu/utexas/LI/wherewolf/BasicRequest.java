package edu.utexas.LI.wherewolf;

import java.util.List;

import org.apache.http.NameValuePair;

public abstract class BasicRequest {

	public enum RequestType {
		GET, PUT, POST, DELETE
	}

	public static final String SUCCESS = "success";
	public static final String FAILURE = "failure";
	
	protected static String username;
	protected static String password;
	

	public BasicRequest (String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public abstract String getURL();

	public abstract List<NameValuePair> getParameters();

	public abstract RequestType getRequestType();

	public abstract BasicResponse execute(WherewolfNetworking net);

}
