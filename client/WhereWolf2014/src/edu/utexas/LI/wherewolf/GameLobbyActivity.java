package edu.utexas.LI.wherewolf;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class GameLobbyActivity extends ListActivity {
	private static final String TAG = "GameLobbyActivity";
	private static ArrayList<Player> arrayOfPlayers = new ArrayList<Player>();
	private long currentGameID;
	
	private void saveCurrentGameID(){	
		currentGameID = getIntent().getLongExtra("selectedGameID", -100);
		SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
		SharedPreferences.Editor editor = myPrefs.edit();
		editor.putLong("currentGameID", currentGameID);
		editor.commit();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_lobby);
		
		// Create the adapter to convert the array to views
		PlayerAdapter adapter = new PlayerAdapter(this, arrayOfPlayers);
		// Attach the adapter to a ListView
		ListView playerListView = getListView();
		playerListView.setAdapter(adapter);
		adapter.clear();
		adapter.add(new Player(1, "villagermale", "Tom", 5));
		adapter.add(new Player(2, "villagermale", "George", 3));
		adapter.add(new Player(3, "villagerfemale", "Abigail", 1));
		adapter.add(new Player(4, "villagerfemale", "Martha", 0));
		
		playerListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "Hit on a player, doing nothing");
				// do nothing
			}
		});
		
		final Button startGameButton = (Button) findViewById(R.id.start_game_button);		
		startGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "User pressed the create new game button");
				saveCurrentGameID();
				Intent createGameIntent = new Intent(GameLobbyActivity.this, MainScreenActivity.class);
				startActivity(createGameIntent);			
			}
		});	
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
}
