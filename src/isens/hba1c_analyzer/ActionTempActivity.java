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
import android.graphics.Color;
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

public class ActionTempActivity extends Activity {
	
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
		setContentView(R.layout.actiontemp);
		
		ActionInit(this, this);
	}
	
	public void ActionInit(Activity activity, Context context) {

		Log.w("ActionInit", "run");
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(activity, R.id.actiontemplayout);
		
		CartridgeInsert CartridgeInsertObj = new CartridgeInsert(activity, context);
		CartridgeInsertObj.start();		
	}
	
	public class CartridgeInsert extends Thread {
		
		Activity activity;
		Context context;
		
		public CartridgeInsert(Activity activity, Context context) {
			
			this.activity = activity;
			this.context = context;
		}
		
		public void run () {

			TimerDisplay.RXBoardFlag = true;
				
			GpioPort.CartridgeActState = true;
			
			while(ActionActivity.CartridgeCheckFlag != 1) { // to test
			
				if(ActionActivity.ESCButtonFlag) break;
				SerialPort.Sleep(100);
			}
			
			if(!ActionActivity.ESCButtonFlag) {  // to test
				
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
			
			GpioPort.DoorActState = true;
			
			while((ActionActivity.DoorCheckFlag != 1) | (ActionActivity.CartridgeCheckFlag != 1)) {
				
				if(ActionActivity.ESCButtonFlag) break;
				SerialPort.Sleep(100);
			}
			
			if(!ActionActivity.ESCButtonFlag) {
				
				WhichIntent(activity, context, TargetIntent.Run);
			}
		}
	}

	public void ESC() {
		
		mErrorPopup = new ErrorPopup(this, this, R.id.actiontemplayout); // to test
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
			ActionActivity.ESCButtonFlag = true;
			
			nextIntent = new Intent(context, HomeActivity.class);
			break;
				
		case Remove	:	
			ActionActivity.ESCButtonFlag = true;
			
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
