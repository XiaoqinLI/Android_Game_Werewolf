package edu.utexas.LI.wherewolf;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenPlayerAdapter extends ArrayAdapter<Player> {

	List<Player>   data;
	Context context;
	int layoutResID;

	public MainScreenPlayerAdapter(Context context, int layoutResourceId, List<Player> data) {
		super(context, layoutResourceId, data);

		this.data=data;
		this.context=context;
		this.layoutResID=layoutResourceId;


		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		NewsHolder holder = null;
		View row = convertView;
		holder = null;

		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResID, parent, false);

			holder = new NewsHolder();

			holder.playerId = (TextView)row.findViewById(R.id.player_id);
			holder.profPic = (ImageView)row.findViewById(R.id.prof_pic);
			holder.playerName = (TextView)row.findViewById(R.id.player_name);
			holder.numVotes = (TextView)row.findViewById(R.id.num_votes);
			holder.attackButton=(Button)row.findViewById(R.id.attack_button);
			holder.voteButton=(Button)row.findViewById(R.id.vote_button);
			row.setTag(holder);
		}
		else
		{
			holder = (NewsHolder)row.getTag();
		}

		///////// *** Added (ItemRow) cast
		Player playerdata = (Player) data.get(position);
		holder.playerId.setText(String.valueOf(playerdata.getPlayerId()));
		holder.profPic.setImageDrawable(playerdata.getProfPic());
		holder.playerName.setText(playerdata.getName());
		holder.numVotes.setText(String.valueOf(playerdata.getNumVotes()));

		holder.attackButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Attack Button Clicked",Toast.LENGTH_SHORT).show();
			}
		});

		holder.voteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "Vote Button Clicked",Toast.LENGTH_SHORT).show();
			}
		});

		return row;

	}

	static class NewsHolder{

		TextView playerId;
		ImageView profPic;
		TextView playerName;
		TextView numVotes;
		Button attackButton;
		Button voteButton;
	}

}