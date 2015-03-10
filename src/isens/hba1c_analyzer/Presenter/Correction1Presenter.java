package isens.hba1c_analyzer.Presenter;

import android.app.Activity;
import android.content.Context;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.FactorModel;
import isens.hba1c_analyzer.View.FactorIView;

public class Correction1Presenter {
	
	private FactorIView mFactorIView;
	private FactorModel mFactorModel;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public Correction1Presenter(FactorIView view, Activity activity, Context context, int layout) {
		
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
		
		fct1st = mFactorModel.getStrFactor(RunActivity.RF1_Slope);
		fct2nd = mFactorModel.getStrFactor(RunActivity.RF1_Offset);
		
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
		
		mFactorModel.setFactor((int) FactorModel.CORRECTION_FACTOR1, "RF1 SlopeVal", fct1st, "RF1 OffsetVal", fct2nd);
		
		mActivityChange.whichIntent(TargetIntent.Maintenance);
		mActivityChange.finish();
	}
}
