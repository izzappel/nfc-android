package ch.zuegersolutions.easytimer.dataaccess;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import ch.zuegersolutions.easytimer.model.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EasytimerOpenHelper extends OrmLiteSqliteOpenHelper {
	private final static int DATABASE_VERSION = 5;
	private final static String DATABASE_NAME = "easytimer";

    private RuntimeExceptionDao<WorkDay, Integer> workDayRuntimeDao = null;
    private RuntimeExceptionDao<Task, Integer> taskRuntimeDao = null;
    private RuntimeExceptionDao<TaskUnit, Integer> taskUnitRuntimeDao = null;
    
	
	public EasytimerOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // R.raw.ormlite_config);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, WorkDay.class);
			TableUtils.createTable(connectionSource, Task.class);
			TableUtils.createTable(connectionSource, TaskUnit.class);
		} catch (SQLException e) {
			Log.e(EasytimerOpenHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource cs, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, WorkDay.class, true);
			TableUtils.dropTable(connectionSource, Task.class, true);
			TableUtils.dropTable(connectionSource, TaskUnit.class, true);

			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(EasytimerOpenHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}
	
    public RuntimeExceptionDao<WorkDay, Integer> getWorkDayDao() {
            if (workDayRuntimeDao == null) {
            	workDayRuntimeDao = getRuntimeExceptionDao(WorkDay.class);
            }
            return workDayRuntimeDao;
    }

    public RuntimeExceptionDao<Task, Integer> getTaskDao() {
            if (taskRuntimeDao == null) {
            	taskRuntimeDao = getRuntimeExceptionDao(Task.class);
            }
            return taskRuntimeDao;
    }

    public RuntimeExceptionDao<TaskUnit, Integer> getTaskUnitDao() {
            if (taskUnitRuntimeDao == null) {
            	taskUnitRuntimeDao = getRuntimeExceptionDao(TaskUnit.class);
            }
            return taskUnitRuntimeDao;
    }
    
    public void clearDb() {
    	try {
			TableUtils.clearTable(getConnectionSource(), WorkDay.class);
			TableUtils.clearTable(getConnectionSource(), Task.class);
			TableUtils.clearTable(getConnectionSource(), TaskUnit.class);
		} catch (SQLException e) {
			Log.e(EasytimerOpenHelper.class.getName(), "Can't clear database", e);
			e.printStackTrace();
		}
    }
    
    public WorkDay getWorkDayFrom(Date date) {
    	List<WorkDay> workDays = getWorkDayDao().queryForEq("date", date);
    	if(workDays != null && workDays.size() > 0) {
    		return workDays.get(0);
    	} 
    	return null;
    }

    public WorkDay getWorkDayToday() {
    	WorkDay workDay = getWorkDayFrom(new Date());
    	if(workDay == null) {
    		workDay = new WorkDay(new Date());
    		getWorkDayDao().create(workDay);
    	}
    	return workDay;
    }
    
    public Task getTask(String taskId, String taskColor) {
		for(Task t : getTaskDao().queryForAll()) {
			if(t.getTaskId().equals(taskId) && t.getTaskColor().equals(taskColor)) {
				return t;
			}
		}
	
		Task task = new Task(taskId, taskColor);
		getTaskDao().create(task);
				
		return task;
    }

}
