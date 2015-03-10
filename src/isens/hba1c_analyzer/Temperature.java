package isens.hba1c_analyzer;

import isens.hba1c_analyzer.SerialPort.CtrTarget;

import java.text.DecimalFormat;
import java.util.Timer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class Temperature extends SerialPort {

	TemperatureActivity mTemperatureActivity;

	final static String TEMPERATURE_CELLBLOCK = "VT",
						TEMPERATURE_AMBIENT   = "IA";
	
	public TextView TmpText;
	
	public static float InitTmp;

	static final int MaxAmbTmp = 39,
					 MinAmbTmp = 20;
	
	public void TmpInit() {
		
		TmpInit mTmpInit = new TmpInit();
		mTmpInit.start();
	}
	
	public class TmpInit extends Thread {
		
		public void run() {
			
			double tmpDouble;
			String tmpString;
			DecimalFormat tmpFormat;
			
			while(TimerDisplay.RXBoardFlag) Sleep(10);
			
			TimerDisplay.RXBoardFlag = true;
			
			tmpDouble = (double) InitTmp * (double) 1670.17 + (double) 25891.34;
			tmpFormat = new DecimalFormat("#####0");
			
			if(tmpFormat.format(tmpDouble).length() == 5) tmpString = "0" + tmpFormat.format(tmpDouble);
			else tmpString = tmpFormat.format(tmpDouble);
			
			BoardTx("R" + tmpString, CtrTarget.NormalSet);
			while(!BoardMessageOutput().equals(tmpFormat.format(tmpDouble))) Sleep(100);
			
			TimerDisplay.RXBoardFlag = false;
		}
	}
	
	public double CellTmpRead() { // Read current temperature of cell block
		
		CellTmpRead mCellTmpRead = new CellTmpRead();
		mCellTmpRead.start();
		
		try {
			mCellTmpRead.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mCellTmpRead.getCellTmp();
	}
	
	public class CellTmpRead extends Thread {
		
		double cellTmp;
		
		public void run() {
			
			double tmpRaw;
			String temp;
			
			while(TimerDisplay.RXBoardFlag) Sleep(10);
			
			TimerDisplay.RXBoardFlag = true;
			
			BoardTx(TEMPERATURE_CELLBLOCK, CtrTarget.NormalSet);
			
			do {	
			
				temp = BoardMessageOutput();
				Sleep(10);
//				Log.w("CellTmpRead", temp);
			
			} while(temp.equals("NR"));
			
			TimerDisplay.RXBoardFlag = false;
			
			try {
				
				tmpRaw = Double.parseDouble(temp);
					
			} catch(NumberFormatException e) {
				
				tmpRaw = 0.0;
			}
			
			cellTmp = (tmpRaw / (double) 1670.17) - (double) 15.5;
			
	//		Log.w("CellTmpRead", "tmpDouble : " + tmpDouble);
		}
		
		public double getCellTmp() {
			
			return cellTmp;
		}
	}

	public double AmbTmpRead() { // Read current temperature of cell block
		
		AmbTmpRead mAmbTmpRead = new AmbTmpRead();
		mAmbTmpRead.start();
		
		try {
			mAmbTmpRead.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mAmbTmpRead.getAmbTmp();
	}
	
	public class AmbTmpRead extends Thread {
	
		double ambTmp;
		
		public void run() {
			
			int tmpADC;
			double tmpV;
			String tmpData;
			
			while(TimerDisplay.RXBoardFlag) Sleep(10);
			
			TimerDisplay.RXBoardFlag = true;
			
			BoardTx(TEMPERATURE_AMBIENT, CtrTarget.NormalSet);
					
			do {
				
				tmpData = SensorMessageOutput();
				Sleep(10);
//				Log.w("AmbTmpRead", tmpData);
				
			} while(!tmpData.substring(1, 2).equals("T"));
			
			TimerDisplay.RXBoardFlag = false;
			
			try {
				
				tmpADC = Integer.parseInt(tmpData.substring(2));
				
			} catch(NumberFormatException e) {
				
				tmpADC = 0;
			}
			
			tmpV = ((double) 5/1024 * (tmpADC + 1));
			ambTmp = (double) tmpV / 0.01;
		}
		
		public double getAmbTmp() {
			
			return ambTmp;
		}
	}
}
