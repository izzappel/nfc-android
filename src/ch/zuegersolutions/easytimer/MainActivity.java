package ch.zuegersolutions.easytimer;

import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import ch.zuegersolutions.easytimer.R;
import ch.zuegersolutions.easytimer.dataaccess.EasytimerOpenHelper;
import ch.zuegersolutions.easytimer.dataaccess.TrackerHandlerListener;
import ch.zuegersolutions.easytimer.dataaccess.TrackingHandler;
import ch.zuegersolutions.easytimer.model.NfcTag;
import ch.zuegersolutions.easytimer.model.TaskUnit;
import ch.zuegersolutions.easytimer.nfc.INfcUtility;
import ch.zuegersolutions.easytimer.nfc.NfcUtility;

import android.nfc.*;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements TrackerHandlerListener {

	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFilters;
	
	private TimespanComponent timespanComponent;
	private Button stopTrackingButton;
	private Button changeTimeButton;
	private Switch switchHandledButton;
	private TextView explanationText;
	private TextView trackingInformation;
	
	private EasytimerOpenHelper databaseHelper = null;
	private TrackingHandler trackingHandler = null;
	
	private TaskUnit taskUnit = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("onCreate", this.getPackageName());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		timespanComponent = (TimespanComponent) findViewById(R.id.timespanComponent);

		stopTrackingButton = (Button) findViewById(R.id.stopTrackingButton);
		stopTrackingButton.setVisibility(View.INVISIBLE);
		
		changeTimeButton = (Button)findViewById(R.id.changeTimeButton);
		changeTimeButton.setVisibility(View.INVISIBLE);

		switchHandledButton = (Switch) findViewById(R.id.switchHandledButton);
		switchHandledButton.setVisibility(View.INVISIBLE);
		
		explanationText = (TextView) findViewById(R.id.explanationText);
		trackingInformation = (TextView) findViewById(R.id.trackingInformation);

		SharedPreferencesHandler preferences = new SharedPreferencesHandler(PreferenceManager.getDefaultSharedPreferences(this));
		trackingHandler = new TrackingHandler(preferences, getHelper(), this);
		trackingHandler.addListener(this);
		
		// see if app was started from a tag and start tracking time
        Intent intent = getIntent();
        if(intent.getType() != null && intent.getType().equals(Utility.NFC_MIME_TYPE)) {
        	onNewIntent(intent);
        }

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
		intentFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED) };	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onNewIntent(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		setIntent(intent);

		INfcUtility nfcUtility = new NfcUtility();
		NfcTag tag = nfcUtility.readNfcTag(intent);
		if(tag != null) {
	        trackingHandler.onNfcTagRegistered(tag);
	
	        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	        v.vibrate(200);
		}
    }
   
	@Override
	protected void onPause() {
		super.onPause();
		nfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
				
		updateDbTextView();

		if(trackingHandler.isTracking()) {
			trackingInformation.setText("Tracking auf " + trackingHandler.getCurrentTagColor());
			
			changeTimeButton.setVisibility(View.INVISIBLE);
			switchHandledButton.setVisibility(View.INVISIBLE);
			
			timespanComponent.setStarttime(System.currentTimeMillis() - trackingHandler.getCurrentDuration());
			timespanComponent.start();
			
			stopTrackingButton.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//respond to menu item selection
		switch (item.getItemId()) {
		    case R.id.action_settings:
		    	startActivity(new Intent(this, SettingsActivity.class));
		    	return true;
		    case R.id.action_overview:
		    	startActivity(new Intent(this, OverviewActivity.class));
		    	return true;
		    default:
		    	return super.onOptionsItemSelected(item);
		}
	}

	public void stopTracking(View view) {
		timespanComponent.stop();
		TaskUnit taskUnit = trackingHandler.stopTrackingAtCurrentTag();
		
		stopTrackingButton.setVisibility(View.INVISIBLE);
	}	
	
	public void changeTime(View view) {
		if(taskUnit != null) {
			int hours = timespanComponent.getHours(); 
			int minutes = timespanComponent.getMinutes();
			int seconds = timespanComponent.getSeconds();
			
			long duration = hours * 3600000 + minutes * 60000 + seconds * 1000;
			long oldDuration = taskUnit.getDuration();
			
			taskUnit.setDuration(duration);
			getHelper().getTaskUnitDao().update(taskUnit);
			
			trackingInformation.setText("Trackingzeit auf " + taskUnit.getTask().getTaskColor() + " von " + Utility.convertDurationToString(oldDuration) + " nach  " + Utility.convertDurationToString(duration) + " geändert.");
		}
	}
	
	public void switchHandled(View view) {
		if(taskUnit != null) {
			taskUnit.setHandled(switchHandledButton.isChecked());
			getHelper().getTaskUnitDao().update(taskUnit);
		}
	}
	
	
	@Override
	public void onStartTracking(NfcTag tag, long starttime) {
		trackingInformation.setText("Tracking auf " + trackingHandler.getCurrentTagColor());
		
		changeTimeButton.setVisibility(View.INVISIBLE);
		switchHandledButton.setVisibility(View.INVISIBLE);

		timespanComponent.setStarttime(System.currentTimeMillis() - trackingHandler.getCurrentDuration());
		timespanComponent.start();
		
		stopTrackingButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void onStopTracking(TaskUnit taskUnit) {
		this.taskUnit = taskUnit;

		trackingInformation.setText("Tracking auf " + taskUnit.getTask().getTaskColor() + " nach " + Utility.convertDurationToString(taskUnit.getDuration()) + " beendet.");

		timespanComponent.edit(taskUnit.getDuration());

		changeTimeButton.setVisibility(View.VISIBLE);
		switchHandledButton.setVisibility(View.VISIBLE);
		stopTrackingButton.setVisibility(View.INVISIBLE);
		
		updateDbTextView();
	}
	
	private void updateDbTextView() {
		List<TaskUnit> taskUnits = getHelper().getTaskUnitDao().queryForAll();
		explanationText.setText("Es hat " + taskUnits.size() + " Einträge.");
	}
	
	private EasytimerOpenHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, EasytimerOpenHelper.class);
		}
		return databaseHelper;
	}
}
