package ch.zuegersolutions.easytimer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.zuegersolutions.easytimer.model.TaskUnit;
import ch.zuegersolutions.easytimer.model.WorkDay;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OverviewExpandableListViewAdapter extends BaseExpandableListAdapter {
	
	private Context context;
	private List<WorkDay> workDays;
	private SimpleDateFormat simpleDateFormat;
	
	public OverviewExpandableListViewAdapter(Context context, List<WorkDay> workDays) {
		super();
		
		this.context = context;
		this.workDays = workDays;
		simpleDateFormat = new SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.GERMANY);
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		WorkDay workDay = workDays.get(groupPosition);
		List<TaskUnit> entries = getAllTaskUnitsFrom(workDay);
		return entries.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		final TaskUnit taskUnit = (TaskUnit) getChild(groupPosition, childPosition);
		final String color =  taskUnit.getTask().getTaskColor();
		final String time = Utility.convertDurationToString(taskUnit.getDuration());
		final String starttime =  Utility.convertDateToTimeString(taskUnit.getStart());
				
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }
 
        TextView colorTextView = (TextView) convertView.findViewById(R.id.overviewListItemColor);
        colorTextView.setText(color);
        
        TextView starttimeTextView = (TextView) convertView.findViewById(R.id.overviewListItemStartTime);
        starttimeTextView.setText(starttime);
        
        TextView durationTextView = (TextView) convertView.findViewById(R.id.overviewListItemTime);
        durationTextView.setText(time);
        
        ImageView handledView = (ImageView) convertView.findViewById(R.id.handledImage);
        if(taskUnit.isHandled()) {
        	handledView.setVisibility(View.INVISIBLE);
        }
        
        return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return getAllTaskUnitsFrom(workDays.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return workDays.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return workDays.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String headerTitle = simpleDateFormat.format(((WorkDay)getGroup(groupPosition)).getDate().getTime());
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.overviewListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private List<TaskUnit> getAllTaskUnitsFrom(WorkDay workDay) {
		List<TaskUnit> entries = new ArrayList<TaskUnit>(workDay.getTaskUnits());		
		return entries;	

	}
	
}