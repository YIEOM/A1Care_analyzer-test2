package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordActivity extends Activity {

	public TimerDisplay mTimerDisplay;
	
	final static byte CONTROL = 1,
					  PATIENT = 2;
	
	private Button patientBtn,
				   controlBtn,
				   backIcon;
	
	public static int DataPage;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.record);			
		
		MemoryInit();

		/*Patient Test Activity activation*/
		patientBtn = (Button)findViewById(R.id.patientbtn);
		patientBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				if(!btnState) {
					
					btnState = true;
					
					patientBtn.setEnabled(false);
					
					WhichIntent(TargetIntent.PatientFileLoad);
				}
			}
		});
		
		/*Control Test Activity activation*/
		controlBtn = (Button)findViewById(R.id.controlbtn);
		controlBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				if(!btnState) {
					
					btnState = true;
					
					controlBtn.setEnabled(false);
				
					WhichIntent(TargetIntent.ControlFileLoad);
				}
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
	}	
	
	public void MemoryInit() {

		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.memorylayout);
		
		DataPage = 0;
	}

	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home				:
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
			
		case ControlFileLoad	:
			nextIntent = new Intent(getApplicationContext(), FileLoadActivity.class);
			nextIntent.putExtra("DataCnt", RemoveActivity.ControlDataCnt); // delivering recent data number
			nextIntent.putExtra("DataPage", DataPage);
			nextIntent.putExtra("Type", (int) CONTROL);
			break;
			
		case PatientFileLoad	:
			nextIntent = new Intent(getApplicationContext(), FileLoadActivity.class);
			nextIntent.putExtra("DataCnt", RemoveActivity.PatientDataCnt); // delivering recent data number
			nextIntent.putExtra("DataPage", DataPage);
			nextIntent.putExtra("Type", (int) PATIENT);
			break;
			
		default					:
			break;			
		}
		
		startActivity(nextIntent);
		finish();		
	}
	
	public void finish() {
		
		super.finish();
	}
}
