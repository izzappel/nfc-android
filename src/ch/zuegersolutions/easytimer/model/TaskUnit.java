package ch.zuegersolutions.easytimer.model;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class TaskUnit {
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private long duration;
	
	@DatabaseField
	private Date start;
	
	@DatabaseField
	private boolean isHandled;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true) 
	private Task task;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true) 
	private WorkDay workDay;
	
	
	public TaskUnit(long duration, Date start,  boolean isHandled, Task task, WorkDay workDay) {
		this.duration = duration;
		this.start = start;
		this.isHandled = isHandled;
		this.task = task;
		this.workDay = workDay;
	}
	
	public TaskUnit() {
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}

	public boolean isHandled() {
		return isHandled;
	}

	public void setHandled(boolean isHandled) {
		this.isHandled = isHandled;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public WorkDay getWorkDay() {
		return workDay;
	}

	public void setWorkDay(WorkDay workDay) {
		this.workDay = workDay;
	}
	
}
