package edu.utexas.LI.wherewolf;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class GameSelectionActivity extends ListActivity {
	private static final String TAG = "GameSelectionActivity";
	private static ArrayList<Game> arrayOfGames = new ArrayList<Game>();
	private long selectedGameID;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_selection);
		
		// Create the adapter to convert the array to views
		GameAdapter adapter = new GameAdapter(this, arrayOfGames);
		// Attach the adapter to a ListView
		ListView gameListView = getListView();
		gameListView.setAdapter(adapter);
		adapter.clear();
		adapter.add(new Game(1, "NightHunt", "michael", ""));
		adapter.add(new Game(2, "twinlight", "dwight", ""));
		adapter.add(new Game(3, "new moon", "jim", ""));
		adapter.add(new Game(4, "daybreak", "pam", ""));
//		update_game('michael', 'paper01', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
//	    update_game('dwight', 'paper02', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
//	    update_game('jim', 'paper03', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
//	    update_game('pam', 'paper04', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
//	    update_game('ryan', 'paper05', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
//	    update_game('andy', 'paper06', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
//	    update_game('angela', 'paper07', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
//	    update_game('toby', 'paper08', current_game_id, random.uniform(minValue, maxValue), random.uniform(minValue, maxValue))
		
		gameListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "User joined a specific game from the list");
				selectedGameID = id+1;
				WherewolfPreferences myPrefs = new WherewolfPreferences(GameSelectionActivity.this);
				String storedUsername = myPrefs.getUsername();
		        String storedPassword = myPrefs.getPassword();
		        
				GameSelectionRequest request = new GameSelectionRequest(storedUsername, storedPassword, id+1);
				JoinGameTask joinGameTask = new JoinGameTask();
				joinGameTask.execute(request);
				
				
			}
		});
		
		final Button createGameButton = (Button) findViewById(R.id.create_new_game_button);		
		createGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "User pressed the create new game button");
				Intent createGameIntent = new Intent(GameSelectionActivity.this, CreateGameActivity.class);
				startActivity(createGameIntent);			
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_selection, menu);
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
	
	@Override
	public void onBackPressed() {
	  super.onBackPressed();
	  overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	private class JoinGameTask extends AsyncTask<GameSelectionRequest, Integer, GameSelectionResponse> {

		@Override
		protected GameSelectionResponse doInBackground(GameSelectionRequest... params) {

			WherewolfPreferences myPrefs = new WherewolfPreferences(GameSelectionActivity.this);
			String username = myPrefs.getUsername();
	        String password = myPrefs.getPassword();
	        
			GameSelectionRequest gameSelectionRequest = new GameSelectionRequest(username, password, params[0].getGameId());
			return gameSelectionRequest.execute(new WherewolfNetworking());
		}

		protected void onPostExecute(GameSelectionResponse result) {
			Log.v(TAG, " passed the server");

			if (result.getStatus().equals("success")) {
				Intent joinGameIntent = new Intent(GameSelectionActivity.this, GameLobbyActivity.class).putExtra("selectedGameID", selectedGameID);
				startActivity(joinGameIntent);

			} else {
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(getLayoutInflater().inflate(R.layout.custom_toast_joingame,null));
				toast.show();
			}
		}
	}
	
}
