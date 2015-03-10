package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.Model.DateModel.SavingDate;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.Activity;
import android.os.SystemClock;

public class TimeModel {

	public final static byte HOUR_UP     = 7,
							 HOUR_DOWN   = 8,
							 MINUTE_UP   = 9,
							 MINUTE_DOWN = 10;		
	
	private Activity activity;
	private Calendar calendar;
	
	private int currHour, hour, currMinute, minute, amPm;
	
	public TimeModel(Activity activity) {
		
		this.activity = activity;
		calendar = Calendar.getInstance();
	}
	
	public void changeTime(int mode) {
		
		switch(mode) {
		
		case HOUR_UP		:
			if(hour < 12) {
				hour += 1;
			} else {
				hour = 1;
			}
			break;
			
		case HOUR_DOWN	:
			if(hour > 1) {
				hour -= 1;
			} else {
				hour = 12;
			}
			break;
			
		case MINUTE_UP	:
			if(minute < 59) {
				minute += 1;
			} else {
				minute = 0;
			}
			break;
			
		case MINUTE_DOWN	:
			if(minute > 0) {
				minute -= 1;
			} else {
				minute = 59;
			}
			break;
			
		default		:
			break;
		}
	}
	
	public void changeAmPm() { // changing the am/pm
		
		if(amPm == 0) {
			
			amPm = 1;
		
		} else {
		
			amPm = 0;
		}
	}
	
	public void arrangeTime() { // saving the date modified
				
		int setHour = 0;
		int setMin;
		
		if(amPm == 0) {
			
			if(hour != 12) setHour = hour - currHour;
			else setHour -= currHour;
					
		} else {
			
			if(hour != 12) setHour = (hour + 12) - currHour;
			else setHour = hour - currHour;
		}
		setMin = minute - currMinute;
		
		calendar.add(Calendar.MINUTE, setMin);
		calendar.add(Calendar.HOUR_OF_DAY, setHour);;
	}
	
	public void setTime() {
		
		SystemClock.setCurrentTimeMillis(calendar.getTimeInMillis());
	}
		
	public String getStrAmpm() {
		
		if(amPm != 0) return "PM";
		else return "AM";
	}
	
	public String getStrHour() {
		
		return Integer.toString(hour);
	}
	
	public String getStrMinute() {
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		return dfm.format(minute);
	}
	
	public void getCurrTime() {
		
		hour = calendar.get(Calendar.HOUR);
		
		if(calendar.get(Calendar.AM_PM) != 0) {
			
			amPm = 1;
			currHour = hour + 12;
		} else {
			
			amPm = 0;
			currHour = hour;
		}
		
		if(hour == 0) hour = 12;
		
		minute  = calendar.get(Calendar.MINUTE);
		currMinute = minute;
	}
	
	public void savingTime() {
		
		SavingTime mSavingTime = new SavingTime();
		mSavingTime.start();
		
		try {
			
			mSavingTime.join();
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	
	public class SavingTime extends Thread {
		
		public void run() {
			
			SerialPort.Sleep(500);
		}
	}
}
