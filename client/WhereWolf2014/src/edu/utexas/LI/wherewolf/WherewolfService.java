package edu.utexas.LI.wherewolf;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.os.Process;

public class WherewolfService extends Service implements LocationListener{

	private static final String TAG = "Wherewolf Service";
	// number of milliseconds before a location update
	private static final int REPORT_PERIOD = 5000;
	private static final int MIN_CHANGE = 0;

	// allows us to prevent the CPU from going to sleep.
	private WakeLock wakeLock;

	// allows us to register updates to the GPS system
	private LocationManager locationManager;

	// we will use this handler to run asynchronous tasks through
	private Handler handler;

	private boolean isNight = false;
	private Game currentGame = null;
	private Player currentPlayer = null;

	private Looper mServiceLooper;

	//	private LocationListener listener = new LocationListener() {
	//	    @Override
	//	    public void onLocationChanged(Location location) {
	//	        
	//	        if (location != null) {
	//	            // do something with new location
	//	            double latitude = location.getLatitude();
	//	            double longitude = location.getLongitude();
	//	            

	//	        }
	//
	//	    }		
	//
	//		@Override
	//		public void onProviderDisabled(String arg0) {			
	//		}
	//
	//		@Override
	//		public void onProviderEnabled(String arg0) {			
	//		}
	//
	//		@Override
	//		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {		
	//		} 
	//
	//	};

	public void setNight()
	{
		handler.post( new Runnable() {
			@Override
			public void run () 
			{

				wakeLock.acquire();

				// makes location updates happen every 5 seconds, with no minimum 
				// distance for change notification            
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, REPORT_PERIOD, MIN_CHANGE, WherewolfService.this);
				isNight = true;
			}
		});
	}

	public synchronized boolean isNight()
	{
		return isNight;
	}

	public void setDay(){
		handler.post(new Runnable(){
			@Override
			public void run(){
				// Log.i(TAG, "Setting to day, turning off tracking");
				if (isNight){
					if (wakeLock.isHeld()){
						wakeLock.release();
					}
					locationManager.removeUpdates(WherewolfService.this);

					isNight = false;
					Log.i(TAG, "Stting to day, turning off tracking");

				}
			}
		});
	}

	@Override
	public void onCreate(){
		super.onCreate();

		HandlerThread thread = new HandlerThread("WherewolfThread", Process.THREAD_PRIORITY_BACKGROUND);

		thread.start();

		mServiceLooper = thread.getLooper();
		handler = new Handler(mServiceLooper);

		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(WherewolfService.this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {

			final String locMsg = "location changed "
					+ location.getLatitude() + " "
					+ location.getLongitude();

			// Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
			//	          showLocation(locMsg);

			Log.i(TAG, locMsg);
			//	        send the notification to the server here.
			//	        WherewolfNetworking net = new WherewolfNetworking();
			//	            
			// TODO: send the notification to the server here.
			// Create a UpdateGameRequest class
			// request.execute(new WherewolfNetworking())
			// WherewolfNetworking net = new WherewolfNetworking();

			// net.sendServerUpdate();

			// net.sendServerUpdate();
			// Log.i(TAG, "Network is " + net.isNetworkAvailable(getApplicationContext()));

			//	           Message msg = handler.obtainMessage();
			//	           msg.arg1 = ++counter;
			//	           handler.sendMessage(msg);

		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	// more methods will go here¡­	
}
