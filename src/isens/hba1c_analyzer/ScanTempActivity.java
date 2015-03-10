package isens.hba1c_analyzer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ScanTempActivity extends Activity {
	
	public GpioPort mGpioPort;
	public SerialPort mSerialPort;
	public ErrorPopup mErrorPopup;
	public TimerDisplay mTimerDisplay;
	
	public Handler handler = new Handler();
	public Timer timer;
	
	public AnimationDrawable scanAni;
	public ImageView scanImage;
		
	public RelativeLayout actionLinear;
	
	public Button escBtn;
	
	public AudioManager audioManager;
	public SoundPool mPool;
	public int mWin;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.scantemp);
		
		ActionInit(this, this);
	}
	
	public void ActionInit(Activity activity, Context context) {

		Log.w("ActionInit", "run");
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(activity, R.id.scantemplayout);
		
		ActionActivity.IsCorrectBarcode = false;	
		ActionActivity.BarcodeCheckFlag = false;
		SerialPort.BarcodeReadStart = false;
		
		ActionActivity.ESCButtonFlag = false;
		btnState = false;
		
    	BarcodeScan BarcodeScanObj = new BarcodeScan(activity, context, R.id.scantemplayout);
    	BarcodeScanObj.start();
	}
	
	public class BarcodeScan extends Thread {
		
		Activity activity;
		Context context;
		int layoutid;
		
		public BarcodeScan(Activity activity, Context context, int layoutid) {
			
			this.activity = activity;
			this.context = context;
			this.layoutid = layoutid;
		}
		
		public void run () {
			
			/* Barcode scan action */
    		BarcodeAniStart(activity);
			
			SerialPort.BarcodeBufIndex = 0;
			
			Trigger();
			
			while(!ActionActivity.BarcodeCheckFlag) {
				
				if(ActionActivity.ESCButtonFlag) break; // exiting if esc button is pressed
				SerialPort.Sleep(100);
			}

			timer.cancel();
			
			if(!ActionActivity.ESCButtonFlag) { 
				
				if(ActionActivity.IsCorrectBarcode) {
					
					WhichIntent(activity, context, TargetIntent.BlankTemp);
					
				} else { // to test
				
					mErrorPopup = new ErrorPopup(activity, context, layoutid); // to test
					mErrorPopup.ErrorBtnDisplay(R.string.e313);
				} // to test	
			}	
		}
	}
	
	public void BarcodeAniStart(Activity activity) { // Barcode scan animation start
		
		scanImage = (ImageView)activity.findViewById(R.id.userAct1);
		scanAni = (AnimationDrawable)scanImage.getBackground();
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run(){
				
		            	scanAni.start();
		            }
		        });
		    }
		}).start();
	}
	
	public void Trigger() {
		
		BarcodeScan();
		
		TimerTask triggerTimer = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if (cnt++ == 5) {
							
							cnt = 0;
							BarcodeScan();
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(triggerTimer, 0, 1000); // Timer period : 1sec
	}

	public void BarcodeScan() {
		
		mGpioPort = new GpioPort();
		mGpioPort.TriggerLow();
		SerialPort.Sleep(100);
		mGpioPort.TriggerHigh();
	}
	
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
		TimerDisplay.RXBoardFlag = false;
		
		mGpioPort = new GpioPort();
		mGpioPort.TriggerHigh();
		
		SerialPort.Sleep(1000);
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case BlankTemp	:	
			nextIntent = new Intent(context, BlankTempActivity.class);
			break;
						
		default		:	
			break;			
		}
		
		activity.startActivity(nextIntent);
		finish(activity);
	}
	
	public void finish(Activity activity) {
		
		super.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
