package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import java.util.List;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainScreenActivity extends Activity {
	
	private static final String TAG = "MainScreenActivity";
	private static ArrayList<Player> arrayOfPlayers = new ArrayList<Player>();
	
	SwipeListView swipelistview;
	MainScreenPlayerAdapter adapter;
	List<Player> playerData;
	
	private void clearSavedData(){
		SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
		SharedPreferences.Editor editor = myPrefs.edit();
		editor.clear();
        editor.commit();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		// Create the adapter to convert the array to views
//		if time == XXXX{
//		adapter = new MainScreenPlayerAdapter(this, arrayOfPlayers);
//		}
//		else
//			PlayerAdapter adapter = new MainScreenPlayerAdapter(this, arrayOfPlayers);
		// Attach the adapter to a ListView
		
		swipelistview=(SwipeListView)findViewById(R.id.list_of_players);
		playerData=new ArrayList<Player>();
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

		playerData.add(new Player(1, getResources().getDrawable(R.drawable.villagermale), "", "Michael", 1));
		playerData.add(new Player(2, getResources().getDrawable(R.drawable.villagerfemale), "", "Pam", 5));
		playerData.add(new Player(3, getResources().getDrawable(R.drawable.villagermale), "", "Toby", 10));
		playerData.add(new Player(4, getResources().getDrawable(R.drawable.villagermale), "",  "Jim", 9));
		playerData.add(new Player(5, getResources().getDrawable(R.drawable.villagerfemale), "",  "Angela", 13));

		adapter.notifyDataSetChanged();
	
		
//		ListView playerListView = getListView();
//		playerListView.setAdapter(adapter);
//		adapter.clear();
//		adapter.add(new Player(1, null, "villagermale", "Tom", 4));
//		adapter.add(new Player(2, null, "villagermale", "George", 3));
//		adapter.add(new Player(3, null, "villagerfemale", "Abigail", 1));
//		adapter.add(new Player(4, null, "villagerfemale", "Martha", 0));
//		
//		final SwipeDetector swipeDetector = new SwipeDetector();
//		playerListView.setOnTouchListener(swipeDetector);		
//		playerListView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				Log.v(TAG, "Hit on a player, doing nothing");
//				if (swipeDetector.swipeDetected()) {
//		            if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
//
//		                Toast.makeText(getApplicationContext(),
//		                    "Left to right", Toast.LENGTH_SHORT).show();
//
//		            }
//		            if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
//
//		                Toast.makeText(getApplicationContext(),
//		                    "Right to left", Toast.LENGTH_SHORT).show();
//
//		            }
//		        }
//				
//			}
//		});		
		
		final CircadianWidgetView circadianWidget = (CircadianWidgetView) findViewById(R.id.circadian);
		
		final SeekBar sk = (SeekBar) findViewById(R.id.daytime_seekbar);
//		MyOnChangeListener changeListener = new MyOnChangeListener();
//		changeListener.setCircadianViewWidget(circadianWidget);
//		seekbar.setOnSeekBarChangeListener( changeListener );
		
		sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       

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
		        // TODO Auto-generated method stub      
		    	// progress(time) from 0 - 99, (100)
		    	circadianWidget.changeTime(progress);
		    	
//		        t1.setTextSize(progress);
//		        Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_LONG).show();

		    }       
		});

		
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
	
	
}
