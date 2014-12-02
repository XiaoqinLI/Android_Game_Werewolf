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
	  
	  public void setCurrentGameID(int gameID)
	  {
	      SharedPreferences.Editor editor = sharedPreferences.edit();
	      editor.putInt("currentGame", gameID);
	      editor.commit();
	  }
	  
}
