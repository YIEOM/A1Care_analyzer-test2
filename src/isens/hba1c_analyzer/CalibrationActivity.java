package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.RunActivity.Cart1stFilter1;
import isens.hba1c_analyzer.RunActivity.Cart1stFilter2;
import isens.hba1c_analyzer.RunActivity.Cart1stFilter3;
import isens.hba1c_analyzer.RunActivity.Cart2ndFilter1;
import isens.hba1c_analyzer.RunActivity.Cart2ndFilter2;
import isens.hba1c_analyzer.RunActivity.Cart2ndFilter3;
import isens.hba1c_analyzer.RunActivity.Cart2ndShaking;
import isens.hba1c_analyzer.RunActivity.CartDump;
import isens.hba1c_analyzer.RunActivity.ShakingAniThread;
import isens.hba1c_analyzer.SerialPort.CtrTarget;

public class CalibrationActivity extends Activity{

	public SerialPort mSerialPort;
	public ActionActivity mActionActivity;
	public TimerDisplay mTimerDisplay;
	
	public Button escBtn,
				  blankBtn,
				  quickBtn,
				  fullBtn;
	
	public TextView deviceState,
					oneOne,
					oneTwo,
					oneThree,
					twoOne,
					twoTwo,
					twoThree,
					threeOne,
					threeTwo,
					threeThree,
					fourOne,
					fourTwo,
					fourThree,
					fiveOne,
					fiveTwo,
					fiveThree,
					sixOne,
					sixTwo,
					sixThree,
					hba1cStr,
					tHbStr;
	
	public Handler handler = new Handler();
	public TimerTask OneHundredmsPeriod;
	public Timer timer;
	
	public static boolean TestFlag = false,
						   ThreadRun = false;
	
	public enum TargetMode {Blank, Quick, Full, Scan}
	public enum MeasTarget {Shk1stOne, Shk1stTwo, Shk1stThree, Shk2ndOne, Shk2ndTwo, Shk2ndThree}
	
	public TargetMode targetMode = null;
	public MeasTarget measTarget = null;
	
	public RunActivity.AnalyzerState calibState;
	
	public boolean absorbCheck = false,
				   btnState = false;
	
	DecimalFormat AbsorbanceFormat = new DecimalFormat("0.0000"),
				  hbA1cFormat = new DecimalFormat("0.00"),
				  tmpFormat = new DecimalFormat("0.0");
	
	public int checkError;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.calibration);
		
		deviceState = (TextView) findViewById(R.id.devicestate);
		
		oneOne = (TextView) findViewById(R.id.oneone);
		oneTwo = (TextView) findViewById(R.id.onetwo);
		oneThree = (TextView) findViewById(R.id.onethree);
		
		twoOne = (TextView) findViewById(R.id.twoone);
		twoTwo = (TextView) findViewById(R.id.twotwo);
		twoThree = (TextView) findViewById(R.id.twothree);

		fourOne = (TextView) findViewById(R.id.fourone);
		fourTwo = (TextView) findViewById(R.id.fourtwo);
		fourThree = (TextView) findViewById(R.id.fourthree);
		
		threeOne = (TextView) findViewById(R.id.threeone);
		threeTwo = (TextView) findViewById(R.id.threetwo);
		threeThree = (TextView) findViewById(R.id.threethree);
		
		fiveOne = (TextView) findViewById(R.id.fiveone);
		fiveTwo = (TextView) findViewById(R.id.fivetwo);
		fiveThree = (TextView) findViewById(R.id.fivethree);
		
		sixOne = (TextView) findViewById(R.id.sixone);
		sixTwo = (TextView) findViewById(R.id.sixtwo);
		sixThree = (TextView) findViewById(R.id.sixthree);
		
		hba1cStr = (TextView) findViewById(R.id.HbA1cText);
		tHbStr = (TextView) findViewById(R.id.tHbText);
		
		escBtn = (Button)findViewById(R.id.escicon);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
				
					btnState = true;

					escBtn.setEnabled(false);
					
					WhichIntent(TargetIntent.Maintenance);
				}
			}
		});
		
		blankBtn = (Button)findViewById(R.id.blankbtn);
		blankBtn.setOnClickListener(new View.OnClickListener() { 
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					blankBtn.setEnabled(false);
					
					BlankMode();
				}
			}
		});
		
