package ch.zuegersolutions.easytimer;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utility {
	public static final String NFC_MIME_TYPE = "application/ch.zuegersolutions.easytimer";
	public static final String APPLICATION = "ch.zuegersolutions.easytimer";

	
	public static boolean equalDates(Date date1, Date date2) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
		return simpleDateFormat.format(date1).equals(simpleDateFormat.format(date2));
	}
	
	
	public static String convertDurationToString(long duration) {
		long diffSeconds = duration / 1000 % 60;  
		long diffMinutes = duration / (60 * 1000) % 60;     
        long diffHours = duration / (60 * 60 * 1000); 
        
		return String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds);
	}
	
	public static String convertDateToTimeString(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.GERMANY);
		return simpleDateFormat.format(date);
	}
	
	public static int getHours(long duration) {
        long hours = duration / (60 * 60 * 1000);
        return (int) hours;
	}
	
	public static int getMinutes(long duration) {
		long minutes = duration / (60 * 1000) % 60; 
		return (int) minutes;
	}

	public static int getSeconds(long duration) {
		long seconds = duration / 1000 % 60;
		return (int) seconds;
	}
}
