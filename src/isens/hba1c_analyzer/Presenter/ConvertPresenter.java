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
import isens.hba1c_analyzer.Model.ConvertModel;
import isens.hba1c_analyzer.Model.DateModel;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.View.ConvertIView;
import isens.hba1c_analyzer.View.DateIView;
import isens.hba1c_analyzer.View.DisplayIView;
import isens.hba1c_analyzer.View.LanguageIView;

public class ConvertPresenter {
	
	private ConvertIView mConvertIView;
	private ConvertModel mConvertModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public ConvertPresenter(ConvertIView view, Activity activity, Context context, int layout) {
		
		mConvertIView = view;
		mConvertModel = new ConvertModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mConvertIView.setImageId();
		mConvertIView.setImage();
		mConvertIView.setButtonId();
		mConvertIView.setTextId();
		mConvertIView.setButtonClick();
		
		mConvertModel.initPrimary();
		display();
		
		mTimerDisplay.ActivityParm(activity, layout);
	}
	
	public void changePrimaryUp() {
		
		unenabledAllBtn();
		
		mConvertModel.upPrimaryIdx();
		
		display();
		
		enableAllBtn();
	}

	public void changePrimaryDown() {
		
		unenabledAllBtn();
		
		mConvertModel.downPrimaryIdx();
		
		display();
		
		enableAllBtn();
	}
	
	public void display() {
		
		int primary;
		
		primary = mConvertModel.getPrimary();
		mConvertIView.setText(primary);
	}
	
	public void enableAllBtn() {

		mConvertIView.setButtonState(R.id.backBtn, true);
		mConvertIView.setButtonState(R.id.leftBtn, true);
		mConvertIView.setButtonState(R.id.rightBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mConvertIView.setButtonState(R.id.backBtn, false);
		mConvertIView.setButtonState(R.id.leftBtn, false);
		mConvertIView.setButtonState(R.id.rightBtn, false);
	}
	
	public void changeActivity() {
		
		mConvertModel.setPrimary();
		
		mActivityChange.whichIntent(TargetIntent.SystemSetting);
		mActivityChange.finish();
	}
}
