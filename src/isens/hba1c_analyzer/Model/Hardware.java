package isens.hba1c_analyzer.Model;

import java.text.DecimalFormat;

import android.util.Log;

import isens.hba1c_analyzer.Barcode;
import isens.hba1c_analyzer.HomeActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.RunActivity;
import isens.hba1c_analyzer.SerialPort;
import isens.hba1c_analyzer.RunActivity.AnalyzerState;
import isens.hba1c_analyzer.RunActivity.CartDump;
import isens.hba1c_analyzer.SerialPort.CtrTarget;

public class Hardware {

	public final static String HOME_POSITION    = "CH",
							   MEASURE_POSITION = "CM",
							   Step1st_POSITION = "C1",
							   Step2nd_POSITION = "C2",
							   CARTRIDGE_DUMP   = "CD",
							   FILTER_DARK      = "FD",
							   FILTER_SKIP		= "FS",
							   MOTOR_COMPLETE   = "AR",
							   PHOTO_MEASURE	= "VH",
							   MOTOR_STOP		= "MS",
							   CARTRIDGE_ERROR	= "CE1",
							   FILTER_ERROR 	= "FE1",
							   ERROR_DOOR       = "ED",
							   NO_RESPONSE		= "NR";
	
	final static byte FIRST_SHAKING_TIME = 105, // Motor shaking time, default : 6 * 105(sec) = 0630
					  SECOND_SHAKING_TIME = 90; // Motor shaking time, default : 6 * 90(sec) = 0540
	
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
	
	private SerialPort mSerialPort;
	
	private double photoADC;
	
	public Hardware() {
		
		mSerialPort = new SerialPort();
	}
	
	public void runHomePosition() {
		
		mSerialPort.BoardTx(HOME_POSITION, CtrTarget.NormalSet);
	}
	
	public void runMeasurePosition() {
		
		mSerialPort.BoardTx(MEASURE_POSITION, CtrTarget.NormalSet);
	}
	
	public void runStep1stPosition() {
		
		mSerialPort.BoardTx(Step1st_POSITION, CtrTarget.NormalSet);
	}
	
	public void runStep2ndPosition() {
		
		mSerialPort.BoardTx(Step2nd_POSITION, CtrTarget.NormalSet);
	}
	
	public void runCartridgeDump() {
		
		mSerialPort.BoardTx(CARTRIDGE_DUMP, CtrTarget.NormalSet);
	}
	
	public void runFilterDark() {
		
		mSerialPort.BoardTx(FILTER_DARK, CtrTarget.NormalSet);
	}
	
	public void runFilterSkip() {
		
		mSerialPort.BoardTx(FILTER_SKIP, CtrTarget.NormalSet);
	}
	
	public void runMotorShaking(byte sec) {
		
		DecimalFormat ShkDf = new DecimalFormat("0000");
		
		mSerialPort.BoardTx(ShkDf.format(sec * 6), CtrTarget.MotorSet);  // Motor shaking time, default : 6.5 * 10(sec) = 0065
	}
	
	public void runPhotoMeasure() {
		
		mSerialPort.BoardTx("VH", SerialPort.CtrTarget.NormalSet);
	}
	
	public String getBoardMessage(int rspTime) {
		
		String message;
		byte time = 0;
		
		rspTime = rspTime * 10; // second unit
		
		do {
			
			message = mSerialPort.BoardMessageOutput();
			
			if(time++ > rspTime) {
				break;
			}
							
			if(RunActivity.isError) break;
			
			SerialPort.Sleep(100);
			
		} while(!message.equals(NO_RESPONSE));
		
		return message;
	}
	
	public AnalyzerState getBoardState(String crtRsp, AnalyzerState nextState, byte time) {
		
		AnalyzerState state;
		String message;
		
		message = getBoardMessage(time);
		
		if(message.equals(crtRsp)) state = nextState;
		else state = checkBoardError(message);
				
		return state;
	}
	
	public AnalyzerState getPhotoState(AnalyzerState nextState, byte time) {
		
		AnalyzerState state;
		String message;
		
		message = getBoardMessage(time);
		
		if(message.length() == 8) {
			
			try {
				
				photoADC = Double.parseDouble(message);	
				state = nextState;
				
			} catch(NumberFormatException e) {
				
				state = AnalyzerState.NoResponse;
			}			
			
		} else state = checkBoardError(message);
				
		return state;
	}
	
	public AnalyzerState checkBoardError(String message) {
		
		AnalyzerState state = AnalyzerState.NormalOperation;
		
		if(!message.equals(NO_RESPONSE)) {
			
			if(message.equals(CARTRIDGE_ERROR)) state= AnalyzerState.ShakingMotorError;
			
			else if(message.equals(FILTER_ERROR)) state = AnalyzerState.FilterMotorError;
			
		} else if(RunActivity.isError) state = AnalyzerState.ErrorCover;
		
		else state = AnalyzerState.NoResponse;
		
		return state;
	}
	
	public double getPhotoADC() {
		
		return photoADC - BlankValue[0];
	}
}
