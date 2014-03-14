package ch.zuegersolutions.easytimer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimespanComponent extends RelativeLayout {
	
	private final NumberPicker numberPickerHours;
	private final NumberPicker numberPickerMinutes;
	private final NumberPicker numberPickerSeconds;
	
	private final TextView timeTextView;
	
	private long startTime;
	
	//runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            minutes = minutes % 60;
            seconds = seconds % 60;

            timeTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    
	public TimespanComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.timespan, this);
        
        numberPickerHours = (NumberPicker) findViewById(R.id.numberPickerHours);
        numberPickerMinutes = (NumberPicker) findViewById(R.id.numberPickerMinutes);
        numberPickerSeconds = (NumberPicker) findViewById(R.id.numberPickerSeconds);
        timeTextView = (TextView) findViewById(R.id.timeTextView);

		numberPickerHours.setVisibility(INVISIBLE);
		numberPickerMinutes.setVisibility(INVISIBLE);
		numberPickerSeconds.setVisibility(INVISIBLE);
		
		timeTextView.setVisibility(INVISIBLE);
		
		numberPickerHours.setMinValue(0);
		numberPickerMinutes.setMinValue(0);
		numberPickerSeconds.setMinValue(0);

		numberPickerHours.setMaxValue(100);
		numberPickerMinutes.setMaxValue(59);
		numberPickerSeconds.setMaxValue(59);
		
		numberPickerHours.setFormatter(new TwoDecimalFormatter());
		numberPickerMinutes.setFormatter(new TwoDecimalFormatter());
		numberPickerSeconds.setFormatter(new TwoDecimalFormatter());
		
		numberPickerMinutes.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if(oldVal == 59 && newVal == 0) {
					numberPickerHours.setValue(numberPickerHours.getValue() + 1);
				} else if(oldVal == 0 && newVal == 59) {
					int hours = numberPickerHours.getValue();
					if(hours > 0) {
						numberPickerHours.setValue(hours - 1);
					}
				}
			}
		});
		numberPickerSeconds.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if(oldVal == 59 && newVal == 0) {
					numberPickerMinutes.setValue(numberPickerMinutes.getValue() + 1);
				} else if(oldVal == 0 && newVal == 59) {
					int minutes = numberPickerMinutes.getValue();
					if(minutes > 0) {
						numberPickerMinutes.setValue(minutes - 1);
					}
				}
			}
		});
	}
	
	public void start() {
		numberPickerHours.setVisibility(INVISIBLE);
		numberPickerMinutes.setVisibility(INVISIBLE);
		numberPickerSeconds.setVisibility(INVISIBLE);
		
		timeTextView.setVisibility(VISIBLE);
		
		timerHandler.postDelayed(timerRunnable, 0);
	}
	
	public void stop() {
		timerHandler.removeCallbacks(timerRunnable);
	}
	
	public long getCurrentTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	public void setStarttime(long starttime) {
		startTime = starttime;
	}

	public void edit(long milliseconds) {
		numberPickerHours.setValue((int) milliseconds / (60 * 60 * 1000));
		numberPickerMinutes.setValue((int) milliseconds / (60 * 1000) % 60);
		numberPickerSeconds.setValue((int) milliseconds / 1000 % 60);
		
		numberPickerHours.setVisibility(VISIBLE);
		numberPickerMinutes.setVisibility(VISIBLE);
		numberPickerSeconds.setVisibility(VISIBLE);
		
		timeTextView.setVisibility(INVISIBLE);
	}
	
	public int getHours() {
		return numberPickerHours.getValue();
	}
	public int getMinutes() {
		return numberPickerMinutes.getValue();
	}
	public int getSeconds() {
		return numberPickerSeconds.getValue();
	}
	
	private class TwoDecimalFormatter implements NumberPicker.Formatter {

		@Override
		public String format(int number) {
			return String.format("%02d", number);
		}
		
	}
}
