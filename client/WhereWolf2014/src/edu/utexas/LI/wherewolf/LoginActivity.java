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

public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";
	private static final int GET_TEXT_REQUEST_CODE = 1;
	private EditText usernameEdit;
	private EditText passwordEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Log.i(TAG, "created the login activity");
		
		usernameEdit = (EditText) findViewById(R.id.usernameText);
		passwordEdit = (EditText) findViewById(R.id.passwordText);
		
		final Button registerButton = (Button) findViewById(R.id.registerButton);		
		registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "User pressed the register button");
				Intent explicitIntent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivityForResult(explicitIntent, GET_TEXT_REQUEST_CODE);
			}
		});
		
		final Button loginButton = (Button) findViewById(R.id.loginButton);		
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "User pressed the login button");
				if (usernameEdit.getText().toString().length() != 0 && passwordEdit.getText().toString().length() != 0){				
					Intent loginIntent = new Intent(LoginActivity.this, GameSelectionActivity.class);
					startActivity(loginIntent);			
				}
				else{
					Toast toast = new Toast(getApplicationContext());

			        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			        toast.setDuration(Toast.LENGTH_LONG);
			        
			        toast.setView(getLayoutInflater().inflate(R.layout.custom_toast,null));

			        toast.show();
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
		Log.i(TAG, "Entered onActivityResult()");
		
		// If so, update the username editText showing the user-entered username.	
		if (requestCode == GET_TEXT_REQUEST_CODE){
			if (resultCode == RESULT_OK){
				usernameEdit.setText(data.getStringExtra("Explicit_Activity"));
			}
		}   
    }

	@Override
	protected void onStart() {
		Log.i(TAG, "started the login activity");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "restarted the login activity");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "resumed the login activity");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "pause the login activity");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "stopped the login activity");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "destroyed the login activity");
		super.onDestroy();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.login, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
	 * action bar item clicks here. The action bar will // automatically handle
	 * clicks on the Home/Up button, so long // as you specify a parent activity
	 * in AndroidManifest.xml. int id = item.getItemId(); if (id ==
	 * R.id.action_settings) { return true; } return
	 * super.onOptionsItemSelected(item); }
	 */
}
