package edu.utexas.LI.wherewolf;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {

	private static final String TAG = "registeractivity";
	private EditText usernameEdit;
	private EditText passwordEdit;
	private EditText confirmPasswordEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
//		SharedPreferences sharedPref  = this.getSharedPreferences("myPrefs", MODE_WORLD_WRITEABLE);
		
		usernameEdit = (EditText) findViewById(R.id.usernameText);
		passwordEdit = (EditText) findViewById(R.id.passwordText);
		confirmPasswordEdit = (EditText) findViewById(R.id.passwordConfirmText);
		
		final Button registrationButton = (Button) findViewById(R.id.register_user_button);
		registrationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
				Log.v(TAG, "closing the register screen");
				signupClicked();
			}
		});
		

		
	}
	
	private void signupClicked() {

		Log.i(TAG,"Entered signupClicked()");		
		String username = usernameEdit.getText().toString();
		String password = passwordEdit.getText().toString();
		String confirmPassword = confirmPasswordEdit.getText().toString();
		
		if (password.equals(confirmPassword)){
			Log.i(TAG,"Entered Registration Succeeded");
			@SuppressWarnings("deprecation")
			SharedPreferences sharedPref  = this.getSharedPreferences("myPrefs", MODE_WORLD_WRITEABLE);
			SharedPreferences.Editor prefsEditor = sharedPref.edit();
			prefsEditor.putString("username", username);
			prefsEditor.putString("password", password);
			prefsEditor.commit();
			
			Intent explicitCallbackIntent = new Intent(this, LoginActivity.class);
			explicitCallbackIntent.putExtra("Explicit_Activity", username);// (String name, String value)
			setResult(RESULT_OK, explicitCallbackIntent);
			this.finish();
		}
		else{
			// create a notification
		}
		
//		Intent explicitCallbackIntent = new Intent(this, ActivityLoaderActivity.class);
//		explicitCallbackIntent.putExtra("Explicit_Activity", editTextString);// (String name, String value)
//		// TODO - Set Activity's result with result code RESULT_OK
//		setResult(RESULT_OK, explicitCallbackIntent);
//		// TODO - Finish the Activity
//		this.finish();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
