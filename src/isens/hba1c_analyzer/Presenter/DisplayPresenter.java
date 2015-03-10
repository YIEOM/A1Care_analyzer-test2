package isens.hba1c_analyzer.Presenter;

import android.app.Activity;
import android.content.Context;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.DisplayModel;
import isens.hba1c_analyzer.View.DisplayIView;

public class DisplayPresenter {

	private DisplayIView mDisplayIView;
	private DisplayModel mDisplay;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public DisplayPresenter(DisplayIView view, Activity activity, Context context, int layout) {
		
		mDisplayIView = view;
		mDisplay = new DisplayModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mDisplayIView.setImageId();
		mDisplayIView.setButtonId();
		mDisplayIView.setImage();
		mDisplayIView.setButtonClick();
		
		mTimerDisplay.ActivityParm(activity, layout);
		displayBarGauge(mDisplay.getBrightnessValue());
	}
	
	public void upBrightness() {
		
		int value;
		
		unenabledAllBtn();
		
		value = mDisplay.upBrightnessValue(mDisplay.getBrightnessValue());
		displayBarGauge(value);
		mDisplay.setBrightnessValue(value);

		enableAllBtn();
	}
	
	public void downBrightness() {
		
		int value;
		
		unenabledAllBtn();
		
		value = mDisplay.downBrightnessValue(mDisplay.getBrightnessValue());
		displayBarGauge(value);
		mDisplay.setBrightnessValue(value);
		
		enableAllBtn();
	}
	
	public void displayBarGauge(int Value) {
		
		mDisplayIView.setBarGaugeImage(mDisplay.getBarGauageImage(Value));
	}
	
	public void enableAllBtn() {
		
		mDisplayIView.setButtonState(R.id.backBtn, true);
		mDisplayIView.setButtonState(R.id.minusBtn, true);
		mDisplayIView.setButtonState(R.id.plusBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mDisplayIView.setButtonState(R.id.backBtn, false);
		mDisplayIView.setButtonState(R.id.minusBtn, false);
		mDisplayIView.setButtonState(R.id.plusBtn, false);
	}
	
	public void changeActivity() {
		
		mActivityChange.whichIntent(TargetIntent.SystemSetting);
		mActivityChange.finish();
	}
}
