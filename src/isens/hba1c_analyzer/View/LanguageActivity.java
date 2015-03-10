package isens.hba1c_analyzer.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SystemSettingActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import isens.hba1c_analyzer.R.string;
import isens.hba1c_analyzer.Presenter.LanguagePresenter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LanguageActivity extends Activity implements LanguageIView {
		
	private LanguagePresenter mLanguagePresenter;
	
	private TextView languageText;

	private ImageView titleImage, iconImage;
	
	private Button backBtn, leftBtn, rightBtn;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting4);
		
		mLanguagePresenter = new LanguagePresenter(this, this, this, R.layout.setting4);
		mLanguagePresenter.init();
	}
	
	public void setImageId() {
		
		titleImage = (ImageView) findViewById(R.id.title);
		iconImage = (ImageView) findViewById(R.id.icon);
	}
	
	public void setImage() {
		
		titleImage.setBackgroundResource(R.drawable.language_title);
		iconImage.setBackgroundResource(R.drawable.language);
	}
	
	public void setTextId() {
		
		languageText = (TextView) findViewById(R.id.mainText);
	}
	
	public void setText(int language) {
		
		languageText.setText(language);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		leftBtn = (Button)findViewById(R.id.leftBtn);
		rightBtn = (Button)findViewById(R.id.rightBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		leftBtn.setOnTouchListener(mTouchListener);
		rightBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}

	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_DOWN	:
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mLanguagePresenter.changeActivity();
					break;
					
				case R.id.leftBtn	:
					mLanguagePresenter.changeLanguageDown();
					break;
					
				case R.id.rightBtn	:
					mLanguagePresenter.changeLanguageUp();
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
}
