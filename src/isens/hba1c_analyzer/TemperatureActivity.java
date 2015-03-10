package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class TemperatureActivity extends Activity {
	
	public Temperature mTemperature;
	public TimerDisplay mTimerDisplay;
	
	public Handler runHandler = new Handler();
	public Timer runningTimer;
	
	public Button escBtn,
				  setBtn;	
	
	public TextView chamTmpText,
					ambTmpText;
	
	public EditText tmpEText;
		
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temperature);
		
		tmpEText = (EditText) findViewById(R.id.tmpetext);
		
		/*System setting Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				escBtn.setEnabled(false);
				
				WhichIntent(TargetIntent.Maintenance);
			}
		});
		
		setBtn = (Button)findViewById(R.id.setbtn);
		setBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				setBtn.setEnabled(false);
				
				TmpSave(Float.valueOf(tmpEText.getText().toString()).floatValue());
			}
		});
		
		TemperatureInit();
	}
	
	public void TemperatureInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.temperaturelayout);
		
		tmpEText.setText(Float.toString(Temperature.InitTmp));
		chamTmpText = (TextView)findViewById(R.id.chamTmpText);
		ambTmpText = (TextView)findViewById(R.id.ambTmpText);
		
		RunTimerInit();
	}

	public void TmpSave(float tmp) {
		
		SharedPreferences temperaturePref = getSharedPreferences("Temperature", MODE_PRIVATE);
		SharedPreferences.Editor temperatureedit = temperaturePref.edit();
		
		temperatureedit.putFloat("Cell Block", tmp);
		temperatureedit.commit();
		
		Temperature.InitTmp = tmp;
		
		mTemperature = new Temperature();
		mTemperature.TmpInit();
		
		setBtn.setEnabled(true);
	}
	
	public void TmpDisplay() {
		
		TmpDisplay mTmpDisplay = new TmpDisplay(this);
		mTmpDisplay.start();
	}
	
	public class TmpDisplay extends Thread {
		
		Activity activity;
		
		TmpDisplay(Activity activity) {
			
			this.activity = activity;
		}
		
		public void run() {
			
			final DecimalFormat tmpdfm = new DecimalFormat("0.0");
			final double chamTmp,
						 ambTmp;
			
			mTemperature = new Temperature();
			chamTmp = mTemperature.CellTmpRead();
			ambTmp = mTemperature.AmbTmpRead(); 
			
			new Thread(new Runnable() {
				public void run() {    
					runOnUiThread(new Runnable(){
						public void run(){

							chamTmpText.setText(tmpdfm.format(chamTmp));
							ambTmpText.setText(tmpdfm.format(ambTmp));
						}
					});
				}
			}).start();
		}
	}
	
	public void RunTimerInit() {

		TimerTask FiveHundredmsPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						TmpDisplay();
					}
				};
				
				runHandler.post(updater);		
			}
		};
		
		runningTimer = new Timer();
		runningTimer.schedule(FiveHundredmsPeriod, 0, 500); // Timer period : 500msec
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		runningTimer.cancel();
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Maintenance	:
			nextIntent = new Intent(getApplicationContext(), MaintenanceActivity.class);
			break;
			
		default		:	
			break;			
		}
		
		startActivity(nextIntent);
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}