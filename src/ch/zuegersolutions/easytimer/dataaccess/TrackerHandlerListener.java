package ch.zuegersolutions.easytimer.dataaccess;

import ch.zuegersolutions.easytimer.model.NfcTag;
import ch.zuegersolutions.easytimer.model.TaskUnit;

public interface TrackerHandlerListener {
	void onStartTracking(NfcTag tag, long starttime);
	void onStopTracking(TaskUnit taskUnit);
}
