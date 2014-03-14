package ch.zuegersolutions.easytimer.dataaccess;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import ch.zuegersolutions.easytimer.MainActivity;
import ch.zuegersolutions.easytimer.R;
import ch.zuegersolutions.easytimer.SharedPreferencesHandler;
import ch.zuegersolutions.easytimer.model.NfcTag;
import ch.zuegersolutions.easytimer.model.Task;
import ch.zuegersolutions.easytimer.model.TaskUnit;
import ch.zuegersolutions.easytimer.model.WorkDay;

public class TrackingHandler {
	SharedPreferencesHandler preferences;
	EasytimerOpenHelper dbHandler;
	Context context;
	
	List<TrackerHandlerListener> listeners;
	
	public TrackingHandler(SharedPreferencesHandler preferences, EasytimerOpenHelper dbHandler, Context context) {
		this.preferences = preferences;
		this.dbHandler = dbHandler;
		this.context = context;
		listeners = new ArrayList<TrackerHandlerListener>();
	}
	
	public void onNfcTagRegistered(NfcTag tag) {
		String tagid = preferences.getTagId();
		
		if(tagid == null) {
 			startTracking(tag);
		} else if(tagid.equals(tag.getId())) {
			stopTracking();
 		} else {
 			stopTracking();
 			startTracking(tag);
 		}
	}
	
	public TaskUnit stopTrackingAtCurrentTag() {
		return stopTracking();	
	}
	
	public boolean isTracking() {
		return preferences.getTagId() != null;
	}

	public String getCurrentTagId() {
		return preferences.getTagId();
	}
	public String getCurrentTagColor() {
		return preferences.getTagColor();
	}
	public long getCurrentDuration() {
		long starttime = preferences.getStarttime();
		long now = Calendar.getInstance().getTimeInMillis();
		long duration = now - starttime; 
		return duration;
	}
	
	private void startTracking(NfcTag tag) {
		long starttime = new Date().getTime();
		preferences.setTagId(tag.getId());
		preferences.setTagColor(tag.getColor());
		preferences.setStarttime(starttime);
		for(TrackerHandlerListener listener : listeners) {
			listener.onStartTracking(tag, starttime);
		}
		
		createNotification(tag, starttime);
	}
	
	private TaskUnit stopTracking() {
		String tagid = preferences.getTagId();
		String tagcolor = preferences.getTagColor();
		long starttime = preferences.getStarttime();
		
		long endtime = new Date().getTime();
		long duration = endtime - starttime; 
		
		preferences.setTagId(null);
		preferences.setTagColor(null);
		
		TaskUnit taskUnit = addTaskUnit(tagid, tagcolor, duration, starttime);
		for(TrackerHandlerListener listener : listeners) {
			listener.onStopTracking(taskUnit);
		}
		
		removeNotification();
		
		return taskUnit;
	}
	
	private TaskUnit addTaskUnit(String tagid, String tagcolor, long duration, long starttime) {
		WorkDay workDay = dbHandler.getWorkDayToday();
		Task task = dbHandler.getTask(tagid, tagcolor);
				
		TaskUnit taskUnit = new TaskUnit(duration, new Date(starttime), false, task, workDay);
		dbHandler.getTaskUnitDao().create(taskUnit);
		
		return taskUnit;
	}
	
	public void addListener(TrackerHandlerListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(TrackerHandlerListener listener) {
		this.listeners.remove(listener);
	}
	
	private void createNotification(NfcTag tag, long starttime) {
		NotificationCompat.Builder builder = new Builder(context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle("Tracking auf " + tag.getColor());
		builder.setWhen(starttime);
		builder.setUsesChronometer(true);
		builder.setOngoing(true);
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0); 
		
		builder.setContentIntent(pendingIntent);
		
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, builder.build());
	}
	
	private void removeNotification() {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(1);
	}
}
