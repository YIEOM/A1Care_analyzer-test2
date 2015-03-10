package isens.hba1c_analyzer.Presenter;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.DateModel;
import isens.hba1c_analyzer.Model.TimeModel;
import isens.hba1c_analyzer.View.DateIView;
import isens.hba1c_analyzer.View.DisplayIView;
import isens.hba1c_analyzer.View.TimeIView;

public class TimePresenter {
	
	private TimeIView mTimeIView;
	private TimeModel mTimeModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;

	private Handler handler = new Handler();
	private TimerTask oneHundredmsPeriod;
	private Timer timer;
	
	public TimePresenter(TimeIView view, Activity activity, Context context, int layout) {
		
		mTimeIView = view;
		mTimeModel = new TimeModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mTimeIView.setImageId();
		mTimeIView.setButtonId();
		mTimeIView.setTextId();
		mTimeIView.setImage();
		mTimeIView.setButtonClick();
		mTimeIView.setButtonLongClick();
		
		mTimeModel.getCurrTime();
		display();
		
		mTimerDisplay.ActivityParm(activity, layout);
	}
	
	public void initTimer(final int mode) {
		
		oneHundredmsPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
		
						mTimeModel.changeTime(mode);
						display();
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(oneHundredmsPeriod, 0, 100); // Timer period : 100msec
	}
	
	public void cancelTimer() {
		
		if(timer != null) timer.cancel();
	}
	
	public void changeAmPm() {
		
		unenabledAllBtn();
		
		mTimeModel.changeAmPm();
		
		display();
		
		enableAllBtn();
	}

	public void changeHourUp() {
		
		unenabledAllBtn();
		
		mTimeModel.changeTime((int) TimeModel.HOUR_UP);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeHourAutoUp() {
		
		mTimeIView.setButtonState(R.id.val2ndpBtn, true);
		
		initTimer((int) TimeModel.HOUR_UP);
	}

	public void changeHourDown() {
		
		unenabledAllBtn();
		
		mTimeModel.changeTime((int) TimeModel.HOUR_DOWN);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeHourAutoDown() {
		
		mTimeIView.setButtonState(R.id.val2ndmBtn, true);
		
		initTimer((int) TimeModel.HOUR_DOWN);
	}

	public void changeMinuteUp() {
		
		unenabledAllBtn();
		
		mTimeModel.changeTime((int) TimeModel.MINUTE_UP);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeMinuteAutoUp() {
		
		mTimeIView.setButtonState(R.id.val3rdpBtn, true);
		
		initTimer((int) TimeModel.MINUTE_UP);
	}

	public void changeMinuteDown() {
		
		unenabledAllBtn();
		
		mTimeModel.changeTime((int) TimeModel.MINUTE_DOWN);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeMinuteAutoDown() {
		
		mTimeIView.setButtonState(R.id.val3rdmBtn, true);
		
		initTimer((int) TimeModel.MINUTE_DOWN);
	}
	
	public void display() {
		
		String amPm, hour, minute;
		
		amPm = mTimeModel.getStrAmpm();
		hour = mTimeModel.getStrHour();
		minute = mTimeModel.getStrMinute();
		mTimeIView.setText(amPm, hour, minute);
	}
	
	public void enableAllBtn() {

		mTimeIView.setButtonState(R.id.backBtn, true);
		mTimeIView.setButtonState(R.id.val1stpBtn, true);
		mTimeIView.setButtonState(R.id.val1stmBtn, true);
		mTimeIView.setButtonState(R.id.val2ndpBtn, true);
		mTimeIView.setButtonState(R.id.val2ndmBtn, true);
		mTimeIView.setButtonState(R.id.val3rdpBtn, true);
		mTimeIView.setButtonState(R.id.val3rdmBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mTimeIView.setButtonState(R.id.backBtn, false);
		mTimeIView.setButtonState(R.id.val1stpBtn, false);
		mTimeIView.setButtonState(R.id.val1stmBtn, false);
		mTimeIView.setButtonState(R.id.val2ndpBtn, false);
		mTimeIView.setButtonState(R.id.val2ndmBtn, false);
		mTimeIView.setButtonState(R.id.val3rdpBtn, false);
		mTimeIView.setButtonState(R.id.val3rdmBtn, false);
	}
	
	public void changeActivity() {
		
		TimerDisplay.FiftymsPeriod.cancel();
		mTimeModel.arrangeTime();
		mTimeModel.setTime();
		mTimerDisplay.TimerInit();
		mTimeModel.savingTime();
		
		mActivityChange.whichIntent(TargetIntent.SystemSetting);
		mActivityChange.finish();
	}
}
