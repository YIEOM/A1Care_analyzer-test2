package isens.hba1c_analyzer.Presenter;

import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ActivityChange;
import isens.hba1c_analyzer.Model.SoundModel;
import isens.hba1c_analyzer.View.SoundIView;
import android.app.Activity;
import android.content.Context;

public class SoundPresenter {

	private SoundIView mSoundIView;
	private SoundModel mSound;
	private TimerDisplay mTimerDisplay;
	private ActivityChange mActivityChange;
	
	private Activity activity;
	private Context context;
	private int layout;
	
	public SoundPresenter(SoundIView view, Activity activity, Context context, int layout) {
		
		mSoundIView = view;
		mSound = new SoundModel(activity, context);
		mTimerDisplay = new TimerDisplay();
		mActivityChange = new ActivityChange(activity, context);
		
		this.activity = activity;
		this.context = context;
		this.layout = layout;
	}
	
	public void init() {
		
		mSoundIView.setImageId();
		mSoundIView.setButtonId();
		mSoundIView.setImage();
		mSoundIView.setButtonClick();
		
		mTimerDisplay.ActivityParm(activity, layout);
		displayBarGauge(mSound.getSoundVolume());
	}
	
	public void upSound() {
		
		int volume;
		
		unenabledAllBtn();
		
		volume = mSound.upSoundVolume(mSound.getSoundVolume());
		displayBarGauge(volume);
		mSound.setSoundVolume(volume);
		
		enableAllBtn();
	}
	
	public void downSound() {
		
		int volume;
		
		unenabledAllBtn();
		
		volume = mSound.downSoundVolume(mSound.getSoundVolume());
		displayBarGauge(volume);
		mSound.setSoundVolume(volume);

		enableAllBtn();
	}
	
	public void displayBarGauge(int volume) {
		
		mSoundIView.setBarGaugeImage(mSound.getBarGauageImage(volume));
	}
	
	public void enableAllBtn() {
		
		mSoundIView.setButtonState(R.id.backBtn, true);
		mSoundIView.setButtonState(R.id.minusBtn, true);
		mSoundIView.setButtonState(R.id.plusBtn, true);
	}
	
	public void unenabledAllBtn() {
		
		mSoundIView.setButtonState(R.id.backBtn, false);
		mSoundIView.setButtonState(R.id.minusBtn, false);
		mSoundIView.setButtonState(R.id.plusBtn, false);
	}
	
	public void changeActivity() {
		
		mActivityChange.whichIntent(TargetIntent.SystemSetting);
		mActivityChange.finish();
	}
}
