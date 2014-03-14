package ch.zuegersolutions.easytimer;


import java.util.List;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import ch.zuegersolutions.easytimer.dataaccess.EasytimerOpenHelper;
import ch.zuegersolutions.easytimer.model.WorkDay;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

public class OverviewActivity extends Activity {
	
	private OverviewExpandableListViewAdapter overviewListViewAdapter;
	private EasytimerOpenHelper databaseHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("onCreate", this.getPackageName());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);
		
		List<WorkDay> workDays = getHelper().getWorkDayDao().queryForAll();
		
		ExpandableListView overviewListView = (ExpandableListView) findViewById(R.id.expandableOverviewListView);
		overviewListViewAdapter = new OverviewExpandableListViewAdapter(this, workDays);
		overviewListView.setAdapter(overviewListViewAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    //respond to menu item selection
		switch (item.getItemId()) {
		    case R.id.action_settings:
		    	startActivity(new Intent(this, SettingsActivity.class));
		    	return true;
		    case R.id.action_main:
		    	startActivity(new Intent(this, MainActivity.class));
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
	
	private EasytimerOpenHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, EasytimerOpenHelper.class);
		}
		return databaseHelper;
	}
}
