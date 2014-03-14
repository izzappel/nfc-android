package ch.zuegersolutions.easytimer.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class WorkDay {
	@DatabaseField(generatedId = true)
	private int id;
    
	@DatabaseField(dataType = DataType.DATE_STRING, format = "yyyy-MM-dd")
	private Date date;
    
	@ForeignCollectionField
	private Collection<TaskUnit> taskUnits;
	
	public WorkDay(Date date) {
		this.date = date;
		taskUnits = new ArrayList<TaskUnit>();
	}
	
	public WorkDay() {
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Collection<TaskUnit> getTaskUnits() {
		return taskUnits;
	}
	public void setTaskUnits(Collection<TaskUnit> taskUnits) {
		this.taskUnits = taskUnits;
	}
}
