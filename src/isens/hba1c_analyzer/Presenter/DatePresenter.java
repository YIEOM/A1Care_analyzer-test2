package isens.hba1c_analyzer.Presenter;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Button;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.DateModel;
import isens.hba1c_analyzer.View.DateIView;
import isens.hba1c_analyzer.View.DisplayIView;

public class DatePresenter {
	
	private DateIView mDateIView;
	private DateModel mDateModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	private Handler handler = new Handler();
	private TimerTask oneHundredmsPeriod;
	private Timer timer;	
	
	public DatePresenter(DateIView view, Activity activity, Context context, int layout) {
		
		mDateIView = view;
		mDateModel = new DateModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mDateIView.setImageId();
		mDateIView.setButtonId();
		mDateIView.setTextId();
		mDateIView.setImage();
		mDateIView.setButtonClick();
		mDateIView.setButtonLongClick();
		
		display();
		
		mTimerDisplay.ActivityParm(activity, layout);
	}
	
	public void initTimer(final int mode) {
		
		oneHundredmsPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
		
						mDateModel.changeDate(mode);
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
		
	public void changeYearUp() {
		
		unenabledAllBtn();
		
		mDateModel.changeDate((int) DateModel.YEAR_UP);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeYearAutoUp() {
		
		mDateIView.setButtonState(R.id.val1stpBtn, true);
		
		initTimer((int) DateModel.YEAR_UP);
	}
	
	public void changeYearDown() {
		
		unenabledAllBtn();
		
		mDateModel.changeDate((int) DateModel.YEAR_DOWN);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeYearAutoDown() {
		
		mDateIView.setButtonState(R.id.val1stmBtn, true);
		
		initTimer((int) DateModel.YEAR_DOWN);
	}

	public void changeMonthUp() {
		
		unenabledAllBtn();
		
		mDateModel.changeDate((int) DateModel.MONTH_UP);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeMonthAutoUp() {
		
		mDateIView.setButtonState(R.id.val2ndpBtn, true);
		
		initTimer((int) DateModel.MONTH_UP);
	}

	public void changeMonthDown() {
		
		unenabledAllBtn();
		
		mDateModel.changeDate((int) DateModel.MONTH_DOWN);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeMonthAutoDown() {
		
		mDateIView.setButtonState(R.id.val2ndmBtn, true);
		
		initTimer((int) DateModel.MONTH_DOWN);
	}

	public void changeDayUp() {
		
		unenabledAllBtn();
		
		mDateModel.changeDate((int) DateModel.DAY_UP);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeDayAutoUp() {
		
		mDateIView.setButtonState(R.id.val3rdpBtn, true);
		
		initTimer((int) DateModel.YEAR_UP);
	}

	public void changeDayDown() {
		
		unenabledAllBtn();
		
		mDateModel.changeDate((int) DateModel.DAY_DOWN);
		
		display();
		
		enableAllBtn();
	}
	
	public void changeDayAutoDown() {
		
		mDateIView.setButtonState(R.id.val3rdmBtn, true);
		
		initTimer((int) DateModel.DAY_DOWN);
	}

	public void display() {
		
		String year, month, day;
		
		year = mDateModel.getStrYear();
		month = mDateModel.getStrMonth();
		day = mDateModel.getStrDay();
		mDateIView.setText(year, month, day);
	}
	
	public void enableAllBtn() {

		mDateIView.setButtonState(R.id.backBtn, true);
		mDateIView.setButtonState(R.id.val1stpBtn, true);
		mDateIView.setButtonState(R.id.val1stmBtn, true);
		mDateIView.setButtonState(R.id.val2ndpBtn, true);
		mDateIView.setButtonState(R.id.val2ndmBtn, true);
		mDateIView.setButtonState(R.id.val3rdpBtn, true);
		mDateIView.setButtonState(R.id.val3rdmBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mDateIView.setButtonState(R.id.backBtn, false);
		mDateIView.setButtonState(R.id.val1stpBtn, false);
		mDateIView.setButtonState(R.id.val1stmBtn, false);
		mDateIView.setButtonState(R.id.val2ndpBtn, false);
		mDateIView.setButtonState(R.id.val2ndmBtn, false);
		mDateIView.setButtonState(R.id.val3rdpBtn, false);
		mDateIView.setButtonState(R.id.val3rdmBtn, false);
	}
	
	public void changeActivity() {
		
		TimerDisplay.FiftymsPeriod.cancel();
		mDateModel.setDate();
		mTimerDisplay.TimerInit();
		mDateModel.savingDate();
		
		mActivityChange.whichIntent(TargetIntent.SystemSetting);
		mActivityChange.finish();
	}
}
