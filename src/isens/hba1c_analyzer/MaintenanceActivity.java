package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.SerialPort.CtrTarget;
import isens.hba1c_analyzer.Temperature.CellTmpRead;
import isens.hba1c_analyzer.View.AbsorbanceActivity;
import isens.hba1c_analyzer.View.AdjustmentActivity;
import isens.hba1c_analyzer.View.Correction1Activity;
import isens.hba1c_analyzer.View.Correction2Activity;
import isens.hba1c_analyzer.View.LampActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

public class MaintenanceActivity extends Activity {
	
	final static String FW_VERSION =  "QV";
	
	public TimerDisplay mTimerDisplay;
	public SerialPort mSerialPort;
	
	public Button escBtn,
	  			  lampBtn,
	  			  adjustBtn,
	  			  calibrationBtn,
	  			  tempBtn,
	  			  absorbanceBtn,
	  			  correct1Btn,
	  			  correct2Btn;
	
	public TextView swVersionText, fwVersionText;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.maintenance);
		
		swVersionText = (TextView)findViewById(R.id.swVersionText);
		fwVersionText = (TextView)findViewById(R.id.fwVersionText);
		
		MaintenanceInit();
					
		/*Home Activity activation*/
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
				
					escBtn.setEnabled(false);
					
					WhichIntent(TargetIntent.Home);
				}
			}
		});
		
		/*Adjustment Factor Activity activation*/
		adjustBtn = (Button)findViewById(R.id.adjustbtn);
		adjustBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					adjustBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Adjustment);
				}
			}
		});
		
		/*Calibration Activity activation*/
		calibrationBtn = (Button)findViewById(R.id.calibrationbtn);
		calibrationBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					calibrationBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Calibration);
				}
			}
		});
		
		/*Temperature Activity activation*/
		tempBtn = (Button)findViewById(R.id.tempbtn);
		tempBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					tempBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Temperature);
				}
			}
		});
		
		/*Lamp Intensity Activity activation*/
		lampBtn = (Button)findViewById(R.id.lampbtn);
		lampBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					lampBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Lamp);
				}
			}
		});
		
		absorbanceBtn = (Button)findViewById(R.id.absorbancebtn);
		absorbanceBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					lampBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Absorbance);
				}
			}
		});
		
		correct1Btn = (Button)findViewById(R.id.correct1Btn);
		correct1Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					correct1Btn.setEnabled(false);
				
					WhichIntent(TargetIntent.Correction1);
				}
			}
		});

		correct2Btn = (Button)findViewById(R.id.correct2Btn);
		correct2Btn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					correct2Btn.setEnabled(false);
				
					WhichIntent(TargetIntent.Correction2);
				}
			}
		});
	}
	
	public void MaintenanceInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.maintenancelayout);
		
		DisplayVersion mDisplayVersion = new DisplayVersion();
		mDisplayVersion.start();
	}
	
	public class DisplayVersion extends Thread {
		
		String swVersion, fwVersion;
		
		PackageInfo pi = null;
		
		public void run() {
			
			try {

				pi = getPackageManager().getPackageInfo(getPackageName(), 0);

			} catch (NameNotFoundException e) {

				// TODO Auto-generated catch block

				e.printStackTrace();
			}

			swVersion = pi.versionName;
			
			fwVersion = getFwVersion();
			
			new Thread(new Runnable() {
				public void run() {
					runOnUiThread(new Runnable(){
						public void run() {
	
							swVersionText.setText("SW : " + swVersion);
							fwVersionText.setText("FW : " + fwVersion);
						}
					});
				}
			}).start();
		}
	}
	
	public String getFwVersion() {
		
		GetFwVersion mGetFwVersion = new GetFwVersion();
		mGetFwVersion.start();
		
		try {
			mGetFwVersion.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mGetFwVersion.getVersion();
	}
	
	public class GetFwVersion extends Thread {
		
		String version;
		
		public void run() {
			
			String temp = "NR";
			int cnt = 0;
			
			TimerDisplay.RXBoardFlag = true;
			
			mSerialPort = new SerialPort();
			mSerialPort.BoardTx(FW_VERSION, CtrTarget.NormalSet);
			
			do {	
			
				temp = mSerialPort.BoardMessageOutput();

				Log.w("GetFwVersion", "temp : " + temp);
				
				if(cnt++ == 15) {
					
					temp = "Nothing";
					break;
				}
				
				SerialPort.Sleep(100);
			
			} while(temp.equals("NR"));
			
			TimerDisplay.RXBoardFlag = false;
			
			version = temp;
		}
		
		public String getVersion() {
			
			return version;
		}
	}

	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home			:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
						
		case Lamp		:				
			nextIntent = new Intent(getApplicationContext(), LampCopyActivity.class);
			break;
			
		case Adjustment		:				
			nextIntent = new Intent(getApplicationContext(), AdjustmentActivity.class);
			break;

		case Calibration	:				
			nextIntent = new Intent(getApplicationContext(), CalibrationActivity.class);
			break;
			
		case Temperature	:				
			nextIntent = new Intent(getApplicationContext(), TemperatureActivity.class);
			break;
		
		case Absorbance		:				
			nextIntent = new Intent(getApplicationContext(), AbsorbanceActivity.class);
			break;
					
		case Correction1		:				
			nextIntent = new Intent(getApplicationContext(), Correction1Activity.class);
			break;
		
		case Correction2		:				
			nextIntent = new Intent(getApplicationContext(), Correction2Activity.class);
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