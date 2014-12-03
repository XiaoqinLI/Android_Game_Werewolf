package edu.utexas.LI.wherewolf;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class PlayerAdapter extends ArrayAdapter<Player>{
	public PlayerAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
     }
 
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
	    // Get the data item for this position
	    Player player = getItem(position);    
	    // Check if an existing view is being reused, otherwise inflate the view
	    if (convertView == null) {
	       convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_player, parent, false);
	    }
	    // Lookup view for data population
	    ImageView profileImg = (ImageView) convertView.findViewById(R.id.player_img);
	    TextView playerNameTV = (TextView) convertView.findViewById(R.id.username);
	    TextView playerIdTV = (TextView) convertView.findViewById(R.id.player_id);
	    // Populate the data into the template view using the data object
	    if (player.getProfilePicUrl().equals("villagerfemale")){
	    	profileImg.setImageResource(R.drawable.villagerfemale);
        }
        else
        {
        	profileImg.setImageResource(R.drawable.villagermale);
        }
	    playerIdTV.setText(String.valueOf(player.getPlayerId()));
	    playerNameTV.setText(player.getName());
//	    playervotesTV.setText(String.valueOf(player.getNumVotes()));    
	    // Return the completed view to render on screen
	    return convertView;
	}
}
