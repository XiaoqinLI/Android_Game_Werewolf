package edu.utexas.LI.wherewolf;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class GameSelectionActivity extends ListActivity {
	private static final String TAG = "GameSelectionActivity";
	private static ArrayList<Game> arrayOfGames = new ArrayList<Game>();
	
	private long selectedGameID;
	
	GameAdapter adapter;
	Game clickedGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_selection);
		Log.i(TAG, "got in the game selection activity");
		// Create the adapter to convert the array to views
		
		new GetGameListTask().execute();
		adapter = new GameAdapter(this, arrayOfGames);
		final ListView gameListView = getListView();
		
//		GameAdapter adapter = new GameAdapter(this, arrayOfGames);
		gameListView.setAdapter(adapter);
		
		LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.item_game, gameListView, false);               
//        gameListView.addHeaderView(header, null, false);	
		
		gameListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "User joined a specific game from the list");
				clickedGame = (Game) gameListView.getItemAtPosition(position);
				long clickedGameId = (long) clickedGame.getGameId();
				selectedGameID = clickedGameId;
				WherewolfPreferences myPrefs = new WherewolfPreferences(GameSelectionActivity.this);
				String storedUsername = myPrefs.getUsername();
		        String storedPassword = myPrefs.getPassword();
		        
				GameSelectionRequest request = new GameSelectionRequest(storedUsername, storedPassword, selectedGameID);
				JoinGameTask joinGameTask = new JoinGameTask();
				joinGameTask.execute(request);
				
				
			}
		});
		
		gameListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int idx, long id) {

				AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(GameSelectionActivity.this);
				deleteBuilder.setTitle("Delete");
				deleteBuilder.setMessage("Delete This Game?");
				deleteBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						clickedGame = (Game) gameListView.getItemAtPosition(idx);
						Log.v(TAG, "CLICKED GAME: " + clickedGame.getGameId());
						new LeaveGameTask().execute();
						new GetGameListTask().execute();
						dialog.dismiss();
						return;
					}
				});

				deleteBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog alert = deleteBuilder.create();
				alert.show();
				return true;
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
	
	//AsyncTask
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
	
	private class GetGameListTask extends AsyncTask<Void, Integer, GetGamesResponse>{

		@Override
		protected GetGamesResponse doInBackground(Void... request){
			
			WherewolfPreferences myPrefs = new WherewolfPreferences(GameSelectionActivity.this);			
			String username = myPrefs.getUsername();
			String password = myPrefs.getPassword();

			GetGamesRequest getGamesRequest = new GetGamesRequest(username, password);
			return getGamesRequest.execute(new WherewolfNetworking());
		}

		protected void onPostExecute(GetGamesResponse result){

			if (result.getStatus().equals("success")) {

				arrayOfGames.clear();

				JSONArray jArray = result.getGames();

				for (int i=0; i<jArray.length(); i++){
					try{
						JSONObject oneObject = jArray.getJSONObject(i);
						// Pulling Items from array
						int gameID = oneObject.getInt("game_id");
						String gameName = oneObject.getString("name");
						String adminName = oneObject.getString("admin_name");

						arrayOfGames.add(new Game(gameID, gameName, adminName,"")); 	
						adapter.notifyDataSetChanged();
					}                                    
					catch(JSONException e){
						Log.v(TAG, "JSON Exception was thrown");
					}
				}
				
			} else {
				// do something with bad password
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(getLayoutInflater().inflate(R.layout.custom_toast,null));
				toast.show();
			}


		}
	}
	
	private class LeaveGameTask extends AsyncTask<Void, Integer, LeaveGameResponse>{

		@Override
		protected LeaveGameResponse doInBackground(Void... request){

			WherewolfPreferences myPrefs = new WherewolfPreferences(GameSelectionActivity.this);
			String storedUsername = myPrefs.getUsername();
	        String storedPassword = myPrefs.getPassword();

			LeaveGameRequest leaveGameRequest = new LeaveGameRequest(storedUsername, storedPassword, clickedGame);
			return leaveGameRequest.execute(new WherewolfNetworking());
		}

		protected void onPostExecute(LeaveGameResponse result){

			if (result.getStatus().equals("success")) {
				Toast.makeText(GameSelectionActivity.this, "Deleted.", Toast.LENGTH_LONG).show();
				WherewolfPreferences myPrefs = new WherewolfPreferences(GameSelectionActivity.this);
				myPrefs.setCurrentGameID(-100);
			} else {
				// do something with bad password
				Toast.makeText(GameSelectionActivity.this, result.getErrorMessage(), Toast.LENGTH_LONG).show();
			}


		}
	}
	
	@Override
	protected void onStart() {
		Log.i(TAG, "started the game selection activity");
		super.onStart();
		
		new GetGameListTask().execute();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "restarted the game selection activity");
		super.onRestart();
		
		// adapter.clear();
		new GetGameListTask().execute();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "resumed the game selection activity");
		super.onResume();
		
		new GetGameListTask().execute();
		
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "pause the game selection activity");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "stopped the game selection activity");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "destroyed the game selection activity");
		super.onDestroy();
	}
	
}
