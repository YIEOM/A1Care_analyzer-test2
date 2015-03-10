package isens.hba1c_analyzer.Presenter;

import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.View.LampIView;
import android.app.Activity;
import android.content.Context;

public class LampPresenter {

	private LampIView mLampIView;
//	private LampModel mLampModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public LampPresenter(LampIView view, Activity activity, Context context, int layout) {
		
		mLampIView = view;
//		mLampModel = new LampModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mLampIView.setImageId();
		mLampIView.setTextId();
		mLampIView.setButtonId();
		mLampIView.setImageBgColor("#000000");
		mLampIView.setText("");
		mLampIView.setButtonClick();
		
//		display();
		
		mTimerDisplay.ActivityParm(activity, layout);
	}
	
	public void changeActivity() {
		
		mActivityChange.whichIntent(TargetIntent.Maintenance);
		mActivityChange.finish();
	}
}
