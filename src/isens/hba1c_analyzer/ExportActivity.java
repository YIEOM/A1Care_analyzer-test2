package isens.hba1c_analyzer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ExportActivity extends Activity {

	public TimerDisplay mTimerDisplay;
	
	public UsbManager mUSBManager;
	public Reader mNFCReader;
	
	public TextView explain;
	
	public Button nextBtn;
	public Button backIcon;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.export);
		
		explain = (TextView) findViewById(R.id.explain);
		
		MemoryInit();

		/*Load to data activation*/
		nextBtn = (Button)findViewById(R.id.nextbtn);
		nextBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				nextBtn.setEnabled(false);
				
			}
		});

		/*Memory Activity activation*/
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				backIcon.setEnabled(false);
				
				WhichIntent();
			}
		});
	}	
	
	private void MemoryInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.exportlayout);
		
		Check();
		
		SerialPort.Sleep(300);
	}

	public void WhichIntent() { // Activity conversion
			
		Intent CancleIntent = new Intent(getApplicationContext(), DataSettingActivity.class);
		startActivity(CancleIntent);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		finish();
	}
	
	public void Check() {
		
//		UsbManager manager =(UsbManager) getSystemService(Context.USB_SERVICE);
//		UsbAccessory[] accessoryList = manager.getAccessoryList();
//		
//		Log.w("check", "accessory : " + accessoryList);
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
