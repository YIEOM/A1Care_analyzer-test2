package isens.hba1c_analyzer;

import isens.hba1c_analyzer.Model.ConvertModel;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RunActivity extends Activity {
	
	/* Instruction to motor for filter */
	final static String HOME_POSITION    = "CH", //CO
						MEASURE_POSITION = "CM", //MO
						Step1st_POSITION = "C1",
						Step2nd_POSITION = "C2",
						CARTRIDGE_DUMP   = "CD",
						FILTER_DARK      = "FD", //DO
						FILTER_SPto535nm = "FR", //RO
						FILTER_535nm     = "AR", 
						FILTER_660nm     = "FG", //AG
						FILTER_750nm     = "FB", //AB	
						OPERATE_COMPLETE = "DO",
						MOTOR_COMPLETE   = "AR",
						NEXT_FILTER		 = "FS",
						MOTOR_STOP		 = "MS",
						FILTER_ERROR 	 = "FE1",
						CARTRIDGE_ERROR	 = "CE1",
						ERROR_DOOR       = "ED";
	
	public final static byte NORMAL_OPERATION = 0;

	static final byte DEMO_OPERATION   = 1;

	final static byte FIRST_SHAKING_TIME = 105, // Motor shaking time, default : 6 * 105(sec) = 0630
					  SECOND_SHAKING_TIME = 90; // Motor shaking time, default : 6 * 90(sec) = 0540
	
	public enum AnalyzerState {InitPosition, Step1Position, Step1Shaking, Step2Position, Step2Shaking, MeasurePosition, FilterDark, Filter535nm, Filter660nm, Filter750nm, FilterHome, CartridgeHome, CartridgeDump, MeasureDark, Measure535nm, Measure660nm, Measure750nm, NoResponse, NoWorking, ShakingMotorError, FilterMotorError, PhotoSensorError, LampError, NormalOperation, Stop, ErrorCover}

	public static boolean MotorShakeFlag = false;
	
	public ErrorPopup mErrorPopup;
	public TimerDisplay mTimerDisplay;
	public SerialPort mSerialPort;
	
	public DecimalFormat ShkDf = new DecimalFormat("0000");
	
	public Handler runHandler = new Handler();
	public Timer runningTimer;
	
	public Button escIcon;
	
	public TextView runTimeText,
					warningText;
	
	public ImageView barani;
	
	public static double BlankValue[]     = new double[4],
						 Step1stValue1[]  = new double[3],
						 Step1stValue2[]  = new double[3],
						 Step1stValue3[]  = new double[3],
						 Step2ndValue1[]  = new double[3],
						 Step2ndValue2[]  = new double[3],
						 Step2ndValue3[]  = new double[3],
						 Step1stAbsorb1[] = new double[3],
						 Step1stAbsorb2[] = new double[3],
						 Step1stAbsorb3[] = new double[3],
						 Step2ndAbsorb1[] = new double[3],
						 Step2ndAbsorb2[] = new double[3],
						 Step2ndAbsorb3[] = new double[3];
	
	public static boolean isRun,
						  isStop,
						  isError;
	
	public static double tHbDbl,
						 HbA1cValue,
						 douValue;
	
	public static float AF_Slope,
						AF_Offset,
						CF_Slope,
						CF_Offset,
						RF1_Slope,
						RF1_Offset,
						RF2_Slope,
						RF2_Offset,
						SF_F1,
						SF_F2;
	
	public AnalyzerState runState;
	
	public int checkError = NORMAL_OPERATION;
	
	public double A;
	
	public boolean btnState = false;

	public byte runSec,
				runMin;

	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.run);
		
		mSerialPort = new SerialPort(); // to test
		
		/* esc pop-up window activation */
		escIcon = (Button)findViewById(R.id.escicon);
		escIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
				
					ESC();
					
					btnState = false;
				}
			}
		});
	
		RunInit();
	}
	
	public class Cart1stShaking extends Thread { // First shaking motion

		public void run() {
			
			BarAnimation(165);
			
			for(int i = 0; i < 4; i++) {
				
				checkMode();
				
				switch(runState) {
				
				case InitPosition		:
					MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);
					BoardMessage(HOME_POSITION, AnalyzerState.Step1Position, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					break;
				
				case Step1Position	:
					MotionInstruct(Step1st_POSITION, SerialPort.CtrTarget.NormalSet);
					BarAnimation(168);
					BoardMessage(Step1st_POSITION, AnalyzerState.Step1Shaking, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					BarAnimation(171);
					SerialPort.Sleep(500);
					break;
					
				case Step1Shaking	:
					/* TEST Mode */
					if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL || HomeActivity.ANALYZER_SW == HomeActivity.TEST) {
						
						MotionInstruct(ShkDf.format(30), SerialPort.CtrTarget.MotorSet);
						ShakingAniThread ShakingAniThreadObj = new ShakingAniThread(174, 5);
						ShakingAniThreadObj.start();
						
					} else if(HomeActivity.ANALYZER_SW == HomeActivity.DEMO) {
					
						MotionInstruct(ShkDf.format(18), SerialPort.CtrTarget.MotorSet);
						ShakingAniThread ShakingAniThreadObj = new ShakingAniThread(174, 3);
						ShakingAniThreadObj.start();
					
					} else {
					
						MotionInstruct(ShkDf.format(FIRST_SHAKING_TIME * 6), SerialPort.CtrTarget.MotorSet);  // Motor shaking time, default : 6.5 * 10(sec) = 0065
						ShakingAniThread ShakingAniThreadObj = new ShakingAniThread(174, FIRST_SHAKING_TIME);
						ShakingAniThreadObj.start();
					}
					MotorShakeFlag = true;
					BoardMessage(MOTOR_COMPLETE, AnalyzerState.NormalOperation, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 120);
					MotorShakeFlag = false;
					
					if(HomeActivity.ANALYZER_SW == HomeActivity.DEMO) {
						
						checkError = DEMO_OPERATION;
						runState = AnalyzerState.Step2Position;
						
						Cart2ndShaking Cart2ndShakingObjDemo = new Cart2ndShaking();
						Cart2ndShakingObjDemo.start();
					}
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					endRun(false);
					break;
					
				case NoResponse 	:
					Log.w("Cart1stShaking", "NR : " + checkError);
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case Stop			 :
					checkError = R.string.stop;
					runState = AnalyzerState.NoWorking;
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					runState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
					
			switch(checkError) {
			
			case NORMAL_OPERATION	:
				Cart1stFilter1 Cart1stFilter1Obj = new Cart1stFilter1();
				Cart1stFilter1Obj.start();	
				break;
				
			case R.string.stop		:
				Log.w("Cart1stShaking", "Stop : " + checkError);
				CartDump CartDumpObj = new CartDump(checkError);
				CartDumpObj.start();
				break;
				
			case R.string.e322		:
				mErrorPopup.ErrorDisplay(R.string.w004);					
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
				break;
				
			default	:
				break;
			}
			
		}
	}

	public class Cart1stFilter1 extends Thread { // First filter motion of first shaking 

		public void run() {

			BarAnimation(279);
			SerialPort.Sleep(2000);
			BarAnimation(282);
			
			runState = AnalyzerState.MeasurePosition;
			
			for(int i = 0; i < 6; i++) {
				
				checkMode();
				
				switch(runState) {
				
				case MeasurePosition	:
					MotionInstruct(MEASURE_POSITION, SerialPort.CtrTarget.NormalSet);
					BarAnimation(285);
					BoardMessage(MEASURE_POSITION, AnalyzerState.Filter535nm, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					BarAnimation(288);
					break;
					
				case Filter535nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(291);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter660nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(294);
					Step1stValue1[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(297);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter750nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 5);
					BarAnimation(300);					
					Step1stValue1[1] = AbsorbanceMeasure(); // 660nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(303);
					BoardMessage(NEXT_FILTER, AnalyzerState.FilterDark, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(306);
					Step1stValue1[2] = AbsorbanceMeasure(); // 750nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BarAnimation(309);
					BoardMessage(FILTER_DARK, AnalyzerState.NormalOperation, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(312);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					endRun(false);
					break;
					
				case NoResponse			:
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
				
				case Stop				:
					checkError = R.string.stop;
					runState = AnalyzerState.NoWorking;
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					runState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			switch(checkError) {
			
			case NORMAL_OPERATION	:
				Cart1stFilter2 Cart1stFilter2Obj = new Cart1stFilter2();
				Cart1stFilter2Obj.start();	
				break;
				
			case R.string.stop		:
				CartDump CartDumpObj = new CartDump(checkError);
				CartDumpObj.start();
				break;
			
			case R.string.e322		:
				mErrorPopup.ErrorDisplay(R.string.w004);					
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
				break;
				
			default	:
				break;
			}
		}
	}

	public class Cart1stFilter2 extends Thread { // Second filter motion of first shaking

		public void run() {
			
			SerialPort.Sleep(1000);
			BarAnimation(315);
			
			runState = AnalyzerState.Filter535nm;
			
			for(int i = 0; i < 5; i++) {
				
				checkMode();
				
				switch(runState) {
				
				case Filter535nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(318);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter660nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(321);
					Step1stValue2[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(324);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter750nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(327);
					Step1stValue2[1] = AbsorbanceMeasure(); // 660nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(330);
					BoardMessage(NEXT_FILTER, AnalyzerState.FilterDark, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(333);
					Step1stValue2[2] = AbsorbanceMeasure(); // 750nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BarAnimation(336);
					BoardMessage(FILTER_DARK, AnalyzerState.NormalOperation, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(339);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					endRun(false);
					break;
					
				case NoResponse :
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case Stop			 :
					checkError = R.string.stop;
					runState = AnalyzerState.NoWorking;
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					runState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			switch(checkError) {
				
			case NORMAL_OPERATION	:
				Cart1stFilter3 Cart1stFilter3Obj = new Cart1stFilter3();
				Cart1stFilter3Obj.start();
				break;
				
			case R.string.stop		:
				CartDump CartDumpObj = new CartDump(checkError);
				CartDumpObj.start();
				break;
			
			case R.string.e322		:
				mErrorPopup.ErrorDisplay(R.string.w004);					
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
				break;
				
			default	:
				break;
			}	
		}
	}
	
	public class Cart1stFilter3 extends Thread { // Third filter motion of first shaking

		public void run() {
			
			SerialPort.Sleep(1000);
			BarAnimation(342);
			
			runState = AnalyzerState.Filter535nm;
			
			for(int i = 0; i < 5; i++) {
				
				checkMode();
				
				switch(runState) {
				
				case Filter535nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(345);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter660nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(348);
					Step1stValue3[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(351);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter750nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(354);
					Step1stValue3[1] = AbsorbanceMeasure(); // 660nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(357);
					BoardMessage(NEXT_FILTER, AnalyzerState.FilterDark, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(360);
					Step1stValue3[2] = AbsorbanceMeasure(); // 750nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BarAnimation(363);
					BoardMessage(FILTER_DARK, AnalyzerState.NormalOperation, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(366);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					endRun(false);
					break;
					
				case NoResponse :
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case Stop			 :
					checkError = R.string.stop;
					runState = AnalyzerState.NoWorking;
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					runState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			switch(checkError) {
			
			case NORMAL_OPERATION	:
				checkError = tHbCalculate();
				
				if(checkError == NORMAL_OPERATION) {
					
					Cart2ndShaking Cart2ndShakingObj = new Cart2ndShaking();
					Cart2ndShakingObj.start();
				
				} else {
					
					CartDump CartDumpObj = new CartDump(checkError);
					CartDumpObj.start();
				}
				
				break;
				
			case R.string.stop		:
				CartDump CartDumpObj = new CartDump(checkError);
				CartDumpObj.start();
				break;
			
			case R.string.e322		:
				mErrorPopup.ErrorDisplay(R.string.w004);					
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
				break;
				
			default	:
				break;
			}	
		}
	}
	
	public class Cart2ndShaking extends Thread { // Second shaking motion
		
		public void run() {			
			
			runState = AnalyzerState.Step2Position;
						
			for(int i = 0; i < 3; i++) {
				
				checkMode();
				
				switch(runState) {
				
				case Step2Position	:
					MotionInstruct(Step2nd_POSITION, SerialPort.CtrTarget.NormalSet);
					BarAnimation(369);
					BoardMessage(Step2nd_POSITION, AnalyzerState.Step2Shaking, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(372);
					SerialPort.Sleep(500);
					break;
					
				case Step2Shaking	:
					/* TEST Mode */
					if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL || HomeActivity.ANALYZER_SW == HomeActivity.TEST) {
						
						MotionInstruct(ShkDf.format(30), SerialPort.CtrTarget.MotorSet);
						ShakingAniThread ShakingAniThreadObj = new ShakingAniThread(375, 5);
						ShakingAniThreadObj.start();
						
					} else if(HomeActivity.ANALYZER_SW == HomeActivity.DEMO) {
					
						MotionInstruct(ShkDf.format(18), SerialPort.CtrTarget.MotorSet);
						ShakingAniThread ShakingAniThreadObj = new ShakingAniThread(375, 3);
						ShakingAniThreadObj.start();
					
					} else {
						
						MotionInstruct(ShkDf.format(SECOND_SHAKING_TIME * 6), SerialPort.CtrTarget.MotorSet);  // Motor shaking time, default : 6 * 10(sec) = 0065
						ShakingAniThread ShakingAniThreadObj = new ShakingAniThread(375, SECOND_SHAKING_TIME);
						ShakingAniThreadObj.start();
					}
						
					MotorShakeFlag = true;
					BoardMessage(MOTOR_COMPLETE, AnalyzerState.NormalOperation, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 110);
					MotorShakeFlag = false;
					
					if(HomeActivity.ANALYZER_SW == HomeActivity.DEMO) {
						
						checkError = DEMO_OPERATION;
						runState = AnalyzerState.CartridgeDump;
						
						CartDump CartDumpObjDemo = new CartDump(NORMAL_OPERATION);
						CartDumpObjDemo.start();
					}
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					endRun(false);
					break;
					
				case NoResponse :
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case Stop			 :
					checkError = R.string.stop;
					runState = AnalyzerState.NoWorking;
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					runState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			switch(checkError) {
			
			case NORMAL_OPERATION	:
				Cart2ndFilter1 Cart2ndFilter1Obj = new Cart2ndFilter1();
				Cart2ndFilter1Obj.start();
				break;
				
			case R.string.stop		:
				CartDump CartDumpObj = new CartDump(checkError);
				CartDumpObj.start();
				break;
			
			case R.string.e322		:
				mErrorPopup.ErrorDisplay(R.string.w004);					
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
				break;
				
			default	:
				break;
			}
		}
	}
	
	public class Cart2ndFilter1 extends Thread { // First filter motion of second shaking
		
		public void run() {			

			BarAnimation(484);	
			SerialPort.Sleep(2000);
			BarAnimation(487);
			
			runState = AnalyzerState.MeasurePosition;
			
			for(int i = 0; i < 6; i++) {
				
				checkMode();
				
				switch(runState) {
				
				case MeasurePosition	:
					MotionInstruct(MEASURE_POSITION, SerialPort.CtrTarget.NormalSet);
					BarAnimation(490);
					BoardMessage(MEASURE_POSITION, AnalyzerState.Filter535nm, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					BarAnimation(493);
					break;
					
				case Filter535nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(496);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter660nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(499);
					Step2ndValue1[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(502);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter750nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(505);
					Step2ndValue1[1] = AbsorbanceMeasure(); // 660nm Absorbance
					break;
				
				case Filter750nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(508);
					BoardMessage(NEXT_FILTER, AnalyzerState.FilterDark, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(511);
					Step2ndValue1[2] = AbsorbanceMeasure(); // 750nm Absorbance
					break;
				
				case FilterDark		:
					MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BarAnimation(514);
					BoardMessage(FILTER_DARK, AnalyzerState.NormalOperation, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(517);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					endRun(false);
					break;
					
				case NoResponse :
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case Stop			 :
					checkError = R.string.stop;
					runState = AnalyzerState.NoWorking;
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					runState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			switch(checkError) {
			
			case NORMAL_OPERATION	:
				Cart2ndFilter2 Cart2ndFilter2Obj = new Cart2ndFilter2();
				Cart2ndFilter2Obj.start();
				break;
				
			case R.string.stop		:
				CartDump CartDumpObj = new CartDump(checkError);
				CartDumpObj.start();
				break;
				
			case R.string.e322		:
				mErrorPopup.ErrorDisplay(R.string.w004);					
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
				break;
			
			default	:
				break;
				}
		}
	}
	
	public class Cart2ndFilter2 extends Thread { // Second filter motion of second shaking
		
		public void run() {
			
			SerialPort.Sleep(1000);
			BarAnimation(520);
						
			runState = AnalyzerState.Filter535nm;
			
			for(int i = 0; i < 5; i++) {
				
				checkMode();
				
				switch(runState) {
				
				case Filter535nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(523);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter660nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(526);
					Step2ndValue2[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(529);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter750nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(532);
					Step2ndValue2[1] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(535);
					BoardMessage(NEXT_FILTER, AnalyzerState.FilterDark, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(538);
					Step2ndValue2[2] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BarAnimation(541);
					BoardMessage(FILTER_DARK, AnalyzerState.NormalOperation, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(544);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					endRun(false);
					break;
					
				case NoResponse :
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case Stop			 :
					checkError = R.string.stop;
					runState = AnalyzerState.NoWorking;
					break;
					
				case ErrorCover		:
					checkError = R.string.e322;
					runState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}
			
			switch(checkError) {
			
			case NORMAL_OPERATION	:
				Cart2ndFilter3 Cart2ndFilter3Obj = new Cart2ndFilter3();
				Cart2ndFilter3Obj.start();
				break;
				
			case R.string.stop		:
				CartDump CartDumpObj = new CartDump(checkError);
				CartDumpObj.start();
				break;
				
			case R.string.e322		:
				mErrorPopup.ErrorDisplay(R.string.w004);					
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
				break;
			
			default	:
				break;
			}
		}
	}
	
	public class Cart2ndFilter3 extends Thread { // Third filter motion of second shaking
		
		public void run() {
			
			SerialPort.Sleep(1000);
			BarAnimation(547);
			
			runState = AnalyzerState.Filter535nm;
			
			for(int i = 0; i < 5; i++) {
				
				checkMode();
				
				switch(runState) {
				
				case Filter535nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(550);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter660nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(553);
					Step2ndValue3[0] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter660nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(556);
					BoardMessage(NEXT_FILTER, AnalyzerState.Filter750nm, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(559);
					Step2ndValue3[1] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case Filter750nm	:
					MotionInstruct(NEXT_FILTER, SerialPort.CtrTarget.NormalSet);
					BarAnimation(562);
					BoardMessage(NEXT_FILTER, AnalyzerState.FilterDark, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(565);
					Step2ndValue3[2] = AbsorbanceMeasure(); // 535nm Absorbance
					break;
					
				case FilterDark		:
					MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.NormalSet);
					BarAnimation(568);
					BoardMessage(FILTER_DARK, AnalyzerState.NormalOperation, RunActivity.FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
					BarAnimation(571);
					break;
					
				case ShakingMotorError	:
					checkError = R.string.e211;
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
					
				case FilterMotorError	:
					checkError = R.string.e212;
					MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
					BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
					endRun(false);
					break;
					
				case NoResponse :
					runState = AnalyzerState.NoWorking;
					endRun(false);
					break;
				
				case Stop			 :
					checkError = R.string.stop;
					runState = AnalyzerState.NoWorking;
					break;
				
				case ErrorCover		:
					checkError = R.string.e322;
					runState = AnalyzerState.NoWorking;
					break;
					
				default	:
					break;
				}
			}			
			
			switch(checkError) {
			
			case NORMAL_OPERATION	:
				checkError = HbA1cCalculate();
				
			case R.string.stop		:
				CartDump CartDumpObj = new CartDump(checkError);
				CartDumpObj.start();
				break;
				
			case R.string.e322		:
				mErrorPopup.ErrorDisplay(R.string.w004);					
				CheckCoverError mCheckCoverError = new CheckCoverError();
				mCheckCoverError.start();
				break;
			
			default	:
				break;
			}			
		}
	}
	
	public class CartDump extends Thread { // Cartridge dumping motion
		
		private int whichState;
		
		CartDump (int whichState) {
			
			this.whichState = whichState;
		}
		
		public void run() {
						
			switch(whichState) {
			
			case NORMAL_OPERATION	:
				
				runState = AnalyzerState.CartridgeDump;
				
				for(int i = 0; i < 3; i++) {
					
					checkMode();
					
					switch(runState) {
					
					case CartridgeDump	:
						MotionInstruct(CARTRIDGE_DUMP, SerialPort.CtrTarget.NormalSet);
						BarAnimation(574);
						BoardMessage(CARTRIDGE_DUMP, AnalyzerState.CartridgeHome, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
						BarAnimation(577);
						break;
						
					case CartridgeHome	:
						MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);
						BarAnimation(580);
						BoardMessage(HOME_POSITION, AnalyzerState.Step1Position, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
						BarAnimation(583);
						break;
						
					case ShakingMotorError	:
						checkError = R.string.e211;
						runState = AnalyzerState.NoWorking;
						endRun(false);
						break;
						
					case FilterMotorError	:
						checkError = R.string.e212;
						MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
						BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
						endRun(false);
						break;
						
					case NoResponse :
						runState = AnalyzerState.NoWorking;
						endRun(false);
						break;
						
					case ErrorCover		:
						checkError = R.string.e322;
						runState = AnalyzerState.NoWorking;
						break;
						
					default	:
						break;
					}
				}
				
				if(runState == AnalyzerState.Step1Position) {
					
					BarAnimation(586);
					
					endRun(true);
					
				} else if(checkError == R.string.e322) {
					
					mErrorPopup.ErrorDisplay(R.string.w004);
					CheckCoverError mCheckCoverError = new CheckCoverError();
					mCheckCoverError.start();
				}
				break;
				
			default	:
				
				isStop = false;
				isError = false;
				runState = AnalyzerState.FilterDark;
				
				for(int i = 0; i < 6; i++) {
					
					checkMode();
					
					switch(runState) {
					
					case FilterDark	:
						MotionInstruct(FILTER_DARK, SerialPort.CtrTarget.NormalSet);
						BoardMessage(FILTER_DARK, AnalyzerState.InitPosition, FILTER_ERROR, AnalyzerState.FilterMotorError, 10);
						break;
					
					case InitPosition		:
						MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);
						BoardMessage(HOME_POSITION, AnalyzerState.MeasurePosition, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
						break;
						
					case MeasurePosition	:
						MotionInstruct(MEASURE_POSITION, SerialPort.CtrTarget.NormalSet);
						BoardMessage(MEASURE_POSITION, AnalyzerState.CartridgeDump, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
						break;
						
					case CartridgeDump	:
						MotionInstruct(CARTRIDGE_DUMP, SerialPort.CtrTarget.NormalSet);
						BoardMessage(CARTRIDGE_DUMP, AnalyzerState.CartridgeHome, CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
						break;
						
					case CartridgeHome	:
						MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);
						BoardMessage(HOME_POSITION, AnalyzerState.Step1Position, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
						break;
						
					case ShakingMotorError	:
						checkError = R.string.e211;
						runState = AnalyzerState.NoWorking;
						endRun(false);
						break;
						
					case FilterMotorError	:
						checkError = R.string.e212;
						MotionInstruct(HOME_POSITION, SerialPort.CtrTarget.NormalSet);			
						BoardMessage(HOME_POSITION, AnalyzerState.NoWorking, RunActivity.CARTRIDGE_ERROR, AnalyzerState.ShakingMotorError, 10);
						endRun(false);
						break;
						
					case NoResponse :
						runState = AnalyzerState.NoWorking;
						endRun(false);
						break;
						
					case ErrorCover		:
						checkError = R.string.e322;
						runState = AnalyzerState.NoWorking;
						
						break;
						
					default	:
						break;
					}
				}
				
				if(runState == AnalyzerState.Step1Position) {
				
					BarAnimation(586);
					
					endRun(false);
				
				} else if(checkError == R.string.e322) {
					
					mErrorPopup.ErrorDisplay(R.string.w004);
					CheckCoverError mCheckCoverError = new CheckCoverError();
					mCheckCoverError.start();
				}
				break;
			}
		}
	}
	
	public void RunTimeDisplay(Activity activity) { // Display running time
		
		if(isRun) {
			
			runTimeText = (TextView) activity.findViewById(R.id.runTimeText);
			warningText = (TextView) activity.findViewById(R.id.warningText);
			
			runTimeText.setText(Integer.toString(runMin) + " min " + Integer.toString(runSec) + " sec");
			warningText.setText(R.string.w003);
			if(runSec % 2 == 1) warningText.setTextColor(Color.parseColor("#FF0000"));
			else warningText.setTextColor(Color.parseColor("#FFFFFF"));
			
			if(runSec-- == 0) {
				
				if(runMin == 0) isRun = false;
				
				runMin--;
				runSec = 59;
			}
			
		} else {
			
			runTimeText.setText("");
		}
	}
	
	public void RunInit() {
		
//		Log.w("RunInit", "run");
		
		MotorShakeFlag = false;
		isStop = false;
		isError = false;
		runState = AnalyzerState.InitPosition;
		checkError = NORMAL_OPERATION;
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.runlayout);
		
		mErrorPopup = new ErrorPopup(this, this, R.id.runlayout);
		
		BarAnimation(162);
		RunTimerInit(this);
		
		TimerDisplay.RXBoardFlag = true;
		Cart1stShaking Cart1stShakingObj = new Cart1stShaking(); // to test
		Cart1stShakingObj.start(); // to test
	}
	
	public void RunTimerInit(final Activity activity) {

		isRun = true;
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) {
			
			runMin = 1;
			runSec = 20;
			
		} else if(HomeActivity.ANALYZER_SW == HomeActivity.DEMO) {
			
			runMin = 0;
			runSec = 20;
			
		} else {
			
			runMin = 4;
			runSec = 20;
		}
		
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
	
	public void MotionInstruct(String str, SerialPort.CtrTarget target) { // Motion of system instruction
		
		mSerialPort = new SerialPort();
		mSerialPort.BoardTx(str, target);
		
		Log.w("MotionInstruct", "instruction : " + str);
	}

	public void BoardMessage(String colRsp, AnalyzerState nextState, String errRsp, AnalyzerState errState, int rspTime) {
		
		int time = 0;
		String temp = "";
		
		rspTime = rspTime * 10;
		
		while(true) {
			
			temp = mSerialPort.BoardMessageOutput();

			if(temp.equals(colRsp)) {
					
				runState = nextState;
				break;
			
			} else if(temp.equals(errRsp)) {
				
				runState = errState;
				break;
			
			} else if(temp.equals(MOTOR_STOP)) {
				
				isStop = true;
				break;
			}

			if(time++ > rspTime) {
				
				runState = AnalyzerState.NoResponse;
				checkError = R.string.e241;
				break;
			}
			
			if(isError) break;
			
			SerialPort.Sleep(100);
		}
	}
	
	public synchronized double AbsorbanceMeasure() { // Absorbance measurement
		
		int time = 0;
		String rawValue;
		
		mSerialPort.BoardTx("VH", SerialPort.CtrTarget.NormalSet);
		Log.w("AbsorbanceMeasure", "instruction : " + "VH");
		
		do {
		
			time++;
			rawValue = mSerialPort.BoardMessageOutput();			
			 
			if(time > 50) {
				
				runState = AnalyzerState.NoResponse;
				checkError = R.string.e241;
				break;
			}
				
			if(isError) break;
			
			SerialPort.Sleep(100);
			
		} while(rawValue.length() != 8);
		
		try {
			
			douValue = Double.parseDouble(rawValue);	
		
		} catch(NumberFormatException e) {
			
			douValue = 0.0;
		}
		
		return (douValue - BlankValue[0]);	
	}
	
	public int tHbCalculate() {
		
		A = Absorb1stHandling()*RF1_Slope + RF1_Offset;
//		Log.w("tHb Calucation", "thb A' : " + A);s
		
		/* TEST Mode */
		if(HomeActivity.ANALYZER_SW != HomeActivity.NORMAL) return NORMAL_OPERATION;
		else {
		
			
		if(A < 0.168) {
			
			return R.string.e111;
		
		} else if(A > 0.63) {
			
			return R.string.e112;
		
		} else 
			return NORMAL_OPERATION;
		
		
		}
	}
	
	public int HbA1cCalculate() { // Calculation for HbA1c percentage
		
		double B, St, Bt, C1, C2, SLA, SMA, SHA, BLA, BMA, BHA, SLV, SMV, SHV, BLV, BMV, BHV, SV, SA, BV, BA, a3, b3, a32, b32, a4, b4;
					
		if(HomeActivity.ANALYZER_SW == HomeActivity.NORMAL) {
			
			B = Absorb2ndHandling()*RF2_Slope + RF2_Offset;
			
		} else {
			
			A = 0.45;
			B = 0.15;	
		}
		
//		Log.w("tHb Calucation", "thb B' : " + B);
		St = (A - Barcode.b1)/Barcode.a1;
		tHbDbl = St;
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
		
		HbA1cValue = (C2 - (St * a4 + b4)) / a3 / St * 100; // %-HbA1c(%)
//		Log.w("tHb Calucation", "HbA1cPctDbl : " + HbA1cValue);
		
		HbA1cValue = (Barcode.Sm + Barcode.Ss) * HbA1cValue + (Barcode.Im + Barcode.Is);
		
		HbA1cValue = CF_Slope * (AF_Slope * HbA1cValue + AF_Offset) + CF_Offset;
		
//		Log.w("tHb Calucation", "C1 : " + C1);
//		Log.w("tHb Calucation", "SV : " + SV + "\nSA : " + SA + "\na3 : " + a3 + "\nb3 : " + b3 + "\nBV : " + BV + "\nBA : " + BA + "\na32 : " + a32 + "\nb32 : " + b32);
//		Log.w("tHb Calucation", "HbA1cPctDbl : " + HbA1cValue);
		
		/* TEST Mode */
		if(HomeActivity.ANALYZER_SW != HomeActivity.NORMAL) return NORMAL_OPERATION;
		else {
		
		
		if(HbA1cValue < 4) {
			
			return R.string.e121;
		
		} else if(HbA1cValue > 15) {
			
			return R.string.e122;
		
		} else
			
			return NORMAL_OPERATION;
		}
	}
	
	public double SlopeCalculate(double x_a, double y_a, double x1, double y1, double x2, double y2,double x3, double y3) {
		
		double slope, numerator, denominator;
		
		numerator = (y1 - y_a)*(x1 - x_a) + (y2 - y_a)*(x2 - x_a) + (y3 - y_a)*(x3 - x_a);
		denominator = (x1 - x_a)*(x1 - x_a) + (x2 - x_a)*(x2 - x_a) + (x3 - x_a)*(x3 - x_a);
		
		slope = numerator/denominator;
		
		return slope;
	}
	
	public double ConvertHbA1c(byte primary) {
		
		double hbA1cValue;
		
		if(primary == ConvertModel.NGSP) return HbA1cValue;
		
		else {
			
			hbA1cValue = (HbA1cValue-2.152)/0.09148;
			
			return hbA1cValue;	
		}
	}
	
	
	public double Absorb1stHandling() {
		
		double abs[] = new double[3],
			   dev[] = new double[3],
			   std, 
			   sum, 
			   avg, 
			   res;
		int idx = 0;
		
		/* Step 1st Absorbance */
		Step1stAbsorb1[0] = -Math.log10(Step1stValue1[0]/BlankValue[1]); // 535nm
		Step1stAbsorb1[1] = -Math.log10(Step1stValue1[1]/BlankValue[2]); // 660nm
		Step1stAbsorb1[2] = -Math.log10(Step1stValue1[2]/BlankValue[3]); // 750nm
		
		Step1stAbsorb2[0] = -Math.log10(Step1stValue2[0]/BlankValue[1]);
		Step1stAbsorb2[1] = -Math.log10(Step1stValue2[1]/BlankValue[2]);
		Step1stAbsorb2[2] = -Math.log10(Step1stValue2[2]/BlankValue[3]);
		
		Step1stAbsorb3[0] = -Math.log10(Step1stValue3[0]/BlankValue[1]);
		Step1stAbsorb3[1] = -Math.log10(Step1stValue3[1]/BlankValue[2]);
		Step1stAbsorb3[2] = -Math.log10(Step1stValue3[2]/BlankValue[3]);
		
		abs[0] = Step1stAbsorb1[0] - Step1stAbsorb1[2];
		abs[1] = Step1stAbsorb2[0] - Step1stAbsorb2[2];
		abs[2] = Step1stAbsorb3[0] - Step1stAbsorb3[2];
		
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
		
//		Log.w("Absorb1stHandling", "thb A : " + avg);
		
		return avg;
	}
	
	public double Absorb2ndHandling() {
		
		double abs[] = new double[3],
			   dev[] = new double[3],
			   std, 
			   sum, 
			   avg;
		int idx = 0;
		
		/* Step 2nd Absorbance */
		Step2ndAbsorb1[0] = -Math.log10(Step2ndValue1[0]/BlankValue[1]); // 535nm
		Step2ndAbsorb1[1] = -Math.log10(Step2ndValue1[1]/BlankValue[2]); // 660nm
		Step2ndAbsorb1[2] = -Math.log10(Step2ndValue1[2]/BlankValue[3]); // 750nm
		
		Step2ndAbsorb2[0] = -Math.log10(Step2ndValue2[0]/BlankValue[1]);
		Step2ndAbsorb2[1] = -Math.log10(Step2ndValue2[1]/BlankValue[2]);
		Step2ndAbsorb2[2] = -Math.log10(Step2ndValue2[2]/BlankValue[3]);
		
		Step2ndAbsorb3[0] = -Math.log10(Step2ndValue3[0]/BlankValue[1]);
		Step2ndAbsorb3[1] = -Math.log10(Step2ndValue3[1]/BlankValue[2]);
		Step2ndAbsorb3[2] = -Math.log10(Step2ndValue3[2]/BlankValue[3]);
		
		abs[0] = Step2ndAbsorb1[1] - Step2ndAbsorb1[2];
		abs[1] = Step2ndAbsorb2[1] - Step2ndAbsorb2[2];
		abs[2] = Step2ndAbsorb3[1] - Step2ndAbsorb3[2];
		
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
		
//		Log.w("Absorb2ndHandling", "thb B : " + avg);
		
		return avg;
	}

	public class ShakingAniThread extends Thread {
		
		private int coordinates,
					time;
		
		ShakingAniThread(int coordinates, int time) {
			
			this.coordinates = coordinates;
			this.time = time;
		}
		
		public void run() {
			
			for(int i = 0; i < time/2; i++) {
				
	        	BarAnimation(coordinates + i*2);
	        	SerialPort.Sleep(2000);
	        	
	        	if(checkError != NORMAL_OPERATION) break;
			}
		}
	}
	
	public void BarAnimation(final int x) { // running bar animation

		barani = (ImageView) findViewById(R.id.progressBar);
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		
		            	ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(barani.getLayoutParams());
		            	margin.setMargins(x, 276, 0, 0);
		            	barani.setLayoutParams(new RelativeLayout.LayoutParams(margin));
		            }
		        });
		    }
		}).start();	
	}
	
	public void ESC() {
		
		mErrorPopup.OXBtnDisplay(R.string.esc);
	}
	
	public void RunStop() {
		
		isRun = false;
		
		if(MotorShakeFlag) MotionInstruct(MOTOR_STOP, SerialPort.CtrTarget.NormalSet);
		else isStop = true;
	}
	
	public void checkMode() {
		
		if(isError) {
			
			runState = AnalyzerState.ErrorCover;
		
		} else if(!isError && isStop) {
			
			runState = AnalyzerState.Stop;
		}
	}
	
	public class CheckCoverError extends Thread {
		
		public void run() {
			
			isRun = false;
			
			GpioPort.DoorActState = true;			
			GpioPort.CartridgeActState = true;
			
			SerialPort.Sleep(2000);
			
			while(ActionActivity.DoorCheckFlag != 1) SerialPort.Sleep(100);
			
			GpioPort.DoorActState = false;			
			GpioPort.CartridgeActState = false;
			
			mErrorPopup.ErrorDisplay(R.string.wait);
			
			CartDump CartDumpObj = new CartDump(checkError);
			CartDumpObj.start();
		}
	}
	
	public void endRun(boolean state) {
		
		isRun = state;
		
		EndRun mEndRun = new EndRun();
		mEndRun.start();
	}
	
	public class EndRun extends Thread {
		
		public void run() {
			
			TimerDisplay.RXBoardFlag = false;
			
			mErrorPopup.ErrorPopupClose();
			
			while(isRun) SerialPort.Sleep(100);
			
			runningTimer.cancel();
			
			SerialPort.Sleep(200);
			
			whichIntent();
		}
	}
	
	public void whichIntent() { // Activity conversion

		Intent nextIntent = new Intent(getApplicationContext(), ResultActivity.class);
		
		nextIntent.putExtra("RunState", checkError); // Error operation
		startActivity(nextIntent);
		
		finish();
	}
	
	public void finish() {
		
		super.finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
