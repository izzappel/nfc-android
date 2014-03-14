package ch.zuegersolutions.easytimer;

import java.io.IOException;
import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ch.zuegersolutions.easytimer.dataaccess.EasytimerOpenHelper;
import ch.zuegersolutions.easytimer.model.TaskUnit;
import ch.zuegersolutions.easytimer.nfc.INfcUtility;
import ch.zuegersolutions.easytimer.nfc.NfcUtility;

public class SettingsActivity  extends Activity {

	private NfcAdapter nfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFilters;
	
	private Intent intent;

	private EasytimerOpenHelper databaseHelper = null;

	private TextView explanationText;
	private EditText nfcTagColorEditText;

	private String farbe = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("onCreate", this.getPackageName());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		explanationText = (TextView) findViewById(R.id.messageTextView);
		nfcTagColorEditText = (EditText) findViewById(R.id.nfcTagColorEditText);

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		intentFilters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED) };		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onNewIntent(Intent intent) {
		Toast.makeText(this, "Nfc Tag detected", Toast.LENGTH_LONG).show();
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        v.vibrate(200);
		this.intent = intent;
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

		explanationText.setText("");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//respond to menu item selection
		switch (item.getItemId()) {
		    case R.id.action_main:
		    	startActivity(new Intent(this, MainActivity.class));
		    	return true;
		    case R.id.action_overview:
		    	startActivity(new Intent(this, OverviewActivity.class));
		    	return true;
		    default:
		    	return super.onOptionsItemSelected(item);
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
	
	public void writeTag(View view) {
		if(intent != null) {
			writeNfc(intent);
		}
	}
	
	public void readTag(View view) {
		if(intent != null) {
			readNfc(intent);
		}
	}
	
	public void clearDb(View view) {
		getHelper().clearDb();
		
		List<TaskUnit> taskUnits = getHelper().getTaskUnitDao().queryForAll();
		displayMessage("Es hat " + taskUnits.size() + " Einträge.");	
	}

	
	private void writeNfc(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		INfcUtility nfcUtility = new NfcUtility();
		try {
			nfcUtility.writeNfcTag(tag, nfcTagColorEditText.getText().toString());
			displayMessage("Tag was successfully written: " + nfcTagColorEditText.getText());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}

	private void readNfc(Intent intent) {
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		NdefRecord uidRecord = msg.getRecords()[0];
		farbe = new String(uidRecord.getPayload());
		
		displayMessage("Farbe ist " + farbe);
	}

	private void displayMessage(String message) {
		message  = explanationText.getText() + "\n" + message;
		explanationText.setText(message);
	}
	
	private EasytimerOpenHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, EasytimerOpenHelper.class);
		}
		return databaseHelper;
	}
}
