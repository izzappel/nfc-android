package ch.zuegersolutions.easytimer;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import ch.zuegersolutions.easytimer.R;
import ch.zuegersolutions.easytimer.dataaccess.EasytimerOpenHelper;
import ch.zuegersolutions.easytimer.dataaccess.TrackerHandlerListener;
import ch.zuegersolutions.easytimer.dataaccess.TrackingHandler;
import ch.zuegersolutions.easytimer.model.NfcTag;
import ch.zuegersolutions.easytimer.model.TaskUnit;
import ch.zuegersolutions.easytimer.nfc.INfcUtility;
import ch.zuegersolutions.easytimer.nfc.NfcUtility;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class TrackerActivity extends Activity implements TrackerHandlerListener{
	
	private TextView text;
	private TimePicker timePicker;
	private Button changeTimeButton;
	
	private EasytimerOpenHelper databaseHelper = null;
	private TrackingHandler trackingHandler = null;
	
	private TaskUnit taskUnit = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracker_activity);
		
		text = (TextView)findViewById(R.id.trackerText);
		timePicker = (TimePicker)findViewById(R.id.timePicker);
		timePicker.setIs24HourView(true);
		changeTimeButton = (Button)findViewById(R.id.changeTimeButton);

        SharedPreferencesHandler preferences = new SharedPreferencesHandler(PreferenceManager.getDefaultSharedPreferences(this));
		trackingHandler = new TrackingHandler(preferences, getHelper(), this);
		trackingHandler.addListener(this);
		
		// see if app was started from a tag and start tracking time
        Intent intent = getIntent();
        if(intent.getType() != null && intent.getType().equals(Utility.NFC_MIME_TYPE)) {
        	INfcUtility nfcUtility = new NfcUtility();
    		NfcTag tag = nfcUtility.readNfcTag(intent);
    		
            displayMessage(tag.getId() + ": " + tag.getColor());
            
            trackingHandler.onNfcTagRegistered(tag);

            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            v.vibrate(200);
        }
	}

	public void changeTime(View view) {
		if(taskUnit != null) {
			int hours = timePicker.getCurrentHour();
			int minutes = timePicker.getCurrentMinute();
			
			long duration = hours * 3600000 + minutes * 60000;
			taskUnit.setDuration(duration);
			getHelper().getTaskUnitDao().update(taskUnit);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

        if (databaseHelper != null) {
                OpenHelperManager.releaseHelper();
                databaseHelper = null;
        }
	}

	private void displayMessage(String message) {
		message  = text.getText() + "\n" + message;
		text.setText(message);
	}

	private EasytimerOpenHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, EasytimerOpenHelper.class);
		}
		return databaseHelper;
	}

	@Override
	public void onStartTracking(NfcTag tag, long starttime) {
		displayMessage("Started Time Tracking at " + tag.getColor());

		timePicker.setVisibility(View.INVISIBLE);
		changeTimeButton.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onStopTracking(TaskUnit taskUnit) {
		this.taskUnit = taskUnit;
		
		displayMessage("Stopped Time Tracking at " + taskUnit.getTask().getTaskColor() + ": (" + Utility.convertDurationToString(taskUnit.getDuration()) + ")");

		timePicker.setCurrentHour(Utility.getHours(taskUnit.getDuration()));
		timePicker.setCurrentMinute(Utility.getMinutes(taskUnit.getDuration()));

		timePicker.setVisibility(View.VISIBLE);
		changeTimeButton.setVisibility(View.VISIBLE);
	}
}
