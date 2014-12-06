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
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

public class WherewolfService extends Service implements LocationListener{

	private static final String TAG = "Wherewolf Service";
	
	// number of milliseconds before a location update
	private static final int REPORT_PERIOD = 10000;
	private static final int MIN_CHANGE = 0;

	// allows us to prevent the CPU from going to sleep.
	private WakeLock wakeLock;

	// allows us to register updates to the GPS system
	private LocationManager locationManager;

	// we will use this handler to run asynchronous tasks through
	private Handler handler;
	private Looper mServiceLooper;

	private boolean isNight = false;
	private Game currentGame = null;
	private Player currentPlayer = null;

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
		
		Log.v(TAG, "Service is working");
		
		HandlerThread thread = new HandlerThread("WherewolfThread", Process.THREAD_PRIORITY_BACKGROUND);

		thread.start();

		mServiceLooper = thread.getLooper();
		handler = new Handler(mServiceLooper);

		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
		
		setNight();
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
	
	private void showLocation(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onLocationChanged(Location location){
		
		
		
		if (location != null){
			WherewolfPreferences pref = new WherewolfPreferences(WherewolfService.this);
			String username = pref.getUsername();

			double latitudeCurrent = location.getLatitude();
			double longitudeCurrent = location.getLongitude();

			if ( Math.abs(location.getLatitude() - pref.getLatitude()) > 0.0001 || Math.abs(location.getLongitude() - pref.getLongitude()) > 0.0001)
			{
				final String locMsg = "location changed " 
						+ (float)location.getLatitude() + " "
						+ (float)location.getLongitude();
				pref.setLatitude(latitudeCurrent);
				pref.setLongitude(longitudeCurrent);

				showLocation(locMsg);
				Log.i(TAG, locMsg);
			}
			
			else{
				final String locMsg = "current location: " 
						+ pref.getLatitude() + " "
						+ pref.getLongitude();
				showLocation(locMsg);
				Log.i(TAG, locMsg);
			}
			
			ChangedLocationRequest request = new ChangedLocationRequest(pref.getUsername(),
					pref.getPassword(), pref.getCurrentGameID(), location.getLatitude(), location.getLongitude());
			ChangedLocationResponse result = request.execute(new WherewolfNetworking());
						
			if (result.getStatus().equals("success"))
			{
				pref.setTime(result.getCurrentTime()); 
			}

		}		
		else{
			Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show();
		}
		
	}

	private LocationListener listener = new LocationListener(){
		@Override
		public void onLocationChanged(Location location)
		{
			if (location != null)
			{
				// do something with new location
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();

				// TODO: send the notification to the server here.
				// Create a UpdateGameRequest class
				// request.execute(new WherewolfNetworking())
				Log.v("GPS", "Lat is " + latitude + " longitude " + longitude);
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}
	};

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
