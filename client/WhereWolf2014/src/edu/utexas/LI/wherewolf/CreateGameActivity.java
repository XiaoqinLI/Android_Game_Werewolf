package edu.utexas.LI.wherewolf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
	private EditText gameDescriptionEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		
		gameNameEdit = (EditText) findViewById(R.id.gameNameText);
		gamePasswordEdit = (EditText) findViewById(R.id.passwordText);
		gameDescriptionEdit = (EditText) findViewById(R.id.gameDescription);
		gameNameEdit.setText("Apple");
		gameNameEdit.setText("Apple");
		gamePasswordEdit.setText("123456");
		gameDescriptionEdit.setText("Good");

		
		final Button createGameButton = (Button) findViewById(R.id.create_game_button);		
		createGameButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "User creating a new game");
				WherewolfPreferences myPrefs = new WherewolfPreferences(CreateGameActivity.this);
		        String storedUsername = myPrefs.getUsername();
		        String storedPassword = myPrefs.getPassword();
				String gameName = gameNameEdit.getText().toString();
				String gameDescription = gameDescriptionEdit.getText().toString();
//				Game game = new Game (0, gameName, username, gameDescription);

				if (gameName.length() != 0){
					CreateGameRequest request = new CreateGameRequest(storedUsername, storedPassword, gameName, gameDescription);
					CreateGameTask createGameTask = new CreateGameTask();
					createGameTask.execute(request);
				}else{
					Toast toast = new Toast(getApplicationContext());
			        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			        toast.setDuration(Toast.LENGTH_LONG);	        
			        toast.setView(getLayoutInflater().inflate(R.layout.custom_toast_creategame,null));
			        toast.show();
				}
			}
		});
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
	
	private class CreateGameTask extends AsyncTask<CreateGameRequest, Void, CreateGameResponse>{
		@Override
		protected CreateGameResponse doInBackground(CreateGameRequest... params){
//			WherewolfNetworking net = new WherewolfNetworking();
			
			final EditText gameNameEdit = (EditText) findViewById(R.id.gameNameText);
			final EditText gameDescriptionEdit = (EditText) findViewById(R.id.gameDescription);
			String gameName = gameNameEdit.getText().toString();
			String gameDescription = gameDescriptionEdit.getText().toString();
			SharedPreferences sharedPreferences = getSharedPreferences("edu.utexas.LI.wherewolf.prefs", Context.MODE_PRIVATE);		
			String username = sharedPreferences.getString("username", "");
			String password = sharedPreferences.getString("password", "");
			CreateGameRequest createGamerequest = new CreateGameRequest(username, password, gameName, gameDescription);
			return createGamerequest.execute(new WherewolfNetworking());
//			return net.createGame(params[0]);
		}

		protected void onPostExecute(CreateGameResponse result){

			if (result.getStatus().equals("success"))
			{				
				WherewolfPreferences myPrefs = new WherewolfPreferences(CreateGameActivity.this);
	            myPrefs.setCurrentGameID(result.getGameID());
				Intent intent = new Intent(CreateGameActivity.this, GameLobbyActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);				
			} else {
				// do something with bad password           	
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(Toast.LENGTH_LONG);   	        
				toast.setView(getLayoutInflater().inflate(R.layout.custom_toast_creategame,null));
				toast.show();
			}


		}
	}
	
//	public class CreateGameTask extends AsyncTask<CreateGameRequest, Void, CreateGameResponse>{
//		@Override
//		protected CreateGameResponse doInBackground(CreateGameRequest... params){
//			WherewolfNetworking net = new WherewolfNetworking();
//			
//			return net.createGame(params[0]);
//			
//		}
//		
//		protected void onPostExecute(CreateGameResponse param){
//			
//			if (param.getStatus().equals("success"))
//			{
//
//				SharedPreferences sharedPreferences = getSharedPreferences("edu.utexas.bleiweiss.wherewolf.prefs", Context.MODE_PRIVATE);
//				
//				Editor editor = sharedPreferences.edit();
//				editor.putInt("gameId", Integer.parseInt(param.getGame().getGameId()));
//				editor.commit();
//				
//				Intent intent = new Intent(CreateGameActivity.this, GameLobbyActivity.class);
//				startActivity(intent);
//				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//				finish();
//				
//			} else{
//				TextView errorTextView = (TextView) findViewById(R.id.error_message_tv);
//				errorTextView.setText(param.getErrorMessage());			
//			}
//			
//		}
//		
//		
//	}
}
