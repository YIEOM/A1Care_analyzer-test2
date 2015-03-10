package isens.hba1c_analyzer.Model;

import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;

public class DateModel {

	public final static byte YEAR_UP	 = 1,
			  		  		 YEAR_DOWN  = 2, 
			  		  		 MONTH_UP   = 3, 
			  		  		 MONTH_DOWN = 4,  
			  		  		 DAY_UP     = 5,
			  		  		 DAY_DOWN   = 6;	

	final static int MAX_YEAR = 2035,
	 		 		 MIN_YEAR = 2000;
	
	private Activity activity;
	private Calendar calendar;
	
	public DateModel(Activity activity) {
		
		this.activity = activity;
		calendar = Calendar.getInstance();
	}
	
	public void changeDate(int mode) {
		
		int year, month, day;
		
		year = getCurrYear();
		month = getCurrMonth();
		day = getCurrDay();
		
		switch(mode) {
		
		case YEAR_UP	:
			if(year < MAX_YEAR) calendar.add(Calendar.YEAR, 1);
			break;
			
		case YEAR_DOWN	:
			if(year > MIN_YEAR) calendar.add(Calendar.YEAR, -1);
			break;
		
		case MONTH_UP	:
			if((year != MAX_YEAR) || (month != 12)) calendar.add(Calendar.MONTH, 1);
			break;
			
		case MONTH_DOWN	:
			if((year != MIN_YEAR) || (month != 1)) calendar.add(Calendar.MONTH, -1);
			break;
		
		case DAY_UP		:
			if((year != MAX_YEAR) || (month != 12) || (day != 31)) calendar.add(Calendar.DAY_OF_MONTH, 1);
			break;
			
		case DAY_DOWN	:
			if((year != MIN_YEAR) || (month != 1) || (day != 1)) calendar.add(Calendar.DAY_OF_MONTH, -1);
			break;
			
		default		:
			break;
		}
	}
	
	public int getCurrYear() {

		return calendar.get(Calendar.YEAR);
	}
	
	public int getCurrMonth() {

		return calendar.get(Calendar.MONTH) + 1;
	}
	
	public int getCurrDay() {

		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public String getStrYear() {
		
		return Integer.toString(getCurrYear());
	}
	
	public String getStrMonth() {
		
		return Integer.toString(getCurrMonth());
	}

	public String getStrDay() {
		
		return Integer.toString(getCurrDay());
	}
	
	public void setDate() {

		SystemClock.setCurrentTimeMillis(calendar.getTimeInMillis());
	}
	
	public void savingDate() {
		
		SavingDate mSavingDate = new SavingDate();
		mSavingDate.start();
		
		try {
			
			mSavingDate.join();
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}
	
	public class SavingDate extends Thread {
		
		public void run() {
			
			SerialPort.Sleep(500);
		}
	}
}
