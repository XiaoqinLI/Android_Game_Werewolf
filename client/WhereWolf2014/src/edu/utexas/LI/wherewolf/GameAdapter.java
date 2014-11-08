package edu.utexas.LI.wherewolf;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GameAdapter extends ArrayAdapter<Game>{
	 public GameAdapter(Context context, ArrayList<Game> games) {
	        super(context, 0, games);
	     }
	 
	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Game game = getItem(position);    
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_game, parent, false);
        }
        // Lookup view for data population
        TextView gameNameTV = (TextView) convertView.findViewById(R.id.game_name);
        TextView gameIdTV = (TextView) convertView.findViewById(R.id.game_id);
        TextView adminNameTV = (TextView) convertView.findViewById(R.id.admin_name);
        // Populate the data into the template view using the data object   
        gameIdTV.setText(String.valueOf(game.getGameId()));
        gameNameTV.setText(game.getGameName());
        adminNameTV.setText("admin:"+game.getAdminName());
        
        // Return the completed view to render on screen
        return convertView;
    }
}

