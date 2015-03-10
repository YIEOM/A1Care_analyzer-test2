package isens.hba1c_analyzer.Presenter;

import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Button;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.ConvertModel;
import isens.hba1c_analyzer.Model.FactorModel;
import isens.hba1c_analyzer.Model.DateModel;
import isens.hba1c_analyzer.Model.LanguageModel;
import isens.hba1c_analyzer.View.ConvertIView;
import isens.hba1c_analyzer.View.FactorIView;
import isens.hba1c_analyzer.View.DateIView;
import isens.hba1c_analyzer.View.DisplayIView;
import isens.hba1c_analyzer.View.LanguageIView;

public class AbsorbancePresenter {
	
	private FactorIView mFactorIView;
	private FactorModel mFactorModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public AbsorbancePresenter(FactorIView view, Activity activity, Context context, int layout) {
		
		mFactorIView = view;
		mFactorModel = new FactorModel(activity);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mFactorIView.setImageId();
		mFactorIView.setImage();
		mFactorIView.setButtonId();
		mFactorIView.setEditTextId();
		mFactorIView.setButtonClick();
		
		display();
		
		mTimerDisplay.ActivityParm(activity, layout);
	}
	
	public void display() {
		
		String fct1st, fct2nd;
		
		fct1st = mFactorModel.getStrFactor(RunActivity.SF_F1);
		fct2nd = mFactorModel.getStrFactor(RunActivity.SF_F2);
		
		mFactorIView.setEditText(fct1st, fct2nd);
	}
	
	public void enableAllBtn() {

		mFactorIView.setButtonState(R.id.backBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mFactorIView.setButtonState(R.id.backBtn, false);
	}
	
	public void changeActivity() {
		
		float fct1st, fct2nd;
		
		fct1st = mFactorIView.getFactor1st();
		fct2nd = mFactorIView.getFactor2nd();
		
		mFactorModel.setFactor((int) FactorModel.ABSORBANCE_FACTOR, "SF Fct1stVal", fct1st, "SF Fct2ndVal", fct2nd);
		
		mActivityChange.whichIntent(TargetIntent.Maintenance);
		mActivityChange.finish();
	}
}
