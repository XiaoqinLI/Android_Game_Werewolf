package edu.cs.utexas.rfdickerson;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends Activity {

	private static final String TAG = "registeractivity";
	public void stopRegistering()
	{
		Log.v(TAG, "closing the register screen");
		this.finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
	
		final Button button = (Button) findViewById(R.id.register_user_button);

		View.OnClickListener jim = new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "TESTTTTTTTTTT");
				stopRegistering();
			}
		};
		
		button.setOnClickListener(jim);
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
