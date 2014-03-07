package com.androidpolice.android44bugdemo_start_sticky;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * Created by codytoombs on 2/26/14.
 */
public class TestService extends Service {
	public static final String TAG = "START_STICKY_DEMO";
	public static final String PREFS_NAME = "defaultPrefs";

	final Handler handler = new Handler();

	private static SharedPreferences settings;
	private static boolean useAlarm;
	public static boolean getUseAlarm() {
		return useAlarm;
	}
	public static void setUseAlarm(boolean enabled) {
		if (useAlarm != enabled) {
			useAlarm = enabled;

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("useAlarm", useAlarm);
			editor.commit();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		settings = getSharedPreferences(PREFS_NAME, 0);
		useAlarm = settings.getBoolean("useAlarm", false);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// check if the call is coming from an alarm instead of the first run of the service
		if (intent != null && intent.hasExtra("callType") && intent.getStringExtra("callType").equals("Alarm")) {
			startToastMessages();
		}
		else {
			Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show();
			startToastMessages();
		}
		return START_STICKY;
	}

	// Handler-based time delays (should work fine on all versions except 4.4.1 / 4.4.2
	public void startToastMessages() {
		scheduleToastEvent();
	}
	public void stopToastMessages() {
		clearToastEvent();
	}

	private void scheduleToastEvent() {
		clearToastEvent(); // Clears out old instances so we don't end up with multiple toasts piling up
		int delay = 4000; // 4 seconds

		handler.postDelayed(toastRunnable, delay);

		if (useAlarm)
		{
			// Using an Alarm to restart the Service, which should be reliable in 4.4.1 / 4.4.2.
			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(getApplicationContext(), TestService.class);
			intent.putExtra("callType", "Alarm");
			PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2)
				alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, scheduledIntent);
			else
				alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, scheduledIntent);
		}
	}

	private void clearToastEvent() {
		// Remove Handler-based events
		handler.removeCallbacks(toastRunnable);

		// Remove Alarm-based events
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), TestService.class );
		PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(scheduledIntent);
	}
	private void doToastEvent() {
		Toast.makeText(getApplicationContext(), "ding", Toast.LENGTH_SHORT).show();
		scheduleToastEvent();
	}

	private Runnable toastRunnable = new Runnable() {
		@Override
		public void run() {
			doToastEvent();
		}
	};

	/**
	 * Return the communication channel to the service.  May return null if
	 * clients can not bind to the service.  The returned
	 * {@link android.os.IBinder} is usually for a complex interface
	 * that has been <a href="{@docRoot}guide/components/aidl.html">described using
	 * aidl</a>.
	 * <p/>
	 * <p><em>Note that unlike other application components, calls on to the
	 * IBinder interface returned here may not happen on the main thread
	 * of the process</em>.  More information about the main thread can be found in
	 * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
	 * Threads</a>.</p>
	 *
	 * @param intent The Intent that was used to bind to this service,
	 *               as given to {@link android.content.Context#bindService
	 *               Context.bindService}.  Note that any extras that were included with
	 *               the Intent at that point will <em>not</em> be seen here.
	 * @return Return an IBinder through which clients can call on to the
	 * service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public TestService getService() {
			return TestService.this;
		}
	}
	private final IBinder mBinder = new LocalBinder();
}
