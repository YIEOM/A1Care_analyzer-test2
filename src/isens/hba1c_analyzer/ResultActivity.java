package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.Model.ConvertModel;
import isens.hba1c_analyzer.View.ConvertActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {
		
	final static byte ACTION_ACTIVITY  = 1,
					  HOME_ACTIVITY    = 2,
					  COVER_ACTION_ESC = 3,
					  SCAN_ACTIVITY    = 4;
	
	public Temperature mTemperature;
	public SerialPort mSerialPort;
	public ErrorPopup mErrorPopup;
	public TimerDisplay mTimerDisplay;
	public DatabaseHander mDatabaseHander;
	public RunActivity mRunActivity;
		
	public static EditText PatientIDText;
	
	private TextView HbA1cText,
					 HbA1cUnitText,
					 DateText,
					 AMPMText,
					 Ref,
					 PrimaryText,
					 RangeText,
					 UnitText;
	
//	public RelativeLayout resultLinear;
	
	private Button homeIcon,
				   printBtn,
				   nextSampleBtn,
				   convertBtn;
	
	private String getTime[] = new String[6];
	
	public static int ItnData;
	public int dataCnt;
	public double cellBlockEndTmp;
	public String hbA1cCurr,
				  unitCurr,
				  rangeCurr,
				  primaryCurr;
	public byte primaryByte;
	
	public String operator;
	
	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.result);
		
		PatientIDText = (EditText) findViewById(R.id.patientidtext);
		
		ResultInit();
		
		/*Home Activity activation*/
		homeIcon = (Button)findViewById(R.id.homeicon);
		homeIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					homeIcon.setEnabled(false);
					
					WhichIntent(TargetIntent.Home);
				}
			}
		});
		
		printBtn = (Button)findViewById(R.id.printbtn);
		printBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					PrintResultData();
				}
			}
		});
		
		/*Test Activity activation*/
		nextSampleBtn = (Button)findViewById(R.id.nextsamplebtn);
		nextSampleBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {

				if(!btnState) {
					
					btnState = true;
					
					nextSampleBtn.setEnabled(false);
					
					if(HomeActivity.ANALYZER_SW != HomeActivity.TEST) WhichIntent(TargetIntent.Run);
					else WhichIntent(TargetIntent.ScanTemp);
				}
			}
		});
		
		convertBtn = (Button)findViewById(R.id.convertbtn);
		convertBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {

				if(!btnState) {
					
					btnState = true;
					
					SerialPort.Sleep(200);
					
					PrimaryConvert();
				}
			}
		});		
	}
	
	public void ResultInit() {

		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.resultlayout);
		
		GetCurrTime();
		GetDataCnt();
		
