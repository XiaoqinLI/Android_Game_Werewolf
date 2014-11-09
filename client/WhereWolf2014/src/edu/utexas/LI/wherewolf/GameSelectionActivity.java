package edu.utexas.LI.wherewolf;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class GameSelectionActivity extends ListActivity {
	private static final String TAG = "GameSelectionActivity";
	private static ArrayList<Game> arrayOfGames = new ArrayList<Game>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_selection);
		
		// Create the adapter to convert the array to views
		GameAdapter adapter = new GameAdapter(this, arrayOfGames);
		// Attach the adapter to a ListView
//		ListView gameListView = (ListView) findViewById(android.R.id.list);
		ListView gameListView = getListView();
		gameListView.setAdapter(adapter);
		adapter.clear();
		adapter.add(new Game(1, "eclipse", "Tom"));
		adapter.add(new Game(2, "twinlight", "George"));
		adapter.add(new Game(3, "new moon", "Abigail"));
		adapter.add(new Game(4, "daybreak", "Martha"));
		adapter.add(new Game(5, "eclipse", "Tom"));
		adapter.add(new Game(6, "twinlight", "George"));
		adapter.add(new Game(7, "new moon", "Abigail"));
		adapter.add(new Game(8, "daybreak", "Martha"));
		adapter.add(new Game(9, "eclipse", "Tom"));
		adapter.add(new Game(10, "twinlight", "George"));
		adapter.add(new Game(11, "new moon", "Abigail"));
		adapter.add(new Game(12, "daybreak", "Martha"));
		adapter.add(new Game(13, "eclipse", "Tom"));
		adapter.add(new Game(14, "twinlight", "George"));
		adapter.add(new Game(15, "new moon", "Abigail"));
		adapter.add(new Game(16, "daybreak", "Martha"));
		
		gameListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "User joined a specific game from the list");
				Intent joinGameIntent = new Intent(GameSelectionActivity.this, JoinGameActivity.class);
				startActivity(joinGameIntent);
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
}
