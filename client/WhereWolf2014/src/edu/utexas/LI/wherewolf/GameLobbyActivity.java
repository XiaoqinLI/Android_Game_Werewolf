package edu.utexas.LI.wherewolf;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GameLobbyActivity extends ListActivity {
	private static final String TAG = "GameLobbyActivity";
	private static ArrayList<Player> arrayOfPlayers = new ArrayList<Player>();
	private long currentGameID;
	PlayerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_lobby);
		
        currentGameID = getIntent().getLongExtra("selectedGameID", -100);
		// Create the adapter to convert the array to views
        new GetPlayersTask().execute();
        adapter = new PlayerAdapter(this, arrayOfPlayers);
        final ListView playerListView = getListView();
		playerListView.setAdapter(adapter);


		
		playerListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "Hit on a player, doing nothing");
				// do nothing
			}
		});
		
		//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		final Button startGameButton = (Button) findViewById(R.id.start_game_button);		
		startGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "User pressed the start game button");			
				WherewolfPreferences myPrefs = new WherewolfPreferences(GameLobbyActivity.this);
				String storedUsername = myPrefs.getUsername();
		        String storedPassword = myPrefs.getPassword();
		        currentGameID = getIntent().getLongExtra("selectedGameID", -100);
				GameLobbyStartGameRequest request = new GameLobbyStartGameRequest(storedUsername, storedPassword, currentGameID, 1);
				StartGameTask startGameTask = new StartGameTask();
				startGameTask.execute(request);
			}
		});	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK ) {
	    	WherewolfPreferences myPrefs = new WherewolfPreferences(GameLobbyActivity.this);
			String storedUsername = myPrefs.getUsername();
	        String storedPassword = myPrefs.getPassword();
	        currentGameID = getIntent().getLongExtra("selectedGameID", -100);
	    	LeaveGameLobbyRequest request = new LeaveGameLobbyRequest(storedUsername, storedPassword,currentGameID);
	    	LeaveGameLobbyTask leaveGameLobbyTask = new LeaveGameLobbyTask();
	    	leaveGameLobbyTask.execute(request);
	    	
	    }
	    return super.onKeyDown(keyCode, event);
	}	

	@Override
	protected void onStart() {
		Log.i(TAG, "started the game lobby activity");
		super.onStart();
		adapter.clear();
		new GetPlayersTask().execute();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "restarted the game lobby activity");
		super.onRestart();
		adapter.clear();
		new GetPlayersTask().execute();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "resumed the game lobby activity");
		super.onResume();
		adapter.clear();
		new GetPlayersTask().execute();
		
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "pause the game lobby activity");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "stopped the game lobby activity");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "destroyed the game lobby activity");
		super.onDestroy();
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private class StartGameTask extends AsyncTask<GameLobbyStartGameRequest, Integer, GameLobbyStartGameResponse> {
		
	    @Override
	    protected GameLobbyStartGameResponse doInBackground(GameLobbyStartGameRequest... request) {
	
	    	WherewolfPreferences myPrefs = new WherewolfPreferences(GameLobbyActivity.this);
			String storedUsername = myPrefs.getUsername();
	        String storedPassword = myPrefs.getPassword();
	        currentGameID = getIntent().getLongExtra("selectedGameID", -100);
	        
	        GameLobbyStartGameRequest startGameRequest = new GameLobbyStartGameRequest(storedUsername, storedPassword, currentGameID, 1);
	        
	        return startGameRequest.execute(new WherewolfNetworking());
  	    }
	
	    protected void onPostExecute(GameLobbyStartGameResponse result) {
	    	
	    	Log.v(TAG, "Signed in user has player id ");
	    	if (result.getStatus().equals("success")) {
	//	        final TextView errorText = (TextView) findViewById(R.id.error_text);
		    	currentGameID = getIntent().getLongExtra("selectedGameID", -100);
	
		    	WherewolfPreferences myPrefs = new WherewolfPreferences(GameLobbyActivity.this);
		    	myPrefs.setCurrentGameID(currentGameID);
	
		    	Intent createGameIntent = new Intent(GameLobbyActivity.this, MainScreenActivity.class);
		    	startActivity(createGameIntent);	
		    	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	    	}	
	    }
	
	}
	 
	
	private class GetPlayersTask extends AsyncTask<Void, Integer, GetPlayersResponse>{
		
		@Override
		protected GetPlayersResponse doInBackground(Void... request){
			
			WherewolfPreferences myPrefs = new WherewolfPreferences(GameLobbyActivity.this);
			String storedUsername = myPrefs.getUsername();
	        String storedPassword = myPrefs.getPassword();
	        currentGameID = getIntent().getLongExtra("selectedGameID", -100);
			
			GetPlayersRequest getPlayersRequest = new GetPlayersRequest(storedUsername, storedPassword, currentGameID);
			return getPlayersRequest.execute(new WherewolfNetworking());
		}
		
		protected void onPostExecute(GetPlayersResponse result){
			
	        if (result.getStatus().equals("success")) {
	        	
	        	arrayOfPlayers.clear();
	        	
				JSONArray jArray = result.getPlayers();
				
				for (int i=0; i<jArray.length(); i++){
					try{
						JSONObject oneObject = jArray.getJSONObject(i);
						// Pulling Items from array
						int playerId = oneObject.getInt("playerid");
						Drawable profPic = null;
						String profPicUrl;
						String playerName = oneObject.getString("playername");
						int numVotes = 0;
						
						if (playerName.startsWith("an") || playerName.startsWith("pa"))
						{
							profPicUrl = "villagerfemale";
						}
						else
						{
							profPicUrl = "villagermale";
						}
						
						arrayOfPlayers.add(new Player(playerId, profPic, profPicUrl, playerName, String.valueOf(numVotes))); 	
						adapter.notifyDataSetChanged();
					}                                    
					catch(JSONException e){
						Log.v(TAG, "JSON Exception was thrown");
					}
				}
	        } else {
	            // do something with bad password
	        	Toast.makeText(GameLobbyActivity.this, result.getErrorMessage(), Toast.LENGTH_LONG).show();
	        }

			
		}
	}
	
	private class LeaveGameLobbyTask extends AsyncTask<LeaveGameLobbyRequest, Integer, LeaveGameLobbyResponse>{

		@Override
		protected LeaveGameLobbyResponse doInBackground(LeaveGameLobbyRequest... request){

			WherewolfPreferences myPrefs = new WherewolfPreferences(GameLobbyActivity.this);
			String storedUsername = myPrefs.getUsername();
	        String storedPassword = myPrefs.getPassword();
	        currentGameID = getIntent().getLongExtra("selectedGameID", -100);

			LeaveGameLobbyRequest leaveGameLobbyRequest = new LeaveGameLobbyRequest(storedUsername, storedPassword, currentGameID);
			return leaveGameLobbyRequest.execute(new WherewolfNetworking());
		}

		protected void onPostExecute(LeaveGameLobbyResponse result){

			if (result.getStatus().equals("success")) {
				Toast.makeText(GameLobbyActivity.this, "Left game", Toast.LENGTH_LONG).show();
			} else {
				// do something with bad password
				Toast.makeText(GameLobbyActivity.this, result.getErrorMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}
	
}
