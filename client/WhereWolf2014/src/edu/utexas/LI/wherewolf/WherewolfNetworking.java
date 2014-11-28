package edu.utexas.LI.wherewolf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import edu.utexas.LI.wherewolf.BasicRequest.RequestType;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class WherewolfNetworking {

	private static final String TAG = "NetWorking";
	private static final String fullhost = "http://192.168.1.11:5000/v1/";
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

	/*
public class WherewolfNetworking {
	private static final String fullhost = "http://192.168.1.11:5000/v1/";
	private static final String TAG = "Wherewolf Networking";

	public WherewolfNetworking() {

	}

	enum RequestType {
		GET, PUT, POST, DELETE
	}

	public boolean checkPassword(String username, String password)
	{

		// similiar to how the Python requests library handles web service requests
		// url, request type, username, password, then payload

		String result = getJsonFromServer(fullhost + "checkpassword",
				RequestType.GET, username,
				password, null);

		if (result == null)
		{
			Log.e(TAG, "Got a strange response from server");
			return false;
		}

		try {
			Log.i(TAG, "Checking password returned " + result);
			JSONObject jObject = new JSONObject(result);

			String status = jObject.getString("status");

			if (status.equals("success"))
			{
				return true;
			}

		} catch (JSONException ex)
		{
			return false;
		}

		return false;
	}

	public void createGame(CreateGameRequest request) {

        try {

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("game_name", request
                    .getGamename()));
            urlParameters.add(new BasicNameValuePair("description", request
                    .getDescription()));

            String result = getJsonFromServer(fullhost + "game",
                    RequestType.POST, request.getUsername(),
                    request.getPassword(), urlParameters);

            Log.i(TAG, "Result was " + result);

            if (result == null) {
                return;
            }

            JSONObject jObject = new JSONObject(result);

            String status = jObject.getString("status");

            Log.i(TAG, "Status was " + status);

        } catch (JSONException ex) {
            Log.e(TAG, "Problem parsing");
        }

    }

	public String getJsonFromServer(String url, RequestType requestType,
            String username, String password, List<NameValuePair> payload) {

        InputStream inputStream = null;
        String result = null;

        try {

            final DefaultHttpClient httpClient = new DefaultHttpClient();

            // HttpUriRequest request;
            HttpResponse response;

            if (requestType == RequestType.GET) {
                HttpGet getRequest = new HttpGet(url);
                getRequest.setHeader("Content-type", "application/json");

                // add authentication stuff here.
                if (!username.equals("")) {
                    String authorizationString = "Basic "
                            + Base64.encodeToString(
                                    (username + ":" + password).getBytes(),
                                    Base64.NO_WRAP);
                    getRequest.setHeader("Authorization", authorizationString);
                }

                response = httpClient.execute(getRequest);

            } else if (requestType == RequestType.POST) {

                HttpPost postRequest = new HttpPost(url);
                postRequest.setHeader("Content-type",
                        "application/x-www-form-urlencoded");

                if (!payload.equals("")) {

                    postRequest.setEntity(new UrlEncodedFormEntity(payload));
                }

                // add authentication stuff here.
                if (!username.equals("")) {
                    String authorizationString = "Basic "
                            + Base64.encodeToString(
                                    (username + ":" + password).getBytes(),
                                    Base64.NO_WRAP);
                    postRequest.setHeader("Authorization", authorizationString);
                }

                response = httpClient.execute(postRequest);

            } else {
                return null;
            }

            // execute the request here.
            // HttpResponse response = httpClient.execute(request);
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
        } catch (Exception e) {
            Log.e(TAG, "Problem with response from server" + e.toString());

        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (Exception ex) {
            }
        }

        return result;
    }


}
	 */
