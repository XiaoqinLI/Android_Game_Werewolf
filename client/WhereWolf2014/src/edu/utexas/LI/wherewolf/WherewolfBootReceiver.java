package edu.utexas.LI.wherewolf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WherewolfBootReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, WherewolfService.class));	
	}

}
