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
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.View.DateIView;
import isens.hba1c_analyzer.View.DisplayIView;
import isens.hba1c_analyzer.View.LanguageIView;

public class LanguagePresenter {
	
	private LanguageIView mLanguageIView;
	private LanguageModel mLanguageModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public LanguagePresenter(LanguageIView view, Activity activity, Context context, int layout) {
		
		mLanguageIView = view;
		mLanguageModel = new LanguageModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mLanguageIView.setImageId();
		mLanguageIView.setImage();
		mLanguageIView.setButtonId();
		mLanguageIView.setTextId();
		mLanguageIView.setButtonClick();
		
		mLanguageModel.initLanguage();
		display();
		
		mTimerDisplay.ActivityParm(activity, layout);
	}
	
	public void changeLanguageUp() {
		
		unenabledAllBtn();
		
		mLanguageModel.upLanguageIdx();
		
		display();
		
		enableAllBtn();
	}

	public void changeLanguageDown() {
		
		unenabledAllBtn();
		
		mLanguageModel.downLanguageIdx();
		
		display();
		
		enableAllBtn();
	}
	
	public void display() {
		
		int language;
		
		language = mLanguageModel.getLanguage();
		mLanguageIView.setText(language);
	}
	
	public void enableAllBtn() {

		mLanguageIView.setButtonState(R.id.backBtn, true);
		mLanguageIView.setButtonState(R.id.leftBtn, true);
		mLanguageIView.setButtonState(R.id.rightBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mLanguageIView.setButtonState(R.id.backBtn, false);
		mLanguageIView.setButtonState(R.id.leftBtn, false);
		mLanguageIView.setButtonState(R.id.rightBtn, false);
	}
	
	public void changeActivity() {
		
		mLanguageModel.setLocale();
		
		mActivityChange.whichIntent(TargetIntent.SystemSetting);
		mActivityChange.finish();
	}
}
