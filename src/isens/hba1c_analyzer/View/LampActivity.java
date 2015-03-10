package isens.hba1c_analyzer.View;

import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.ErrorPopup;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.MaintenanceActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.TimerDisplay;
import isens.hba1c_analyzer.CalibrationActivity.TargetMode;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.drawable;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import isens.hba1c_analyzer.R.string;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.RunActivity.Cart1stFilter2;
import isens.hba1c_analyzer.RunActivity.CartDump;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.Presenter.LampPresenter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class LampActivity extends Activity implements LampIView{
	
	private LampPresenter mLampPresenter;
	
	public TextView adcText;

	public ImageView stateFlag1, stateFlag2;
	
	public Button escBtn, runBtn, cancelBtn, darkBtn, f535nmBtn, f660nmBtn, f750nmBtn;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.lamp);
		
		mLampPresenter = new LampPresenter(this, this, this, R.layout.lamp);
		mLampPresenter.init();
	}
	
	public void setImageId() {
		
		stateFlag1 = (ImageView) findViewById(R.id.stateFlag1);
		stateFlag2 = (ImageView) findViewById(R.id.stateFlag2);
	}
	
	public void setImageBgColor(String color) {
		
		stateFlag1.setBackgroundColor(Color.parseColor(color));
		stateFlag2.setBackgroundColor(Color.parseColor(color));
	}
	
	public void setTextId() {
		
		adcText = (TextView) findViewById(R.id.adcText);
	}
	
	public void setText(String value) {
		
		adcText.setText(value);
	}
	
	public void setButtonId() {
		
		escBtn = (Button)findViewById(R.id.escBtn);
		runBtn = (Button)findViewById(R.id.runBtn);
		cancelBtn = (Button)findViewById(R.id.cancelBtn);
		darkBtn = (Button)findViewById(R.id.darkBtn);
		f535nmBtn = (Button)findViewById(R.id.f535nmBtn);
		f660nmBtn = (Button)findViewById(R.id.f660nmBtn);
		f750nmBtn = (Button)findViewById(R.id.f750nmBtn);
	}
	
	public void setButtonClick() {
		
		escBtn.setOnTouchListener(mTouchListener);
		runBtn.setOnTouchListener(mTouchListener);
		cancelBtn.setOnTouchListener(mTouchListener);
		darkBtn.setOnTouchListener(mTouchListener);
		f535nmBtn.setOnTouchListener(mTouchListener);
		f660nmBtn.setOnTouchListener(mTouchListener);
		f750nmBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonBg(int dark, int f535nm, int f660nm, int f750nm) {
		
		darkBtn.setBackgroundResource(dark);
		f535nmBtn.setBackgroundResource(f535nm);
		f660nmBtn.setBackgroundResource(f660nm);
		f750nmBtn.setBackgroundResource(f750nm);
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
			
				case R.id.escBtn	:
					mLampPresenter.changeActivity();
					break;
					
				case R.id.runBtn	:
					break;
					
				case R.id.cancelBtn	:
					break;
					
				case R.id.darkBtn	:
					break;
					
				case R.id.f535nmBtn	:
					break;
					
				case R.id.f660nmBtn	:
					break;
					
				case R.id.f750nmBtn	:
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