//		mTemperature = new Temperature(R.id.resultlayout);
//		cellBlockEndTmp = mTemperature.CellTmpRead();
		
		HbA1cText = (TextView) findViewById(R.id.hba1cPct);
		HbA1cUnitText = (TextView) findViewById(R.id.hba1cUnit);
		DateText = (TextView) findViewById(R.id.r_testdate1);
		AMPMText = (TextView) findViewById(R.id.r_testdate2);
		Ref = (TextView) findViewById(R.id.ref);
		PrimaryText = (TextView) findViewById(R.id.primary);
		RangeText = (TextView) findViewById(R.id.r_range);
		UnitText = (TextView) findViewById(R.id.unit);
		
		Intent itn = getIntent();
		ItnData = itn.getIntExtra("RunState", 0);
		
		if(ItnData == RunActivity.NORMAL_OPERATION || ItnData == RunActivity.DEMO_OPERATION) {
			
//			if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) {
//				
//				RunActivity.HbA1cValue = 5.2;
//			
//			} else 
				
			if(HomeActivity.ANALYZER_SW == HomeActivity.DEMO) {
				
				int rem;
				
				rem = (int) ((Math.random() * 10) + 1) % 3;
				
				switch(rem) {
				
				case 0	:
					RunActivity.HbA1cValue = 5.5;
					break;
					
				case 1	:
					RunActivity.HbA1cValue = 6.7;
					break;
				
				case 2	:
					RunActivity.HbA1cValue = 8.3;
					break;
					
				default	:
					break;
				}
				
				ConvertModel.Primary = ConvertModel.NGSP;
			}
			
			primaryByte = ConvertModel.Primary;
			
			mRunActivity = new RunActivity();
			UnitConvert(mRunActivity.ConvertHbA1c(ConvertModel.Primary), ConvertModel.Primary);
			HbA1cDisplay();
		
		} else if(ItnData == R.string.stop) { 
			
			HbA1cText.setText(ItnData);
			
		} else {
			
			HbA1cText.setText(ItnData);
			mErrorPopup = new ErrorPopup(this, this, R.id.resultlayout);
			mErrorPopup.ErrorBtnDisplay(ItnData);
		}
		
		DateText.setText(TimerDisplay.rTime[0] + "." + TimerDisplay.rTime[1] + "." + TimerDisplay.rTime[2] + " " + TimerDisplay.rTime[4] + ":" + TimerDisplay.rTime[5]);
		AMPMText.setText(TimerDisplay.rTime[3]);
		Ref.setText(Barcode.RefNum);
		
		mDatabaseHander = new DatabaseHander(this);
		operator = mDatabaseHander.GetLastLoginID();
		
		if(operator == null) operator = "Guest";
	}
	
	public void PatientIDDisplay(final StringBuffer str) {
		
		new Thread(new Runnable() {
		    public void run() {    
		        runOnUiThread(new Runnable(){
		            public void run() {
		            	
		            	PatientIDText.setText(str.substring(0, str.length() - 1));
		            }
		        });
		    }
		}).start();
	}
	
	public void GetCurrTime() { // getting the current date and time
		
		getTime[0] = TimerDisplay.rTime[0];
		getTime[1] = TimerDisplay.rTime[1];
		getTime[2] = TimerDisplay.rTime[2];
		getTime[3] = TimerDisplay.rTime[3];		
		if(TimerDisplay.rTime[4].length() != 2) getTime[4] = "0" + TimerDisplay.rTime[4];
		else getTime[4] = TimerDisplay.rTime[4];
		getTime[5] = TimerDisplay.rTime[5];			
	}
	
	public void GetDataCnt() {
		
		if(Barcode.RefNum.substring(0, 1).equals("B")) dataCnt = RemoveActivity.ControlDataCnt;
		else dataCnt = RemoveActivity.PatientDataCnt;
	}
	
	public void PrintResultData() {
		
		if(ItnData == RunActivity.NORMAL_OPERATION) {
			
			StringBuffer txData = new StringBuffer();
			DecimalFormat dfm = new DecimalFormat("0000"),
						  pIDLenDfm = new DecimalFormat("00");
			
			int tempDataCnt;
			
			tempDataCnt = dataCnt % 9999;
			if(tempDataCnt == 0) tempDataCnt = 9999; 
			
			txData.delete(0, txData.capacity());
			
			txData.append(getTime[0]);
			txData.append(getTime[1]);
			txData.append(getTime[2]);
			txData.append(getTime[3]);
			txData.append(getTime[4]);
			txData.append(getTime[5]);
			txData.append(dfm.format(tempDataCnt));
			txData.append(Barcode.RefNum);
			txData.append(pIDLenDfm.format(PatientIDText.getText().toString().length()));
			txData.append(PatientIDText.getText().toString());
			txData.append(pIDLenDfm.format(operator.length()));
			txData.append(operator);
			txData.append(Integer.toString((int) primaryByte)); // primary
//			Log.w("PrintResultData", "primary : " + Integer.toString((int) primaryByte));
			txData.append(hbA1cCurr);
			
			mSerialPort = new SerialPort();
			mSerialPort.PrinterTxStart(SerialPort.PRINTRESULT, txData);
			
			SerialPort.Sleep(100);
		
		} else if(ItnData == RunActivity.DEMO_OPERATION) {
			
			StringBuffer txData = new StringBuffer();
			
			txData.delete(0, txData.capacity());
			
			txData.append("2015");
			txData.append("03");
			txData.append("05");
			txData.append("AM");
			txData.append("09");
			txData.append("30");
			txData.append("0003");
			txData.append("DBANA");
			txData.append("07");
			txData.append("Patient");
			txData.append("08");
			txData.append("Operator");
			txData.append("0"); // primary
//			Log.w("PrintResultData", "primary : " + Integer.toString((int) primaryByte));
			txData.append("8.3");
			
			mSerialPort = new SerialPort();
			mSerialPort.PrinterTxStart(SerialPort.PRINTRESULT, txData);
			
			SerialPort.Sleep(100);
		}

		btnState = false;	
	}
	
	public void UnitConvert(double hbA1cValue, byte primary) {
		
		DecimalFormat hbA1cFormat = new DecimalFormat("0.0");
		
		hbA1cCurr = hbA1cFormat.format(hbA1cValue);
		
		if(primary == ConvertModel.NGSP) {
			
			unitCurr = "%";
			rangeCurr = "4.0 - 6.0";
			primaryCurr = "NGSP";
			
			HbA1cUnitText.setTextSize(85);
			
		} else {
			
			unitCurr = "mmol/mol";
			rangeCurr = "20 - 42";
			primaryCurr = "IFCC";
			
			HbA1cUnitText.setTextSize(24);
		}
	}
	
	public void HbA1cDisplay() {
		
		HbA1cText.setText(hbA1cCurr);
		HbA1cUnitText.setText(unitCurr);
		PrimaryText.setText(primaryCurr);
		RangeText.setText(rangeCurr);
		UnitText.setText(unitCurr);
		
		btnState = false;
	}
	
	public void PrimaryConvert() {
		
		if(ItnData == RunActivity.NORMAL_OPERATION || ItnData == RunActivity.DEMO_OPERATION) {
			
			double hbA1cValue;
			
			if(primaryByte == ConvertModel.NGSP) { // to IFCC
					
				primaryByte	= ConvertModel.IFCC;
				hbA1cValue = mRunActivity.ConvertHbA1c(ConvertModel.IFCC);
				UnitConvert(hbA1cValue, primaryByte);
			
			} else {
				
				primaryByte	= ConvertModel.NGSP;
				hbA1cValue = mRunActivity.ConvertHbA1c(ConvertModel.NGSP); 
				UnitConvert(hbA1cValue, primaryByte);
			}
		
			HbA1cDisplay();
		
		} else btnState = false;
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion after intent data deliver
		
		Intent nextIntent = new Intent(getApplicationContext(), FileSaveActivity.class);
		DecimalFormat photoDfm = new DecimalFormat("0.0"),
					  absorbDfm = new DecimalFormat("0.0000"),
					  pIDLenDfm = new DecimalFormat("00");
		
		String pID;
		int pIDLen;
		
		if(ItnData == RunActivity.NORMAL_OPERATION) {
			
			UnitConvert(mRunActivity.ConvertHbA1c(ConvertModel.Primary), ConvertModel.Primary);
			primaryByte = ConvertModel.Primary;
		}
		else primaryByte = 2;
		
		nextIntent.putExtra("RunState", ItnData);
		nextIntent.putExtra("Year", getTime[0]);
		nextIntent.putExtra("Month", getTime[1]);
		nextIntent.putExtra("Day", getTime[2]);
		nextIntent.putExtra("AmPm", getTime[3]);
		nextIntent.putExtra("Hour", getTime[4]);
		nextIntent.putExtra("Minute", getTime[5]);
		nextIntent.putExtra("DataCnt", dataCnt);
		nextIntent.putExtra("RefNumber", Barcode.RefNum);
		nextIntent.putExtra("PatientIDLen", pIDLenDfm.format(PatientIDText.getText().toString().length()));
		nextIntent.putExtra("PatientID", PatientIDText.getText().toString());
		nextIntent.putExtra("OperatorLen", pIDLenDfm.format(operator.length()));
		nextIntent.putExtra("Operator", operator);
		nextIntent.putExtra("Primary", Integer.toString((int) primaryByte)); // primary
		nextIntent.putExtra("Hba1cPct", hbA1cCurr);
		
//		nextIntent.putExtra("RunMin", (int) RunActivity.runMin);
//		nextIntent.putExtra("RunSec", (int) RunActivity.runSec);
		nextIntent.putExtra("BlankVal0", photoDfm.format(RunActivity.BlankValue[0]));
		nextIntent.putExtra("BlankVal1", photoDfm.format(RunActivity.BlankValue[1]));
		nextIntent.putExtra("BlankVal2", photoDfm.format(RunActivity.BlankValue[2]));
		nextIntent.putExtra("BlankVal3", photoDfm.format(RunActivity.BlankValue[3]));
		nextIntent.putExtra("St1Abs1by0", absorbDfm.format(RunActivity.Step1stAbsorb1[0]));
		nextIntent.putExtra("St1Abs1by1", absorbDfm.format(RunActivity.Step1stAbsorb1[1]));
		nextIntent.putExtra("St1Abs1by2", absorbDfm.format(RunActivity.Step1stAbsorb1[2]));
		nextIntent.putExtra("St1Abs2by0", absorbDfm.format(RunActivity.Step1stAbsorb2[0]));
		nextIntent.putExtra("St1Abs2by1", absorbDfm.format(RunActivity.Step1stAbsorb2[1]));
		nextIntent.putExtra("St1Abs2by2", absorbDfm.format(RunActivity.Step1stAbsorb2[2]));
		nextIntent.putExtra("St1Abs3by0", absorbDfm.format(RunActivity.Step1stAbsorb3[0]));
		nextIntent.putExtra("St1Abs3by1", absorbDfm.format(RunActivity.Step1stAbsorb3[1]));
		nextIntent.putExtra("St1Abs3by2", absorbDfm.format(RunActivity.Step1stAbsorb3[2]));
		nextIntent.putExtra("St2Abs1by0", absorbDfm.format(RunActivity.Step2ndAbsorb1[0]));
		nextIntent.putExtra("St2Abs1by1", absorbDfm.format(RunActivity.Step2ndAbsorb1[1]));
		nextIntent.putExtra("St2Abs1by2", absorbDfm.format(RunActivity.Step2ndAbsorb1[2]));
		nextIntent.putExtra("St2Abs2by0", absorbDfm.format(RunActivity.Step2ndAbsorb2[0]));
		nextIntent.putExtra("St2Abs2by1", absorbDfm.format(RunActivity.Step2ndAbsorb2[1]));
		nextIntent.putExtra("St2Abs2by2", absorbDfm.format(RunActivity.Step2ndAbsorb2[2]));
		nextIntent.putExtra("St2Abs3by0", absorbDfm.format(RunActivity.Step2ndAbsorb3[0]));
		nextIntent.putExtra("St2Abs3by1", absorbDfm.format(RunActivity.Step2ndAbsorb3[1]));
		nextIntent.putExtra("St2Abs3by2", absorbDfm.format(RunActivity.Step2ndAbsorb3[2]));
		
		switch(Itn) {
		
		case Home		:							
			nextIntent.putExtra("WhichIntent", (int) HOME_ACTIVITY);
			break;

		case Run	:			
			nextIntent.putExtra("WhichIntent", (int) ACTION_ACTIVITY);
			break;

		case ScanTemp	:
			nextIntent.putExtra("WhichIntent", (int) SCAN_ACTIVITY);
			break;

			
		default			:	
			break;			
		}	
		
		startActivity(nextIntent);
		finish();
	}
	
	public void finish() {
		
		super.finish();
	}
}
