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

public class MainScreenActivity extends ListActivity {
	private static final String TAG = "MainScreenActivity";
	private static ArrayList<Player> arrayOfPlayers = new ArrayList<Player>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		// Create the adapter to convert the array to views
		MainScreenPlayerAdapter adapter = new MainScreenPlayerAdapter(this, arrayOfPlayers);
		// Attach the adapter to a ListView
		ListView playerListView = getListView();
		playerListView.setAdapter(adapter);
		adapter.clear();
		adapter.add(new Player(1, "villagermale", "Tom", 4));
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
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