//		scanBtn = (Button)findViewById(R.id.scanbtn);
//		scanBtn.setOnClickListener(new View.OnClickListener() { 
//		
//			public void onClick(View v) {
//				
//				if(!btnState) {
//					
//					btnState = true;
//					
//					scanBtn.setEnabled(false);
//					
//					BarcodeStart();
//				}
//			}
//		});

		
		quickBtn = (Button)findViewById(R.id.quickbtn);
		quickBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					quickBtn.setEnabled(false);
					
					QuickMode();
				}
			}
		});
		
		fullBtn = (Button)findViewById(R.id.fullbtn);
		fullBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {

				if(!btnState) {
					
					btnState = true;
					
					fullBtn.setEnabled(false);
					
					FullMode();
				}
			}
		});
		
		CalibrationInit();
	}
	
	public void CalibrationInit() {
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.caliblayout);
		
		AbsorbanceDisplay();
		CalValueDisplay();
		
		mSerialPort = new SerialPort();
	}

	public void TimerInit() {
		
		OneHundredmsPeriod = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						cnt++;
						
						if(targetMode != TargetMode.Scan) {
						
							if(cnt == 5)	DeviceStateDisplay1();
							else if (cnt == 10) {
								
								cnt = 0; 
								DeviceStateDisplay2();
							}
							
							ThreadCheck();
						
						} else {
							
							if(cnt == 50) {
								
								cnt = 0;
								
								mActionActivity = new ActionActivity();
								mActionActivity.BarcodeScan();
							}
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(OneHundredmsPeriod, 0, 100); // Timer period : 100msec
	}
	
	public void ThreadCheck() {
		
		if(absorbCheck)
		{
			absorbCheck = false;
			
			switch(measTarget) {
			
			case Shk1stOne		:
				AbsorbanceDisplay1();
				break;
			
			case Shk1stTwo		:
				AbsorbanceDisplay2();
				break;
			
			case Shk1stThree	:
				AbsorbanceDisplay3();
				break;
			
			case Shk2ndOne		:
				AbsorbanceDisplay4();
				break;
			
			case Shk2ndTwo		:
				AbsorbanceDisplay5();
				break;
			
			case Shk2ndThree	:
				AbsorbanceDisplay6();
				break;
				
			default :
				break;
			}
		}
				
		if(!ThreadRun) {
			
			Stop();	
		}
	}
	
	public void Stop() {
		
		timer.cancel();
		
		SerialPort.Sleep(300);
		
		switch(checkError) {
		
		case RunActivity.NORMAL_OPERATION	:
			deviceState.setTextColor(Color.parseColor("#565656"));
			deviceState.setText("READY");
			break;
			
		case R.string.e211		:
			deviceState.setTextColor(Color.parseColor("#FF0000"));
			deviceState.setText("SHAKING ERROR");
			break;
			
		case R.string.e212		:
			deviceState.setTextColor(Color.parseColor("#FF0000"));
			deviceState.setText("FILTER ERROR");
			break;
			
		case R.string.e241		:
			deviceState.setTextColor(Color.parseColor("#FF0000"));
			deviceState.setText("RESPONSE ERROR");
			break;
			
		default	:
			break;
		}
		
		btnState = false;
		
		blankBtn.setEnabled(true);
//		scanBtn.setEnabled(true);
		quickBtn.setEnabled(true);
		fullBtn.setEnabled(true);
		escBtn.setEnabled(true);
	}
	
	public void BlankMode() {
		
		escBtn.setEnabled(false);
//		scanBtn.setEnabled(false);
		quickBtn.setEnabled(false);
		fullBtn.setEnabled(false);
		
		targetMode = TargetMode.Blank;
		calibState = AnalyzerState.InitPosition;
		checkError = RunActivity.NORMAL_OPERATION;
		ThreadRun = true;
		
		TimerInit();
		
		TimerDisplay.RXBoardFlag = true;
		BlankStep BlankStepObj = new BlankStep();
		BlankStepObj.start();
	}
	
	public class BlankStep extends Thread { // Blank run
		
		public void run() {
			
			for(int i = 0; i < 9; i++) {
				
				switch(calibState) {
				
				case InitPosition		:
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.MeasurePosition, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
				
				case MeasurePosition :
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.MEASURE_POSITION, AnalyzerState.FilterDark, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 5);
					break;
					
				case FilterDark :
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.BlankValue[0] = 0;
					RunActivity.BlankValue[0] = AbsorbanceMeasure(); // Dark Absorbance
					break;
					
				case Filter535nm :
					/* 535nm filter Measurement */
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.BlankValue[1] = AbsorbanceMeasure(); // Dark Absorbance
					break;
				
				case Filter660nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.BlankValue[2] = AbsorbanceMeasure(); // Dark Absorbance
					break;
				
				case Filter750nm :
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.FilterHome, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.BlankValue[3] = AbsorbanceMeasure(); // Dark Absorbance
					break;
				
				case FilterHome :
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.CartridgeHome, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					break;
				
				case CartridgeHome :
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NormalOperation, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 5);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			ThreadRun = false;
			TimerDisplay.RXBoardFlag = false;
		}
	}
	
	public void QuickMode() {
		
		blankBtn.setEnabled(false);
//		scanBtn.setEnabled(false);
		fullBtn.setEnabled(false);
		escBtn.setEnabled(false);
		
		targetMode = TargetMode.Quick;
		calibState = AnalyzerState.InitPosition;
		checkError = RunActivity.NORMAL_OPERATION;
		ThreadRun = true;
		
		AbsorbanceDisplay();
		
		TimerInit();
		
		TimerDisplay.RXBoardFlag = true;
		Cart1stShaking Cart1stShakingObj = new Cart1stShaking();
		Cart1stShakingObj.start();
	}
	
	public void FullMode() {
		
		blankBtn.setEnabled(false);
//		scanBtn.setEnabled(false);
		quickBtn.setEnabled(false);
		escBtn.setEnabled(false);
		
		targetMode = TargetMode.Full;
		calibState = AnalyzerState.InitPosition;
		checkError = RunActivity.NORMAL_OPERATION;
		ThreadRun = true;
		
		AbsorbanceDisplay();
		
		TimerInit();
		
		TimerDisplay.RXBoardFlag = true;
		Cart1stShaking BlankCart1stShakingObj = new Cart1stShaking();
		BlankCart1stShakingObj.start();
	}
	
	public class Cart1stShaking extends Thread { // First shaking motion

		public void run() {
			
			String shkTime = "0000";
			
			if(targetMode == TargetMode.Quick) shkTime = "0030";
			else if(targetMode == TargetMode.Full) shkTime = "0630";
			
			for(int i = 0; i < 3; i++) {
				
				switch(calibState) {
				
				case InitPosition		:
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.Step1Position, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
				
				case Step1Position	:
					MotionInstruct(RunActivity.Step1st_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.Step1st_POSITION, AnalyzerState.Step1Shaking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 5);
					SerialPort.Sleep(500);
					break;
					
				case Step1Shaking	:
					MotionInstruct(shkTime, SerialPort.CtrTarget.MotorSet);  // Motor shaking time, default : 6.5 * 10(sec) = 0065
					BoardMessage(RunActivity.MOTOR_COMPLETE, AnalyzerState.MeasurePosition, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 110);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
						
				default	:
					break;
				}
			}
			
			if(calibState == AnalyzerState.MeasurePosition) {
				
				Cart1stFilter1 Cart1stFilter1Obj = new Cart1stFilter1();
				Cart1stFilter1Obj.start();	
			}
		}
	}

	public class Cart1stFilter1 extends Thread { // First filter motion of first shaking 

		public void run() {

			SerialPort.Sleep(2000);
			
			for(int i = 0; i < 5; i++) {
				
				switch(calibState) {
				
				case MeasurePosition	:
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.MEASURE_POSITION, AnalyzerState.Filter535nm, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 5);
					break;
					
				case Filter535nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step1stValue1[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step1stValue1[1] = AbsorbanceMeasure(); // 660nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.FilterDark, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step1stValue1[2] = AbsorbanceMeasure(); // 750nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					AbsorbCal1st();
					measTarget = MeasTarget.Shk1stOne;
					absorbCheck = true;
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
					
					
				default	:
					break;
				}
			}
			
			if(calibState == AnalyzerState.Filter535nm) {
				
				Cart1stFilter2 Cart1stFilter2Obj = new Cart1stFilter2();
				Cart1stFilter2Obj.start();	
			}
		}
	}

	public class Cart1stFilter2 extends Thread { // Second filter motion of first shaking

		public void run() {
			
			SerialPort.Sleep(1000);
			
			for(int i = 0; i < 4; i++) {
				
				switch(calibState) {
				
				case Filter535nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step1stValue2[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step1stValue2[1] = AbsorbanceMeasure(); // 660nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.FilterDark, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step1stValue2[2] = AbsorbanceMeasure(); // 750nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					AbsorbCal1st2();
					measTarget = MeasTarget.Shk1stTwo;
					absorbCheck = true;
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			if(calibState == AnalyzerState.Filter535nm) {
				
				Cart1stFilter3 Cart1stFilter3Obj = new Cart1stFilter3();
				Cart1stFilter3Obj.start();
			}
		}
	}
	
	public class Cart1stFilter3 extends Thread { // Third filter motion of first shaking

		public void run() {
			
			SerialPort.Sleep(1000);
			
			for(int i = 0; i < 4; i++) {
				
				switch(calibState) {
				
				case Filter535nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step1stValue3[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step1stValue3[1] = AbsorbanceMeasure(); // 750nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.FilterDark, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step1stValue3[2] = AbsorbanceMeasure(); // 750nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Step2Position, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					AbsorbCal1st3();
					measTarget = MeasTarget.Shk1stThree;
					absorbCheck = true;
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			if(calibState == AnalyzerState.Step2Position) {
				
				Cart2ndShaking Cart2ndShakingObj = new Cart2ndShaking();
				Cart2ndShakingObj.start();	
			}
		}
	}
	
	public class Cart2ndShaking extends Thread { // Second shaking motion
		
		public void run() {			
			
			String shkTime = "0000";
			
			if(targetMode == TargetMode.Quick) shkTime = "0030";
			else if(targetMode == TargetMode.Full) shkTime = "0540";
			
			for(int i = 0; i < 2; i++) {
				
				switch(calibState) {
				
				case Step2Position	:
					MotionInstruct(RunActivity.Step2nd_POSITION, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.Step2nd_POSITION, AnalyzerState.Step2Shaking, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					SerialPort.Sleep(500);
					break;
					
				case Step2Shaking	:
					MotionInstruct(shkTime, SerialPort.CtrTarget.MotorSet);  // Motor shaking time, default : 6 * 10(sec) = 0065
					BoardMessage(RunActivity.MOTOR_COMPLETE, AnalyzerState.MeasurePosition, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 100);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			if(calibState == AnalyzerState.MeasurePosition) {
				
				Cart2ndFilter1 Cart2ndFilter1Obj = new Cart2ndFilter1();
				Cart2ndFilter1Obj.start();
			}
		}
	}
	
	public class Cart2ndFilter1 extends Thread { // First filter motion of second shaking
		
		public void run() {			

			SerialPort.Sleep(2000);
			
			for(int i = 0; i < 5; i++) {
				
				switch(calibState) {
				
				case MeasurePosition	:
					MotionInstruct(RunActivity.MEASURE_POSITION, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.MEASURE_POSITION, AnalyzerState.Filter535nm, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case Filter535nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step2ndValue1[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step2ndValue1[1] = AbsorbanceMeasure(); // 660nm Absorbance
					break;
				
				case Filter750nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.FilterDark, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step2ndValue1[2] = AbsorbanceMeasure(); // 750nm Absorbance
					break;
				
				case FilterDark		:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					AbsorbCal2nd();
					measTarget = MeasTarget.Shk2ndOne;
					absorbCheck = true;
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			if(calibState == AnalyzerState.Filter535nm) {
			
				Cart2ndFilter2 Cart2ndFilter2Obj = new Cart2ndFilter2();
				Cart2ndFilter2Obj.start();
			}
		}
	}
	
	public class Cart2ndFilter2 extends Thread { // Second filter motion of second shaking
		
		public void run() {
			
			SerialPort.Sleep(1000);
						
			for(int i = 0; i < 4; i++) {
				
				switch(calibState) {
				
				case Filter535nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step2ndValue2[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step2ndValue2[1] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.FilterDark, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step2ndValue2[2] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.Filter535nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					AbsorbCal2nd2();
					measTarget = MeasTarget.Shk2ndTwo;
					absorbCheck = true;
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
		
			if(calibState == AnalyzerState.Filter535nm) {
					
				Cart2ndFilter3 Cart2ndFilter3Obj = new Cart2ndFilter3();
				Cart2ndFilter3Obj.start();
			}
		}
	}
	
	public class Cart2ndFilter3 extends Thread { // Third filter motion of second shaking
		
		public void run() {
			
			SerialPort.Sleep(1000);
			
			for(int i = 0; i < 4; i++) {
				
				switch(calibState) {
				
				case Filter535nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter660nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step2ndValue3[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.Filter750nm, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step2ndValue3[1] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(RunActivity.NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.NEXT_FILTER, AnalyzerState.FilterDark, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					RunActivity.Step2ndValue3[2] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(RunActivity.FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.FILTER_DARK, AnalyzerState.CartridgeDump, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					AbsorbCal2nd3();
					measTarget = MeasTarget.Shk2ndThree;
					absorbCheck = true;
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}			
			
			if(calibState == AnalyzerState.CartridgeDump) {
				
				CartDump CartDumpObj = new CartDump();
				CartDumpObj.start();
			}
		}
	}
	
	public class CartDump extends Thread { // Cartridge dumping motion
		
		public void run() {
		
			for(int i = 0; i < 3; i++) {
				
				switch(calibState) {
				
				case CartridgeDump	:
					MotionInstruct(RunActivity.CARTRIDGE_DUMP, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.CARTRIDGE_DUMP, AnalyzerState.CartridgeHome, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case CartridgeHome	:
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NormalOperation, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					calibState = AnalyzerState.NoWorking;
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(RunActivity.HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(RunActivity.HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 6);
					break;
					
				case NoResponse :
					checkError = R.string.e241;
					calibState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			if(calibState == AnalyzerState.NormalOperation) {
				
				HbA1cCalculation();
				
				new Thread(new Runnable() {
					public void run() {
						runOnUiThread(new Runnable(){
							public void run() {

								HbA1cDisplay();
							}
						});
					}
				}).start();
				
				ThreadRun = false;
				TimerDisplay.RXBoardFlag = true;
			}
		}
	}
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		mSerialPort.BoardTx(str, target);
	}
	
	public synchronized void AbsorbCal1st() {
		
		RunActivity.Step1stAbsorb1[0]  = -Math.log10(RunActivity.Step1stValue1[0]/RunActivity.BlankValue[1]);
		RunActivity.Step1stAbsorb1[1]  = -Math.log10(RunActivity.Step1stValue1[1]/RunActivity.BlankValue[2]);
		RunActivity.Step1stAbsorb1[2]  = -Math.log10(RunActivity.Step1stValue1[2]/RunActivity.BlankValue[3]);
	}

	public synchronized void AbsorbCal1st2() {
		
		RunActivity.Step1stAbsorb2[0] = -Math.log10(RunActivity.Step1stValue2[0]/RunActivity.BlankValue[1]);
		RunActivity.Step1stAbsorb2[1] = -Math.log10(RunActivity.Step1stValue2[1]/RunActivity.BlankValue[2]);
		RunActivity.Step1stAbsorb2[2] = -Math.log10(RunActivity.Step1stValue2[2]/RunActivity.BlankValue[3]);
	}
	
	public synchronized void AbsorbCal1st3() {
		
		RunActivity.Step1stAbsorb3[0] = -Math.log10(RunActivity.Step1stValue3[0]/RunActivity.BlankValue[1]);
		RunActivity.Step1stAbsorb3[1] = -Math.log10(RunActivity.Step1stValue3[1]/RunActivity.BlankValue[2]);
		RunActivity.Step1stAbsorb3[2] = -Math.log10(RunActivity.Step1stValue3[2]/RunActivity.BlankValue[3]);
	}
	
	public synchronized void AbsorbCal2nd() {
		
		RunActivity.Step2ndAbsorb1[0]  = -Math.log10(RunActivity.Step2ndValue1[0]/RunActivity.BlankValue[1]); // 535nm
		RunActivity.Step2ndAbsorb1[1]  = -Math.log10(RunActivity.Step2ndValue1[1]/RunActivity.BlankValue[2]); // 660nm
		RunActivity.Step2ndAbsorb1[2]  = -Math.log10(RunActivity.Step2ndValue1[2]/RunActivity.BlankValue[3]); // 750nm
	}
	
	public synchronized void AbsorbCal2nd2() {
		
		RunActivity.Step2ndAbsorb2[0] = -Math.log10(RunActivity.Step2ndValue2[0]/RunActivity.BlankValue[1]);
		RunActivity.Step2ndAbsorb2[1] = -Math.log10(RunActivity.Step2ndValue2[1]/RunActivity.BlankValue[2]);
		RunActivity.Step2ndAbsorb2[2] = -Math.log10(RunActivity.Step2ndValue2[2]/RunActivity.BlankValue[3]);
	}
	
	public synchronized void AbsorbCal2nd3() {
				
		RunActivity.Step2ndAbsorb3[0] = -Math.log10(RunActivity.Step2ndValue3[0]/RunActivity.BlankValue[1]);
		RunActivity.Step2ndAbsorb3[1] = -Math.log10(RunActivity.Step2ndValue3[1]/RunActivity.BlankValue[2]);
		RunActivity.Step2ndAbsorb3[2] = -Math.log10(RunActivity.Step2ndValue3[2]/RunActivity.BlankValue[3]);
	}
	
	public void DeviceStateDisplay1() {

		switch(targetMode) {
		
		case Blank			:
			deviceState.setTextColor(Color.parseColor("#565656"));
			deviceState.setText("BLANK");
			break;
			
		case Quick			:
			deviceState.setTextColor(Color.parseColor("#04A458"));
			deviceState.setText("QUICK");
			break;
		
		case Full	:
			deviceState.setTextColor(Color.parseColor("#023894"));
			deviceState.setText("FULL");
			break;
			
		default	:
			break;
		}
	}
	
	public void DeviceStateDisplay2() {
		
		switch(targetMode) {
		
		case Blank			:
			deviceState.setTextColor(Color.parseColor("#FFFFFF"));
			deviceState.setText("BLANK");
			break;
			
		case Quick			:
			deviceState.setTextColor(Color.parseColor("#FFFFFF"));
			deviceState.setText("QUICK");
			break;
			
		case Full	:
			deviceState.setTextColor(Color.parseColor("#FFFFFF"));
			deviceState.setText("FULL");
			break;
			
		default	:
			break;
		}
   	}
	
	public void AbsorbanceDisplay() {
		
    	oneOne.setText("");
    	oneTwo.setText("");
    	oneThree.setText("");
			
    	twoOne.setText("");
    	twoTwo.setText("");
    	twoThree.setText(""); 
		
    	threeOne.setText("");
    	threeTwo.setText("");
    	threeThree.setText("");
	
    	fourOne.setText("");
    	fourTwo.setText("");
    	fourThree.setText("");
		
		fiveOne.setText("");
    	fiveTwo.setText("");
    	fiveThree.setText("");
		
    	sixOne.setText("");
    	sixTwo.setText("");
    	sixThree.setText("");
	}
	
	public void CalValueDisplay() {
    	
    	hba1cStr.setText("");
    	tHbStr.setText("");
	}
	
	public void AbsorbanceDisplay1() {
		
    	oneOne.setText(AbsorbanceFormat.format(RunActivity.Step1stAbsorb1[0]));
    	oneTwo.setText(AbsorbanceFormat.format(RunActivity.Step1stAbsorb1[1]));
    	oneThree.setText(AbsorbanceFormat.format(RunActivity.Step1stAbsorb1[2]));
	}
	
	public void AbsorbanceDisplay2() {
			
    	twoOne.setText(AbsorbanceFormat.format(RunActivity.Step1stAbsorb2[0]));
    	twoTwo.setText(AbsorbanceFormat.format(RunActivity.Step1stAbsorb2[1]));
    	twoThree.setText(AbsorbanceFormat.format(RunActivity.Step1stAbsorb2[2])); 
	}
	
	public void AbsorbanceDisplay3() {
		
    	threeOne.setText(AbsorbanceFormat.format(RunActivity.Step1stAbsorb3[0]));
    	threeTwo.setText(AbsorbanceFormat.format(RunActivity.Step1stAbsorb3[1]));
    	threeThree.setText(AbsorbanceFormat.format(RunActivity.Step1stAbsorb3[2]));
	}
	
	public void AbsorbanceDisplay4() {

    	fourOne.setText(AbsorbanceFormat.format(RunActivity.Step2ndAbsorb1[0]));
    	fourTwo.setText(AbsorbanceFormat.format(RunActivity.Step2ndAbsorb1[1]));
    	fourThree.setText(AbsorbanceFormat.format(RunActivity.Step2ndAbsorb1[2]));
	}
	
	public void AbsorbanceDisplay5() {
		
		fiveOne.setText(AbsorbanceFormat.format(RunActivity.Step2ndAbsorb2[0]));
    	fiveTwo.setText(AbsorbanceFormat.format(RunActivity.Step2ndAbsorb2[1]));
    	fiveThree.setText(AbsorbanceFormat.format(RunActivity.Step2ndAbsorb2[2]));
	}
	
	public void AbsorbanceDisplay6() {
		
    	sixOne.setText(AbsorbanceFormat.format(RunActivity.Step2ndAbsorb3[0]));
    	sixTwo.setText(AbsorbanceFormat.format(RunActivity.Step2ndAbsorb3[1]));
    	sixThree.setText(AbsorbanceFormat.format(RunActivity.Step2ndAbsorb3[2])); 
	}
	
	public void HbA1cDisplay() {
		
    	hba1cStr.setText(hbA1cFormat.format(RunActivity.HbA1cValue));
		tHbStr.setText(hbA1cFormat.format(RunActivity.tHbDbl));
	}
	
	public void BarcodeStart() {

		blankBtn.setEnabled(false);
		quickBtn.setEnabled(false);
		fullBtn.setEnabled(false);
		escBtn.setEnabled(false);
		
		CalValueDisplay();
		
//		mSerialPort = new SerialPort(R.id.caliblayout);
//		mSerialPort.BarcodeSerialInit();
//		mSerialPort.BarcodeRxStart();
		
		ActionActivity.BarcodeCheckFlag = false;
		
		RunActivity.HbA1cValue = 0.0;
		RunActivity.tHbDbl = 0.0;
		
		targetMode = TargetMode.Scan;
		
		mActionActivity = new ActionActivity();
		mActionActivity.BarcodeScan();
		
		TimerInit();
		
		BarcodeScan BarcodeScan = new BarcodeScan();
		BarcodeScan.start();
	}
	
	public class BarcodeScan extends Thread {
		
		public void run () {
			
			while(!ActionActivity.BarcodeCheckFlag);
			
			timer.cancel();
			
			if(ActionActivity.IsCorrectBarcode) HbA1cCalculation();
			
			new Thread(new Runnable() {
			    public void run() {
			        runOnUiThread(new Runnable(){
			            public void run() {
			
			            	HbA1cDisplay();
			            	
			            	btnState = false;
			            	
			            	blankBtn.setEnabled(true);
			    			quickBtn.setEnabled(true);
			    			fullBtn.setEnabled(true);
//			    			scanBtn.setEnabled(true);
			    			escBtn.setEnabled(true);
			            }
			        });
			    }
			}).start();
		}
	}
	
	public synchronized double AbsorbanceMeasure() { // Absorbance measurement
		
		int time = 0;
		String rawValue;
		double douValue = 0;
		
		mSerialPort.BoardTx("VH", SerialPort.CtrTarget.NormalSet);
		
		rawValue = mSerialPort.BoardMessageOutput();			
		
		while(rawValue.length() != 8) {
		
			rawValue = mSerialPort.BoardMessageOutput();			
		
			SerialPort.Sleep(100);
		}	
					
		douValue = Double.parseDouble(rawValue);
		
		return (douValue - RunActivity.BlankValue[0]);
	}
	
	public void BoardMessage(String colRsp, AnalyzerState nextState, String errRsp, AnalyzerState errState, int rspTime) {
		
		int time = 0;
		String temp = "";

		rspTime = rspTime * 10;
		
		while(true) {
			
			temp = mSerialPort.BoardMessageOutput();
			
			if(colRsp.equals(temp)) {
				
				calibState = nextState;
				break;			
				
			} else if(errRsp.equals(temp)) {
				
				calibState = errState;
				break;
			}
			
			if(time++ > rspTime) {
				
				calibState = AnalyzerState.NoResponse;
				break;
			}
			
			SerialPort.Sleep(100);
		}
	}
	
	public synchronized void HbA1cCalculation() {
		
		double A, B, St, Bt, C1, C2, SLA, SMA, SHA, BLA, BMA, BHA, SLV, SMV, SHV, BLV, BMV, BHV, SV, SA, BV, BA, a3, b3, a32, b32, a4, b4;
		
		
		if(HomeActivity.ANALYZER_SW == HomeActivity.NORMAL) {
			
			A = Absorb1stHandling()*RunActivity.RF1_Slope + RunActivity.RF1_Offset;
			B = Absorb2ndHandling()*RunActivity.RF2_Slope + RunActivity.RF2_Offset;
			
		} else {
			
			A = 0.45;
			B = 0.15;	
		
		}
		
		Barcode.a1 = 0.009793532;
		Barcode.b1 = -0.028;
		Barcode.a21 = 0.060055;
		Barcode.b21 = -0.003032;
		Barcode.a22 = 0.05014;
		Barcode.b22 = -0.004829;
		Barcode.a23 = 0.039032;
		Barcode.b23 = -0.005064;
		Barcode.L   = 5.1;
		Barcode.M 	= 7.1;
		Barcode.H   = 11.7;
		Barcode.Asm = 0;
		Barcode.Ass = 0;
		Barcode.Aim = 0;
		Barcode.Ais = 0;
		
		St = (A - Barcode.b1)/Barcode.a1;
		RunActivity.tHbDbl = St;
		Bt = (A - Barcode.b1)/Barcode.a1 + 1;
		
		C1 = St * (Barcode.Asm + Barcode.Ass) + Barcode.Aim + Barcode.Ais;
		C2 = B - C1;
		
		SLA = St * Barcode.L / 100;
		SMA = St * Barcode.M / 100;
		SHA = St * Barcode.H / 100;
		BLA = Bt * Barcode.L / 100;
		BMA = Bt * Barcode.M / 100;
		BHA = Bt * Barcode.H / 100;
		
		SLV = SLA * Barcode.a21 + Barcode.b21;
		SMV = SMA * Barcode.a22 + Barcode.b22;
		SHV = SHA * Barcode.a23 + Barcode.b23;
		BLV = BLA * Barcode.a21 + Barcode.b21;
		BMV = BMA * Barcode.a22 + Barcode.b22;
		BHV = BHA * Barcode.a23 + Barcode.b23;
		
		SV = (SLV + SMV + SHV) / 3;
		SA = (SLA + SMA + SHA) / 3;
		
		a3 = SlopeCalculate(SA, SV, SLA, SLV, SMA, SMV, SHA, SHV);
		b3 = SV - a3*SA;
		
		BV = (BLV + BMV + BHV) / 3;
		BA = (BLA + BMA + BHA) / 3;
		
		a32 = SlopeCalculate(BA, BV, BLA, BLV, BMA, BMV, BHA, BHV);
		b32 = BV - a32*BA;
		
		a4 = (b32 - b3) / (Bt - St);
		b4 = b3 - (a4 * St);
		
		RunActivity.HbA1cValue = (C2 - (St * a4 + b4)) / a3 / St * 100; // %-HbA1c(%)
		
		Log.w("HbA1cCalculation", "C1 : " + C1);
		Log.w("HbA1cCalculation", "HbA1cPctDbl : " + RunActivity.HbA1cValue);
				
		RunActivity.HbA1cValue = RunActivity.CF_Slope * (RunActivity.AF_Slope * RunActivity.HbA1cValue + RunActivity.AF_Offset) + RunActivity.CF_Offset;
	}
	
	public double SlopeCalculate(double x_a, double y_a, double x1, double y1, double x2, double y2,double x3, double y3) {
		
		double slope, numerator, denominator;
		
		numerator = (y1 - y_a)*(x1 - x_a) + (y2 - y_a)*(x2 - x_a) + (y3 - y_a)*(x3 - x_a);
		denominator = (x1 - x_a)*(x1 - x_a) + (x2 - x_a)*(x2 - x_a) + (x3 - x_a)*(x3 - x_a);
		
		slope = numerator / denominator;
		
		return slope;
	}
	
	public double Absorb1stHandling() {
		
		double abs[] = new double[3],
			   dev[] = new double[3],
			   std, 
			   sum, 
			   avg, 
			   res;
		int idx = 0;
		
		abs[0] = RunActivity.Step1stAbsorb1[0] - RunActivity.Step1stAbsorb1[2];
		abs[1] = RunActivity.Step1stAbsorb2[0] - RunActivity.Step1stAbsorb2[2];
		abs[2] = RunActivity.Step1stAbsorb3[0] - RunActivity.Step1stAbsorb3[2];
		
		std = (abs[0] + abs[1] + abs[2]) / 3;
		
		for(int i = 0; i < 3; i++) {
			
			if(std > abs[i]) dev[i] = std - abs[i];
			else dev[i] = abs[i] - std;
		}
		
		if(dev[0] > dev[1]) idx = 0; 
		else idx = 1;
		
		if(dev[2] > dev[idx]) idx = 2;
		
		sum = abs[0] + abs[1] + abs[2];
		
		avg = (sum - abs[idx]) / 2;
		
		return avg;
	}
	
	public double Absorb2ndHandling() {
		
		double abs[] = new double[3],
			   dev[] = new double[3],
			   std, 
			   sum, 
			   avg, 
			   res;
		int idx = 0;
		
		abs[0] = RunActivity.Step2ndAbsorb1[1] - RunActivity.Step2ndAbsorb1[2];
		abs[1] = RunActivity.Step2ndAbsorb2[1] - RunActivity.Step2ndAbsorb2[2];
		abs[2] = RunActivity.Step2ndAbsorb3[1] - RunActivity.Step2ndAbsorb3[2];
		
		std = (abs[0] + abs[1] + abs[2]) / 3;
				
		for(int i = 0; i < 3; i++) {
			
			if(std > abs[i]) dev[i] = std - abs[i];
			else dev[i] = abs[i] - std;
		}
		
		if(dev[0] > dev[1]) idx = 0; 
		else idx = 1;
		
		if(dev[2] > dev[idx]) idx = 2;
		
		sum = abs[0] + abs[1] + abs[2];
		
		avg = (sum - abs[idx]) / 2;
		
		return avg;
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home			:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
		
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
