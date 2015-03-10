package isens.hba1c_analyzer;

import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.CalibrationActivity.TargetMode;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	public OperatorController mOperatorController;
	
	public Button systemBtn,
				  dataBtn,
				  operatorBtn,
				  backIcon;
	
	public boolean btnState = false;
	
	public Handler handler = new Handler();
	public TimerTask OneHundredmsPeriod;
	public Timer timer;
	
	public Button cheat1Btn,
				  cheat2Btn;

	public Button btn;
	
	public static boolean isC1Pressed = false,
						  isC2Pressed = false;
	
	public boolean isC1Running = true;
	public boolean isCheat = false;
	public int cnt;
	public int longClickTime;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting);
		
		SettingInit();
		
		/*System setting Activity activation*/
		systemBtn = (Button)findViewById(R.id.systembtn);
		systemBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					systemBtn.setEnabled(false);
					
					WhichIntent(TargetIntent.SystemSetting);
				}
			}
		});
		
		/*Operator setting Activity activation*/
		operatorBtn = (Button)findViewById(R.id.operatorbtn);
		operatorBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				/* */
//				if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) {
				
				if(!btnState) {
				
					btnState = true;
					
					operatorBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.OperatorSetting);
				}
				
//				}
				
				/* */
			}
		});
		
		/*Home Activity activation*/
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					backIcon.setEnabled(false);
				
					WhichIntent(TargetIntent.Home);
				}
			}
		});
		
		cheat1Btn = (Button)findViewById(R.id.cheat1btn);
		cheat1Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				Cheat1stModeStart();
			}
		});
		
		cheat2Btn = (Button)findViewById(R.id.cheat2btn);
		cheat2Btn.setOnTouchListener(new View.OnTouchListener() {
				
			public boolean onTouch(View v, MotionEvent event) {
				
				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN	:
					PressedBtn();
					break;
					
				case MotionEvent.ACTION_UP		:
					TakeOffBtn();
					break;
					
				default	:
					break;
				}
				
				return false;
			}
		});	
	}
	
	public void SettingInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.settinglayout);
	}
	
	public void PressedBtn() {
		
		if(!isC2Pressed && isC1Pressed) {
			
			isC2Pressed = true;
			isCheat = false;
			longClickTime = cnt;
		}
	}
	
	public void TakeOffBtn() {
		
		CheatModeStop(this);
	}
	
	public void TimerInit() {
		
		OneHundredmsPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						cnt++;
						
						if(!isC2Pressed) CheatMode();
						else Cheat2ndModeStart();
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(OneHundredmsPeriod, 0, 100); // Timer period : 100msec
	}
	
	public void Cheat1stModeStart() {
		
		if(!isC1Pressed) {
			
			Log.w("Cheat mode", "1st start");
			
			BtnColor(this, R.id.cheat1btn, "#3004A458");
		
			isC1Pressed = true;
			
			cnt = 0;
			
			TimerInit();
		}
	}
	
	public void Cheat2ndModeStart() {
		
		int hundredmsCnt = cnt - longClickTime;
		
		BtnColor(this, R.id.cheat2btn, "#30023894");
		
		if(hundredmsCnt > 30) {
			
			Log.w("Cheat mode", "2nd start");
			
			isCheat = true;
			
			timer.cancel();
			
			mOperatorController = new OperatorController(this, this, R.id.settinglayout);
			mOperatorController.EngineerLoginDisplay();
		}
	}
	
	public void CheatModeStop(Activity activity) {
		
		if(isC1Pressed) {
			
			Log.w("Cheat mode", "1st stop");
			
			BtnColor(activity, R.id.cheat1btn, "#00000000");
		
			isC1Pressed = false;
			
			if(!isCheat) timer.cancel();
		}
		
		if(isC2Pressed) {
			
			Log.w("Cheat mode", "2nd stop");
			
			BtnColor(activity, R.id.cheat2btn, "#00000000");
			
			isC2Pressed = false;
		}
	}
	
	public void CheatMode() {
		
		if(cnt == 50) {
			
			CheatModeStop(this);
		}
	}
	
	public void BtnColor(final Activity activity, final int id, final String color) {
		
		new Thread(new Runnable() {			
			public void run() {    
				runOnUiThread(new Runnable(){
					public void run() {

						btn = (Button) activity.findViewById(id);
 	
						btn.setBackgroundColor(Color.parseColor(color));
					}
				});
			}
		}).start();
	}

	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home				:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
						
		case SystemSetting		:
			nextIntent = new Intent(getApplicationContext(), SystemSettingActivity.class);
			break;
			
		case DataSetting		:
			nextIntent = new Intent(getApplicationContext(), DataSettingActivity.class);
			break;			
			
		case OperatorSetting	:		
			nextIntent = new Intent(getApplicationContext(), OperatorSettingActivity.class);
			break;
			
		default		:	
			break;			
		}
		
		startActivity(nextIntent);
		finish(this);		
	}
	
	public void MaintenanceIntent(Activity activity, Context context) { // Activity conversion
		
		isC1Pressed = false;
		isC2Pressed = false;
		
		Intent MaintenanceIntent = new Intent(context, MaintenanceActivity.class);
		activity.startActivity(MaintenanceIntent);
		
		finish(activity);		
	}
	
	public void finish(Activity activity) {
		
		super.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
