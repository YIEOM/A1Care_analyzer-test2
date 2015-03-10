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

public class ActionActivity extends Activity {
	
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
	
	public static boolean IsCorrectBarcode = false, 
						  BarcodeCheckFlag = false;	
	public static boolean ESCButtonFlag = false;
	
	public static byte CartridgeCheckFlag, 
					   DoorCheckFlag;
	
	public AudioManager audioManager;
	public SoundPool mPool;
	public int mWin;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
			
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.action);
		
		/* Esc Pop-up window activation */
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
				
					btnState = true;
					
					ESC();
					
					btnState = false;
				}
			}
		});
		
		ActionInit(this, this);
	}
	
	public void ActionInit(Activity activity, Context context) {

		Log.w("ActionInit", "run");
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(activity, R.id.actionlayout);
		
		IsCorrectBarcode = false;	
		BarcodeCheckFlag = false;
		SerialPort.BarcodeReadStart = false;
		
		ESCButtonFlag = false;
		btnState = false;
		
    	BarcodeScan BarcodeScanObj = new BarcodeScan(activity, context, R.id.actionlayout);
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
			
//			Log.w("BarcodeScan", "BarcodeCheckFlag : " + BarcodeCheckFlag);
			SerialPort.BarcodeBufIndex = 0;
			
			Trigger();
			
			while(!BarcodeCheckFlag) {
				
				if(ESCButtonFlag) break; // exiting if esc button is pressed
				SerialPort.Sleep(100);
			}

			timer.cancel();
			
			if(!ESCButtonFlag) { 
				
				if(IsCorrectBarcode) {
					
					CartridgeInsert CartridgeInsertObj = new CartridgeInsert(activity, context);
					CartridgeInsertObj.start();
					
				} else { // to test
				
					mErrorPopup = new ErrorPopup(activity, context, layoutid); // to test
					mErrorPopup.ErrorBtnDisplay(R.string.e313);
				} // to test	
			}	
		}
	}
		
	public class CartridgeInsert extends Thread {
		
		Activity activity;
		Context context;
		
		public CartridgeInsert(Activity activity, Context context) {
			
			this.activity = activity;
			this.context = context;
		}
		
		public void run () {

			/* Cartridge insertion action */
			CartridgeAniStart(activity);
				
			TimerDisplay.RXBoardFlag = true;
				
			GpioPort.CartridgeActState = true;
			
			while(ActionActivity.CartridgeCheckFlag != 1) { // to test
			
				if(ESCButtonFlag) break;
				SerialPort.Sleep(100);
			}
			
			if(!ESCButtonFlag) {  // to test
				
				ActionActivity.DoorCheckFlag = 0;
				
				mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
				mWin = mPool.load(context, R.raw.jump, 1);
				
				mPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				      public void onLoadComplete(SoundPool mPool, int sampleId, int status) {

				  		mPool.play(mWin, 1, 1, 0, 0, 1); // playing sound
				      }
				});
				
				CollectorCover CollectorCoverObj = new CollectorCover(activity, context);
				CollectorCoverObj.start();
			}
		}
	}
	
	public class CollectorCover extends Thread {
		
		Activity activity;
		Context context;
		
		public CollectorCover(Activity activity, Context context) {
			
			this.activity = activity;
			this.context = context;
		}
		
		public void run() {
			
			/* Cover close action */
			CoverAniStart(activity);
			
			GpioPort.DoorActState = true;
			
			while((ActionActivity.DoorCheckFlag != 1) | (ActionActivity.CartridgeCheckFlag != 1)) {
				
				if(ESCButtonFlag) break;
				SerialPort.Sleep(100);
			}
			
			if(!ESCButtonFlag) {
				
				WhichIntent(activity, context, TargetIntent.Run);
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
	
	public void CartridgeAniStart(Activity activity) { // Cartridge insertion animation start
		
		actionLinear = (RelativeLayout)activity.findViewById(R.id.actionlayout);
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run(){
		  
						actionLinear.setBackgroundResource(R.drawable.ani_3steps_bg);
		        		scanImage.setBackgroundResource(0);
						scanAni.stop();
		            }
		        });
		    }
		}).start();
    }
	
	public void CoverAniStart(Activity activity) { // Cover close animation start

		actionLinear = (RelativeLayout)activity.findViewById(R.id.actionlayout);
		scanImage = (ImageView)activity.findViewById(R.id.userAct4);
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run(){
		  
						scanImage.setBackgroundResource(R.drawable.useract4);
						scanAni = (AnimationDrawable)scanImage.getBackground();
						
						actionLinear.setBackgroundResource(R.drawable.ani_close_bg);
						
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
	
	public void ESC() {
		
		mErrorPopup = new ErrorPopup(this, this, R.id.actionlayout); // to test
		mErrorPopup.OXBtnDisplay(R.string.esc);
	}
		
	public void WhichIntent(Activity activity, Context context, TargetIntent Itn) { // Activity conversion
		
		TimerDisplay.RXBoardFlag = false;
		GpioPort.CartridgeActState = false;
		GpioPort.DoorActState = false;
		
		mGpioPort = new GpioPort();
		mGpioPort.TriggerHigh();
		
		SerialPort.Sleep(1000);
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Run	:	
			nextIntent = new Intent(context, RunActivity.class);
			break;
						
		case Home	:	
			ESCButtonFlag = true;
			
			nextIntent = new Intent(context, HomeActivity.class);
			break;
				
		case Remove	:	
			ESCButtonFlag = true;
			
			nextIntent = new Intent(context, RemoveActivity.class);
			nextIntent.putExtra("WhichIntent", (int) ResultActivity.COVER_ACTION_ESC);
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
