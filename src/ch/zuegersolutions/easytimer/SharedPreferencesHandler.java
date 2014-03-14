package ch.zuegersolutions.easytimer;

import java.util.Date;

import android.content.SharedPreferences;

public class SharedPreferencesHandler {
	private static final String START_TIME = Utility.APPLICATION + ".starttime";
	private static final String ID = Utility.APPLICATION + ".id";
	private static final String COLOR = Utility.APPLICATION + ".color";
	
	private SharedPreferences preferences;
	
	
	public SharedPreferencesHandler(SharedPreferences preferences) {
		this.preferences = preferences;
	}
	
	public long getStarttime() {
		return preferences.getLong(START_TIME, new Date().getTime());
	}
	
	public void setStarttime(long starttime) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(START_TIME, starttime);
		editor.commit();
	}
	
	public String getTagId() {
		return preferences.getString(ID, null);
	}
	
	public void setTagId(String tagId) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(ID, tagId);
		editor.commit();
	}
	
	public String getTagColor() {
		return preferences.getString(COLOR, null);
	}
	
	public void setTagColor(String tagColor) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(COLOR, tagColor);
		editor.commit();
	}
	
}
