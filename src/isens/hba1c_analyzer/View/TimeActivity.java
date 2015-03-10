package isens.hba1c_analyzer.View;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.SystemSettingActivity;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Presenter.DatePresenter;
import isens.hba1c_analyzer.Presenter.TimePresenter;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.drawable;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TimeActivity extends Activity implements TimeIView{
	
	private TimePresenter mTimePresenter;
	
	private TextView amPmText, hourText, minuteText;

	private Button backBtn, amPmPBtn, amPmMBtn, hourPBtn, hourMBtn, minutePBtn, minuteMBtn;

	private ImageView titleImage, iconImage;
	
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.datetime);
		
		mTimePresenter = new TimePresenter(this, this, this, R.layout.datetime);
		mTimePresenter.init();
	}
	
	public void setImageId() {
		
		titleImage = (ImageView) findViewById(R.id.title);
		iconImage = (ImageView) findViewById(R.id.icon);
	}
	
	public void setImage() {
		
		titleImage.setBackgroundResource(R.drawable.time_title);
		iconImage.setBackgroundResource(R.drawable.time);
	}
	
	public void setTextId() {
		
		amPmText = (TextView) findViewById(R.id.val1stText);
		hourText = (TextView) findViewById(R.id.val2ndText);
		minuteText = (TextView) findViewById(R.id.val3rdText);
	}
	
	public void setText(String amPm, String hour, String minute) {
		
		amPmText.setText(amPm);
		hourText.setText(hour);
		minuteText.setText(minute);
	}

	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		amPmPBtn = (Button) findViewById(R.id.val1stpBtn);
		amPmMBtn = (Button) findViewById(R.id.val1stmBtn);
		hourPBtn = (Button) findViewById(R.id.val2ndpBtn);
		hourMBtn = (Button) findViewById(R.id.val2ndmBtn);
		minutePBtn = (Button) findViewById(R.id.val3rdpBtn);
		minuteMBtn = (Button) findViewById(R.id.val3rdmBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		amPmPBtn.setOnTouchListener(mTouchListener);
		amPmMBtn.setOnTouchListener(mTouchListener);
		hourPBtn.setOnTouchListener(mTouchListener);
		hourMBtn.setOnTouchListener(mTouchListener);
		minutePBtn.setOnTouchListener(mTouchListener);
		minuteMBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonLongClick() {
		
		amPmPBtn.setOnLongClickListener(mLongClickListener);
		amPmMBtn.setOnLongClickListener(mLongClickListener);
		hourPBtn.setOnLongClickListener(mLongClickListener);
		hourMBtn.setOnLongClickListener(mLongClickListener);
		minutePBtn.setOnLongClickListener(mLongClickListener);
		minuteMBtn.setOnLongClickListener(mLongClickListener);
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
					
					case R.id.backBtn		:
						break;
						
					case R.id.val1stpBtn	:
						mTimePresenter.changeAmPm();
						break;
						
					case R.id.val1stmBtn	:
						mTimePresenter.changeAmPm();
						break;
						
					case R.id.val2ndpBtn	:
						mTimePresenter.changeHourUp();
						break;
						
					case R.id.val2ndmBtn	:
						mTimePresenter.changeHourDown();
						break;
						
					case R.id.val3rdpBtn	:
						mTimePresenter.changeMinuteUp();
						break;
						
					case R.id.val3rdmBtn	:
						mTimePresenter.changeMinuteDown();
						break;
						
					default	:
						break;
					}	
					
					break;

				case MotionEvent.ACTION_UP		:
					
					switch(v.getId()) {
					
					case R.id.backBtn		:
						mTimePresenter.changeActivity();
						break;
						
					default	:
						mTimePresenter.cancelTimer();
						break;
					}	
					
					break;
			}
			
			return false;
		}
	};
	
	Button.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
		
		public boolean onLongClick(View v) {
			
			switch(v.getId()) {
			
			case R.id.val2ndpBtn	:
				mTimePresenter.changeHourAutoUp();
				break;
				
			case R.id.val2ndmBtn	:
				mTimePresenter.changeHourAutoDown();
				break;
				
			case R.id.val3rdpBtn	:
				mTimePresenter.changeMinuteAutoUp();
				break;
				
			case R.id.val3rdmBtn	:
				mTimePresenter.changeMinuteAutoDown();
				break;
				
			default	:
				break;
			}
			
			return false;
		}
	};
}
