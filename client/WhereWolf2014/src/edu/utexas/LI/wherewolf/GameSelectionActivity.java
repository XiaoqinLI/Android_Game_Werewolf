package edu.utexas.LI.wherewolf;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class GameSelectionActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_selection);
		
		// Construct the data source
		ArrayList<Game> arrayOfGames = new ArrayList<Game>();
//		arrayOfGames.add(new Game(1, "eclipse", "Tom"));
//		arrayOfGames.add(new Game(2, "twinlight", "George"));
//		arrayOfGames.add(new Game(3, "new moon", "Abigail"));
//		arrayOfGames.add(new Game(4, "daybreak", "Martha"));
		// Create the adapter to convert the array to views
		GameAdapter adapter = new GameAdapter(this, arrayOfGames);
		// Attach the adapter to a ListView
//		ListView gameListView = (ListView) findViewById(android.R.id.list);
		ListView gameListView = getListView();
		gameListView.setAdapter(adapter);
		adapter.add(new Game(1, "eclipse", "Tom"));
		adapter.add(new Game(2, "twinlight", "George"));
		adapter.add(new Game(3, "new moon", "Abigail"));
		adapter.add(new Game(4, "daybreak", "Martha"));
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
