package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

public class DataSettingActivity extends Activity {
	
	public TimerDisplay mTimerDisplay;
	
	private Button backBtn,
		   		   homeBtn,
		   		   exportBtn;
	
	private boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.datasetting);
		
		DataSettingInit();
					
		/*Setting Activity activation*/
		backBtn = (Button)findViewById(R.id.backicon);
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;

					backBtn.setEnabled(false);
					
					WhichIntent(TargetIntent.Setting);
				}
			}
		});
		
		/*Home Activity activation*/
		homeBtn = (Button)findViewById(R.id.homeicon);
		homeBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;

					homeBtn.setEnabled(false);

					WhichIntent(TargetIntent.Home);
				}
			}
		});
		
		/*Export Activity activation*/
		exportBtn = (Button)findViewById(R.id.exportbtn);
		exportBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				if(!btnState) {
					
					btnState = true;

					exportBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.Export);
				}
			}
		});
	}
	
	public void DataSettingInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.datalayout);
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home		:	
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
			
		case Setting	:
			nextIntent = new Intent(getApplicationContext(), SettingActivity.class);
			break;
			
		case Export		:
			nextIntent = new Intent(getApplicationContext(), ExportActivity.class);
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