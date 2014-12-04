package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainScreenActivity extends Activity {
	
	private static final String TAG = "MainScreenActivity";
	private static ArrayList<Player> playerData = new ArrayList<Player>();
	private long currentGameID;
	MainScreenPlayerAdapter adapter;
	SwipeListView swipelistview;

	private void clearSavedData(){
    	WherewolfPreferences myPrefs = new WherewolfPreferences(MainScreenActivity.this);
    	myPrefs.clearData();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
    	WherewolfPreferences myPrefs = new WherewolfPreferences(MainScreenActivity.this);
    	
        currentGameID = myPrefs.getCurrentGameID();
        
		new GetPlayersTask().execute();

		swipelistview=(SwipeListView)findViewById(R.id.list_of_players);
		
		adapter=new MainScreenPlayerAdapter (this, R.layout.player_row, playerData);
		
		swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
	         @Override
	         public void onOpened(int position, boolean toRight) {
	         }
	 
	         @Override
	         public void onClosed(int position, boolean fromRight) {
	         }
	 
	         @Override
	         public void onListChanged() {
	         }
	 
	         @Override
	         public void onMove(int position, float x) {
	         }
	 
	         @Override
	         public void onStartOpen(int position, int action, boolean right) {
	             Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
	         }
	 
	         @Override
	         public void onStartClose(int position, boolean right) {
	             Log.d("swipe", String.format("onStartClose %d", position));
	         }
	 
	         @Override
	         public void onClickFrontView(int position) {
	             Log.d("swipe", String.format("onClickFrontView %d", position));
	 
	             swipelistview.openAnimate(position); //when you touch front view it will open
	 
	         }
	 
	         @Override
	         public void onClickBackView(int position) {
	             Log.d("swipe", String.format("onClickBackView %d", position));
	 
	             swipelistview.closeAnimate(position);//when you touch back view it will close
	         }
	 
	         @Override
	         public void onDismiss(int[] reverseSortedPositions) {
	 
	         }
	 
	     });
		
		//These are the swipe listview settings. you can change these
		//setting as your requirement
		swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
		swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
		swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
		swipelistview.setOffsetLeft(convertDpToPixel(0f)); // left side offset
		swipelistview.setOffsetRight(convertDpToPixel(0f)); // right side offset
		swipelistview.setAnimationTime(50); // animation time
		swipelistview.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress

		swipelistview.setAdapter(adapter);

		adapter.notifyDataSetChanged();
//		
		// Widget
		final CircadianWidgetView circadianWidget = (CircadianWidgetView) findViewById(R.id.circadian);
		
		//SeekBar
		final SeekBar sk = (SeekBar) findViewById(R.id.daytime_seekbar);	
		sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {   
			boolean progressIsNight;
						
		    @Override       
		    public void onStopTrackingTouch(SeekBar seekBar) {      
		        // TODO Auto-generated method stub      
		    }       

		    @Override       
		    public void onStartTrackingTouch(SeekBar seekBar) {     
		        // TODO Auto-generated method stub      
		    }       

		    @Override       
		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {     
		    	circadianWidget.changeTime(progress);
		    	
		    	progressIsNight = (progress % 24 >=6 && progress % 24 <= 18);
		    	
		    	if (progressIsNight)
		    	{
		    		adapter.setIsNight(true);
		    		adapter.notifyDataSetChanged();
		    	}
		    	else
		    	{
		    		adapter.setIsNight(false);
		    		adapter.notifyDataSetChanged();
		    	}

		    }       
		});
		
		// logout button
		final Button logoutButton = (Button) findViewById(R.id.logout_button);		
		logoutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "User pressed the register button");
				
				
				AlertDialog.Builder builder = new AlertDialog.Builder(MainScreenActivity.this);
				builder.setMessage("Are you sure you want to exit?")
				   .setCancelable(false)
				   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				       public void onClick(DialogInterface dialog, int id) {
				    	    clearSavedData();
							Intent explicitIntent = new Intent(MainScreenActivity.this, LoginActivity.class);
							explicitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
							explicitIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(explicitIntent);
				       }
				   })
				   .setNegativeButton("No", new DialogInterface.OnClickListener() {
				       public void onClick(DialogInterface dialog, int id) {
				            dialog.cancel();
				       }
				   });
				AlertDialog alert = builder.create();
				alert.show();			
			}
		});	
	
	}
	
	@Override
	protected void onStart() {
		Log.i(TAG, "started the game lobby activity");
		super.onStart();
		adapter.clear();
		new GetPlayersTask().execute();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "restarted the game lobby activity");
		super.onRestart();
		adapter.clear();
		new GetPlayersTask().execute();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "resumed the game lobby activity");
		super.onResume();
		adapter.clear();
		new GetPlayersTask().execute();
		
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "pause the game lobby activity");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "stopped the game lobby activity");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "destroyed the game lobby activity");
		super.onDestroy();
	}	
	
	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
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
	
	private class GetPlayersTask extends AsyncTask<Void, Integer, GetPlayersResponse>{

		@Override
		protected GetPlayersResponse doInBackground(Void... request){

			WherewolfPreferences myPrefs = new WherewolfPreferences(MainScreenActivity.this);
			String storedUsername = myPrefs.getUsername();
			String storedPassword = myPrefs.getPassword();
			currentGameID = myPrefs.getCurrentGameID();


			GetPlayersRequest getPlayersRequest = new GetPlayersRequest(storedUsername, storedPassword, currentGameID);
			return getPlayersRequest.execute(new WherewolfNetworking());
		}

		protected void onPostExecute(GetPlayersResponse result){

			if (result.getStatus().equals("success")) {

				playerData.clear();

				JSONArray jArray = result.getPlayers();

				for (int i=0; i<jArray.length(); i++){
					try{
						JSONObject oneObject = jArray.getJSONObject(i);
						// Pulling Items from array
						int playerId = oneObject.getInt("playerid");
						Drawable profPic = null;
						String profPicUrl;
						String playerName = oneObject.getString("playername");
						int numVotes = 0;

						if (playerName.startsWith("an") || playerName.startsWith("pa"))
						{
							profPicUrl = "villagerfemale";
						}
						else
						{
							profPicUrl = "villagermale";
						}
						//						playerData.add(new Player(1, getResources().getDrawable(R.drawable.villagermale), "", "Michael", String.valueOf(1)));

						playerData.add(new Player(playerId, profPic, profPicUrl, playerName, String.valueOf(numVotes))); 	
						adapter.notifyDataSetChanged();
					}                                    
					catch(JSONException e){
						Log.v(TAG, "JSON Exception was thrown");
					}
				}
			} else {
				// do something with bad password
				Toast.makeText(MainScreenActivity.this, result.getErrorMessage(), Toast.LENGTH_LONG).show();
			}


		}
	}


}
