package isens.hba1c_analyzer.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SystemSettingActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Presenter.ConvertPresenter;
import isens.hba1c_analyzer.Presenter.LanguagePresenter;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import isens.hba1c_analyzer.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ConvertActivity extends Activity implements ConvertIView{
	
	private ConvertPresenter mConvertPresenter;
	
	private TextView convertText;

	private ImageView titleImage, iconImage;
	
	private Button backBtn, leftBtn, rightBtn;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting4);
		
		mConvertPresenter = new ConvertPresenter(this, this, this, R.layout.setting4);
		mConvertPresenter.init();
	}
	
	public void setImageId() {
		
		titleImage = (ImageView) findViewById(R.id.title);
		iconImage = (ImageView) findViewById(R.id.icon);
	}
	
	public void setImage() {
		
		titleImage.setBackgroundResource(R.drawable.convert_title);
		iconImage.setBackgroundResource(R.drawable.convert_icon);
	}
	
	public void setTextId() {
		
		convertText = (TextView) findViewById(R.id.mainText);
	}
	
	public void setText(int language) {
		
		convertText.setText(language);
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
					mConvertPresenter.changeActivity();
					break;
					
				case R.id.leftBtn	:
					mConvertPresenter.changePrimaryDown();
					break;
					
				case R.id.rightBtn	:
					mConvertPresenter.changePrimaryUp();
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
