package edu.utexas.LI.wherewolf;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private static final String TAG = "registeractivity";
	private EditText usernameEdit;
	private EditText passwordEdit;
	private EditText confirmPasswordEdit;
	private EditText firstNameEdit;
	private EditText lastNameEdit;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		usernameEdit = (EditText) findViewById(R.id.usernameText);
		passwordEdit = (EditText) findViewById(R.id.passwordText);		
		confirmPasswordEdit = (EditText) findViewById(R.id.passwordConfirmText);
		firstNameEdit = (EditText) findViewById(R.id.first_name);
		lastNameEdit = (EditText) findViewById(R.id.last_name);
		

		
		usernameEdit.setText("Apple");
		passwordEdit.setText("12345678");
		confirmPasswordEdit.setText("12345678");
		firstNameEdit.setText("aaa");
		lastNameEdit.setText("bbb");
		
		final Button registrationButton = (Button) findViewById(R.id.register_user_button);
		registrationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {			
					String username = usernameEdit.getText().toString();
					String password = passwordEdit.getText().toString();
					String confirmPassword = confirmPasswordEdit.getText().toString();
					String firstname = firstNameEdit.getText().toString();
					String lastname = lastNameEdit.getText().toString();

				if (password.equals(confirmPassword) && password.length() != 0 && username.length() != 0){
					RegisterRequest request = new RegisterRequest(username, password, firstname, lastname);
					RegisterTask registerTask = new RegisterTask();
					registerTask.execute(request);
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

	private class RegisterTask extends AsyncTask<RegisterRequest, Integer, RegisterResponse> {

		@Override
		protected RegisterResponse doInBackground(RegisterRequest... params) {
			final EditText nameTV = (EditText) findViewById(R.id.usernameText);
			final EditText passTV = (EditText) findViewById(R.id.passwordText);
			final EditText firstnameTV = (EditText) findViewById(R.id.first_name);
			final EditText lastnameTV = (EditText) findViewById(R.id.last_name);

			String username = nameTV.getText().toString();
			String password = passTV.getText().toString();
			String firstname = firstnameTV.getText().toString();
			String lastname = lastnameTV.getText().toString();

			RegisterRequest registerRequest = new RegisterRequest(username, password, firstname, lastname);

			return registerRequest.execute(new WherewolfNetworking());
		}

		protected void onPostExecute(RegisterResponse result) {

			Log.v(TAG, " passed the server");

			if (result.getStatus().equals("success")) {
				String username = usernameEdit.getText().toString();
				Intent explicitCallbackIntent = new Intent(RegisterActivity.this, LoginActivity.class);
				explicitCallbackIntent.putExtra("Explicit_Activity", username);// (String name, String value)
				setResult(RESULT_OK, explicitCallbackIntent);
				finish();

			} else {
				Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(getLayoutInflater().inflate(R.layout.custom_toast,null));
				toast.show();
			}
		}
	}
}





//private void signupClicked() {
//
//	Log.i(TAG,"Entered signupClicked()");		
//	String username = usernameEdit.getText().toString();
//	String password = passwordEdit.getText().toString();
//	String confirmPassword = confirmPasswordEdit.getText().toString();
//
//	if (password.equals(confirmPassword) && password.length() != 0 && username.length() != 0){
//		Log.i(TAG,"Entered Registration Succeeded");
//
//		Intent explicitCallbackIntent = new Intent(this, LoginActivity.class);
//		explicitCallbackIntent.putExtra("Explicit_Activity", username);// (String name, String value)
//		setResult(RESULT_OK, explicitCallbackIntent);
//		this.finish();
//	}
//	else{
//		Toast toast = new Toast(getApplicationContext());
//
//		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//		toast.setDuration(Toast.LENGTH_LONG);
//
//		toast.setView(getLayoutInflater().inflate(R.layout.custom_toast,null));
//
//		toast.show();
//	}
//
//}
