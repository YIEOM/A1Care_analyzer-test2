package isens.hba1c_analyzer;

import java.util.Timer;
import java.util.TimerTask;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.RunActivity.CartDump;
import isens.hba1c_analyzer.RunActivity.CheckCoverError;
import isens.hba1c_analyzer.SystemCheckActivity.MotorCheck;
import isens.hba1c_analyzer.SystemCheckActivity.TemperatureCheck;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BlankTempActivity extends Activity {

	public SerialPort mSerialPort;
	public ErrorPopup mErrorPopup;
	public TimerDisplay mTimerDisplay;

	public Handler runHandler = new Handler();
	public Timer runningTimer;
	
	private RunActivity.AnalyzerState blankState;
	private byte photoCheck;
	
	private int checkError = RunActivity.NORMAL_OPERATION;
	
	public TextView runTimeText;
	
	public byte runSec;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.blanktemp);
		
		BlankInit();
	}                     
	
	public void BlankInit() {
		
		RunActivity.isError = false;
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.blanklayout);
				
		mSerialPort = new SerialPort();
		
		blankState = RunActivity.AnalyzerState.InitPosition;
		photoCheck = 0;
		
		RunTimerInit(this);
		
		TimerDisplay.RXBoardFlag = true;
		SensorCheck SensorCheckObj = new SensorCheck(this, this, R.id.blanklayout);
		SensorCheckObj.start();
	}
	
	public class SensorCheck extends Thread {
		
		Activity activity;
		Context context;
		int layoutid;
		
		public SensorCheck(Activity activity, Context context, int layoutid) {
			
			this.activity = activity;
			this.context = context;
			this.layoutid = layoutid;
		}
		
		public void run() {
			
			GpioPort.DoorActState = true;			
			GpioPort.CartridgeActState = true;
			
			SerialPort.Sleep(2000);
			
			mErrorPopup = new ErrorPopup(activity, context, layoutid);
			
			if(ActionActivity.CartridgeCheckFlag != 0) mErrorPopup.ErrorDisplay(R.string.w002);
			while(ActionActivity.CartridgeCheckFlag != 0) SerialPort.Sleep(100);
			mErrorPopup.ErrorPopupClose();
			
			if(ActionActivity.DoorCheckFlag != 1) mErrorPopup.ErrorDisplay(R.string.w001);
			while(ActionActivity.DoorCheckFlag != 1) SerialPort.Sleep(100);
			mErrorPopup.ErrorPopupClose();
			
			GpioPort.DoorActState = false;			
			GpioPort.CartridgeActState = false;

			BlankStep BlankStepObj = new BlankStep();
			BlankStepObj.start();
		}
	}
	
	public class BlankStep extends Thread { // Blank run
		
		public void run() {
			
			CartDump mCartDump = new CartDump();
			
			for(int i = 0; i < 9; i++) {
			
				checkMode();
				
				switch(blankState) {
				
				case InitPosition		:
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.MeasurePosition, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break; 
				
				case MeasurePosition :
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.MEASURE_POSITION, AnalyzerState.FilterDark, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case FilterDark :
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					RunActivity.BlankValue[0] = 0;
					RunActivity.BlankValue[0] = AbsorbanceMeasure(SystemCheckActivity.MinDark, SystemCheckActivity.MaxDark, SystemCheckActivity.ERROR_DARK); // Dark Absorbance

					/* TEST Mode */
					if(HomeActivity.ANALYZER_SW == HomeActivity.NORMAL)
						
					PhotoErrorCheck();
					break;
					
				case Filter535nm :
					/* 535nm filter Measurement */
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					RunActivity.BlankValue[1] = AbsorbanceMeasure(SystemCheckActivity.Min535, SystemCheckActivity.Max535, SystemCheckActivity.ERROR_535nm); // Dark Absorbance
					break;
				
				case Filter660nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					RunActivity.BlankValue[2] = AbsorbanceMeasure(SystemCheckActivity.Min660, SystemCheckActivity.Max660, SystemCheckActivity.ERROR_660nm); // Dark Absorbance
					break;
				
				case Filter750nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.FilterHome, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					RunActivity.BlankValue[3] = AbsorbanceMeasure(SystemCheckActivity.Min750, SystemCheckActivity.Max750, SystemCheckActivity.ERROR_750nm); // Dark Absorbance
				
					/* TEST Mode */
					if(HomeActivity.ANALYZER_SW == HomeActivity.NORMAL)
					
					PhotoErrorCheck();
					break;
				
				case FilterHome :
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.CartridgeHome, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					break;
				
				case CartridgeHome :
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NormalOperation, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case NormalOperation	:
					SerialPort.Sleep(1000);
					CoverClose mCoverClose = new CoverClose();
					mCoverClose.start();
					break;
				
				case ShakingMotorError	:
					checkError = R.string.e211;
					blankState = AnalyzerState.NoWorking;
					WhichIntent(TargetIntent.Home);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					WhichIntent(TargetIntent.Home);
					break;
				
				case PhotoSensorError	:
					blankState = AnalyzerState.NoWorking;
					mCartDump.start();
					break;
					
				case LampError			:
					checkError = R.string.e232;
					blankState = AnalyzerState.NoWorking;
					mCartDump.start();
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					blankState = AnalyzerState.NoWorking;
					WhichIntent(TargetIntent.Home);
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					blankState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}

			if(checkError == R.string.e322) {
				
				mErrorPopup.ErrorDisplay(R.string.w004);
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
			}
		}
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		mSerialPort.BoardTx(str, target);
	}
	
	public synchronized double AbsorbanceMeasure(double min, double max, byte errBits) { // Absorbance measurement
	
		int time = 0;
		String rawValue;
		double douValue = 0;
		
		mSerialPort.BoardTx("VH", SerialPort.CtrTarget.NormalSet);
		
		rawValue = mSerialPort.BoardMessageOutput();			
		
		while(rawValue.length() != 8) {
		
			rawValue = mSerialPort.BoardMessageOutput();			
				
			if(time++ > 50) {
				
				blankState = AnalyzerState.NoResponse;
			
				break;
			}
			
			if(RunActivity.isError) break;
			
			SerialPort.Sleep(100);
		}	
		
		if(blankState != AnalyzerState.NoResponse && !RunActivity.isError) {

			douValue = Double.parseDouble(rawValue);
			
			if((min < douValue) & (douValue < max)) {
				
				return (douValue - RunActivity.BlankValue[0]);
				
			} else photoCheck += errBits;
		}
		
		return 0.0;
	}
	
	public void PhotoErrorCheck() {
		
		switch(photoCheck) {
		
		case 1	:
			blankState = AnalyzerState.PhotoSensorError;
			checkError = R.string.e231;
			break;
			
		case 2	:
			blankState = AnalyzerState.PhotoSensorError;
			checkError = R.string.e233;
			break;
			
		case 4	:
			blankState = AnalyzerState.PhotoSensorError;
			checkError = R.string.e234;
			break;
			
		case 8	:
			blankState = AnalyzerState.PhotoSensorError;
			checkError = R.string.e235;
			break;
			
		case 14	:
			blankState = AnalyzerState.LampError;
			checkError = R.string.e232;
			break;
			
		default	:
			break;
		}
	}
	
	public void BoardMessage(String colRsp, AnalyzerState nextState, String errRsp, AnalyzerState errState, int rspTime) {
		
		int time = 0;
		String temp = "";
		
		rspTime = rspTime * 10;
		
		while(true) {
			
			temp = mSerialPort.BoardMessageOutput();
//			Log.w("BoardMessage", "temp : " + temp);
			if(colRsp.equals(temp)) {
				
				blankState = nextState;
				break;
			
			} else if(errRsp.equals(temp)) {
				
				blankState = errState;
				break;
			}
			
			if(time++ > rspTime) {
				
				blankState = AnalyzerState.NoResponse;
				break;
			}
			
			if(RunActivity.isError) break;
			
			SerialPort.Sleep(100);
		}
	}
	
	public class CoverClose extends Thread {
		
		public void run() {
			
			GpioPort.DoorActState = true;
			GpioPort.CartridgeActState = true;
			
			SerialPort.Sleep(2000);
			
			while(ActionActivity.DoorCheckFlag != 0) {
				
				SerialPort.Sleep(100);
			}	
			
			GpioPort.DoorActState = false;			
			GpioPort.CartridgeActState = false;
			
			WhichIntent(TargetIntent.ActionTemp);
		}
	}
	
	public void checkMode() {
		
		if(RunActivity.isError) {
				
			blankState = AnalyzerState.ErrorCover;
		}
	}
	
	public class CheckCoverError extends Thread {
		
		public void run() {
			
			GpioPort.DoorActState = true;			
			GpioPort.CartridgeActState = true;
			
			SerialPort.Sleep(2000);
			
			while(ActionActivity.DoorCheckFlag != 1) SerialPort.Sleep(100);
			
			GpioPort.DoorActState = false;			
			GpioPort.CartridgeActState = false;
			
			mErrorPopup.ErrorDisplay(R.string.wait);
			
			CartDump mCartDump = new CartDump();
			mCartDump.start();
		}
	}
	
	public class CartDump extends Thread { // Cartridge dumping motion
		
		public void run() {
				
			RunActivity.isError = false;
			blankState = AnalyzerState.FilterDark;
			
			for(int i = 0; i < 3; i++) {
				
				checkMode();
				
				switch(blankState) {
				
				case FilterDark	:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.CartridgeHome, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					break;
					
				case CartridgeHome	:
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NormalOperation, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					blankState = AnalyzerState.NoWorking;
					WhichIntent(TargetIntent.Home);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					WhichIntent(TargetIntent.Home);
					break;
					
				case LampError			:
					checkError = R.string.e232;
					CartDump mCartDump = new CartDump();
					mCartDump.start();
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					blankState = AnalyzerState.NoWorking;
					WhichIntent(TargetIntent.Home);
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					blankState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			if(blankState == AnalyzerState.NormalOperation) {
			
				mErrorPopup.ErrorPopupClose();
				
				WhichIntent(TargetIntent.Home);
			
			} else if(checkError == R.string.e322) {
				
				mErrorPopup.ErrorDisplay(R.string.w004);
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
			}
		}
	}	
	
	public void RunTimerInit(final Activity activity) {

		runSec = 20;
		
		TimerTask OneSecondPeriod = new TimerTask() {
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						RunTimeDisplay(activity);
					}
				};
				
				runHandler.post(updater);		
			}
		};
		
		runningTimer = new Timer();
		runningTimer.schedule(OneSecondPeriod, 0, 1000); // Timer period : 100msec
	}
	
	public void RunTimeDisplay(Activity activity) { // Display running time
		
		runTimeText = (TextView) activity.findViewById(R.id.runTimeText);
		
		if(runSec-- > 0) {
		
			runTimeText.setText(Integer.toString(runSec) + " sec");
		
		} else runTimeText.setText("Blank End");
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		TimerDisplay.RXBoardFlag = false;
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home	:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			nextIntent.putExtra("System Check State", (int) checkError);
			break;
			
		case ActionTemp	:				
			nextIntent = new Intent(getApplicationContext(), ActionTempActivity.class);
			break;
			
		default			:	
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
