package edu.utexas.LI.wherewolf;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Provides an abstraction for storing the shared preferences
 * @author rfdickerson
 *
 */
public class WherewolfPreferences {

	  private static final String PREF_URI = "edu.utexas.LI.wherewolf.prefs";
	  
	  Context context;
	  private SharedPreferences sharedPreferences;
	  
	  public WherewolfPreferences(Context context)
	  {
	      this.context = context;
	      sharedPreferences = context
	              .getSharedPreferences(PREF_URI,
	                      Context.MODE_PRIVATE);	      
	  }
	  
	  public String getUsername()
	  {
	      return sharedPreferences.getString("username", "");
	  }
	  
	  public String getPassword()
	  {
	      return sharedPreferences.getString("password", "");
	  }
	  
	  public long getCurrentGameID()
	  {
	      return sharedPreferences.getLong("currentGame", -100);
	  }
	  
	  public void setCreds(String username, String password)
	  {
	      SharedPreferences.Editor editor = sharedPreferences.edit();
	      
	      editor.putString("username", username);
	      editor.putString("password", password);
	      editor.commit();
	  }
	  
	  public void setCurrentGameID(long gameID)
	  {
	      SharedPreferences.Editor editor = sharedPreferences.edit();
	      editor.putLong("currentGame", gameID);
	      editor.commit();
	  }
	  
	  public void clearData(){
		  SharedPreferences.Editor editor = sharedPreferences.edit();
		  editor.clear();
	      editor.commit();
	  }
	  
	  public long getTime()
	  {
		  // DateTimeFormatter df =
	      return sharedPreferences.getLong("currentTime", 0);
		  
	  }
	  
	  public void setTime(long time)
	  {
	      SharedPreferences.Editor editor = sharedPreferences.edit();
	      editor.putLong("currentTime", time);
	      editor.commit();
	  }

	  public float getLatitude() {
		  // TODO Auto-generated method stub
		  return sharedPreferences.getFloat("lat", -1000);
	  }

	  public float getLongitude() {
		  // TODO Auto-generated method stub
		  return sharedPreferences.getFloat("lng", -1000);
	  }

	  public void setLatitude(double lat) {
		  SharedPreferences.Editor editor = sharedPreferences.edit();
		  editor.putFloat("lat", (float) lat);
		  editor.commit();
	  }

	  public void setLongitude(double lng) {
		  SharedPreferences.Editor editor = sharedPreferences.edit();
		  editor.putFloat("lng", (float) lng);
		  editor.commit();
	  }
}
