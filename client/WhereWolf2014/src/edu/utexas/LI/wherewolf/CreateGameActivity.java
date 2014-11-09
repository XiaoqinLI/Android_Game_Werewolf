package edu.utexas.LI.wherewolf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGameActivity extends Activity {
	private static final String TAG = "CreateGameActivity";
	private EditText gameNameEdit;
	private EditText gamePasswordEdit;
	private EditText gameDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		
		gameNameEdit = (EditText) findViewById(R.id.gameNameText);
		gamePasswordEdit = (EditText) findViewById(R.id.passwordText);
		gameDescription = (EditText) findViewById(R.id.gameDescription);
		
		final Button createGameButton = (Button) findViewById(R.id.create_game_button);		
		createGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "User created a new game");
				createGameClicked();
						
			}
		});
	}
	
	private void createGameClicked() {
		Log.i(TAG,"Entered createGameClicked()");		
		String gameName= gameNameEdit.getText().toString();
		if (gameName.length() != 0){
			Log.i(TAG,"Creating Game Succeeded");
			Intent createGameIntent = new Intent(CreateGameActivity.this, GameLobbyActivity.class);
			startActivity(createGameIntent);	
		}
		
		else{
			Toast toast = new Toast(getApplicationContext());

	        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	        toast.setDuration(Toast.LENGTH_LONG);
	        
	        toast.setView(getLayoutInflater().inflate(R.layout.custom_toast,null));

	        toast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_game, menu);
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
