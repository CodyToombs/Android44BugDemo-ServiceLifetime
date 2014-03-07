package com.androidpolice.android44bugdemo_start_sticky;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

	    if (savedInstanceState == null || !savedInstanceState.containsKey("serviceHasStarted"))
		    startService(new Intent(getApplicationContext(), TestService.class));
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("serviceHasStarted", true);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

	    private Button btnKillService;
	    private CheckBox chkUseAlarm;
	    private TextView txtAndroidVersion;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

	        doBindService();

	        return rootView;
        }

	    @Override
	    public void onStart() {
		    super.onStart();

		    initControls();
	    }

	    @Override
	    public void onDestroyView() {
		    super.onDestroyView();
		    doUnbindService();
	    }

	    private void initControls() {
		    View view = getView();

		    assert view != null;
		    btnKillService = (Button) view.findViewById(R.id.btnKillService);
		    chkUseAlarm = (CheckBox) view.findViewById(R.id.chkUseAlarm);
		    txtAndroidVersion = (TextView) view.findViewById(R.id.txtAndroidVersion);

		    txtAndroidVersion.setText(Build.VERSION.RELEASE);

		    btnKillService.setOnClickListener(onClick_KillService);
		    chkUseAlarm.setOnCheckedChangeListener(onCheckedChange_UseAlarm);
	    }

	    private Button.OnClickListener onClick_KillService = new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    killService();
		    }
	    };
	    private CheckBox.OnCheckedChangeListener onCheckedChange_UseAlarm = new CompoundButton.OnCheckedChangeListener() {
		    @Override
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			    TestService.setUseAlarm(isChecked);
		    }
	    };

	    protected void killService() {
		    if (isServiceBound())
			    mBoundService.stopToastMessages();
		    doUnbindService();
		    getActivity().stopService(new Intent(getActivity().getApplicationContext(), TestService.class));
	    }

	    // Service Binding code
	    private TestService mBoundService;
	    private ServiceConnection serviceConnection = new ServiceConnection() {
		    @Override
		    public void onServiceConnected(ComponentName name, IBinder service) {
			    mBoundService = ((TestService.LocalBinder)service).getService();
			    chkUseAlarm.setChecked(TestService.getUseAlarm());
		    }

		    @Override
		    public void onServiceDisconnected(ComponentName name) {
			    mBoundService = null;
		    }
	    };
	    private void doBindService() {
		    getActivity().bindService(new Intent(getActivity().getApplicationContext(), TestService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	    }
	    private void doUnbindService() {
		    if (mBoundService != null) {
			    getActivity().unbindService(serviceConnection);
			    mBoundService = null;
		    }
	    }
	    private boolean isServiceBound() {
		    return mBoundService != null;
	    }
    }
}
