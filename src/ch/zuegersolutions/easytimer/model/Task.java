package ch.zuegersolutions.easytimer.model;

import java.util.ArrayList;
import java.util.Collection;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Task {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField
	private String taskId;
	
	@DatabaseField
	private String taskColor;
	
	@ForeignCollectionField
	private Collection<TaskUnit> taskUnits;
	
	public Task(String taskId, String taskColor) {
		this.taskId = taskId;
		this.taskColor = taskColor;
		this.taskUnits = new ArrayList<TaskUnit>();
	}
	
	public Task() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskColor() {
		return taskColor;
	}

	public void setTaskColor(String taskColor) {
		this.taskColor = taskColor;
	}

	public Collection<TaskUnit> getTaskUnits() {
		return taskUnits;
	}

	public void setTaskUnits(Collection<TaskUnit> taskUnits) {
		this.taskUnits = taskUnits;
	}
}
