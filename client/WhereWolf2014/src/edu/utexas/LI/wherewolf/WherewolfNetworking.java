package edu.utexas.LI.wherewolf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import edu.utexas.LI.wherewolf.BasicRequest.RequestType;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class WherewolfNetworking {

	private static final String TAG = "NetWorking";
	private static final String fullhost = "http://192.168.1.8:5000";
	private static final String loadBalancer = "wherewolfLB-1277079358.us-west-2.elb.amazonaws.com";	 

	public WherewolfNetworking() {

	}

	public JSONObject sendRequest(BasicRequest basicRequest)
			throws WherewolfNetworkException {

		InputStream inputStream = null;
		String result = null;

		String url = fullhost + basicRequest.getURL();
		RequestType requestType = basicRequest.getRequestType();
		List<NameValuePair> payload = basicRequest.getParameters();
		String username = basicRequest.getUsername();
		String password = basicRequest.getPassword();

		try {

			final DefaultHttpClient httpClient = new DefaultHttpClient();

			// HttpUriRequest request;
			HttpResponse response;

			HttpUriRequest request;

			if (basicRequest.getRequestType() == RequestType.GET) {
				request = new HttpGet(url);     
	            request.setHeader("Content-type", "application/json");
				// add authentication stuff here.
			} else if (requestType == RequestType.POST) {

				HttpPost postRequest = new HttpPost(url);
				postRequest.setHeader("Content-type",
						"application/x-www-form-urlencoded");

				if (payload!=null) {

					postRequest.setEntity(new UrlEncodedFormEntity(payload));
				}

				request = postRequest;


			} else if (requestType == RequestType.DELETE) {

				HttpDelete deleteRequest = new HttpDelete(url);

				request = deleteRequest;
				request.setHeader("Content-type", "application/json");

			} else if (requestType == RequestType.PUT) {

				request = new HttpPut(url);
				request.setHeader("Content-type", "application/json");


			} else {
				throw new WherewolfNetworkException(
						"Does not support the HTTP request type");
			}


			if (!username.equals("")) {
				String authorizationString = "Basic "
						+ Base64.encodeToString(
								(username + ":" + password).getBytes(),
								Base64.NO_WRAP);
				request.setHeader("Authorization", authorizationString);
			}

			response = httpClient.execute(request);

			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

			result = sb.toString();

			try {
				JSONObject json = new JSONObject(result);
				return json;

			} catch (JSONException ex) {
				throw new WherewolfNetworkException("Could not parse JSON");
			}

		} catch (Exception e) {
			Log.e(TAG, "Problem with response from server" + e.toString());

		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception ex) {
				throw new WherewolfNetworkException("Network problem");
			}
		}

		throw new WherewolfNetworkException("Network problem");

	}
}
