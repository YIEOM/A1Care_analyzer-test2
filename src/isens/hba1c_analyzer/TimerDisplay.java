package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimerDisplay {
	
	final static byte TIMER_PERIOD = 1000/20, // 1000/Hz
					  PERIOD_1sec  = 1000/TIMER_PERIOD, // 1 second
					  PERIOD_250ms = 250 /TIMER_PERIOD; // 250 milisecond
	
	final static byte FILE_CLOSE 	= 0,
			  		  FILE_OPEN 	= 1,
			  		  FILE_NOT_OPEN = 2;
	
	public Timer timer;

	public GpioPort mGpioPort;
	public SerialPort mSerialPort;

	public Handler handler = new Handler();
	public static TimerTask FiftymsPeriod;

	public static byte ExternalDeviceBarcode = FILE_CLOSE;
	
	public static String rTime[] = new String[8];
	
	public TextView timeText;
	public ImageView deviceImage;
	
	public static Activity activity;
	public static int layoutid;
	
	public static boolean RXBoardFlag = false;
	
	public void TimerInit() {
		
		mSerialPort = new SerialPort();
		mGpioPort = new GpioPort();
		
//		Log.w("TimerInit", "run");
		
		FiftymsPeriod = new TimerTask() {
			
			int cnt = 0;
			
			public void run() {
				Runnable updater = new Runnable() {
					public void run() {
						
						if(cnt++ == 1000) cnt = 0;
						
						if(RXBoardFlag) mSerialPort.BoardRxData();
						
						if((cnt % PERIOD_1sec) == 0) { // One second period
						
							RealTimeSec();
							
							ExternalDeviceCheck();
							
							mGpioPort.CartridgeSensorScan();
							
//							Log.w("TimerInit", "sec" + Integer.parseInt(rTime[6]));
							
							if(Integer.parseInt(rTime[6]) == 0) { // Whenever 00 second
											
								CurrTimeDisplay();
							}
							
						} else if((cnt % PERIOD_250ms) == 0) {
							
							mGpioPort.CartridgeSensorScan();
						}
					}
				};
				
				handler.post(updater);		
			}
		};
		
		timer = new Timer();
		timer.schedule(FiftymsPeriod, 0, TIMER_PERIOD); // Timer period : 50ms
	}
	
	public void ExternalDeviceCheck() {
		
		try {
			
		    boolean isConnect = false;
		    
			Process shell = Runtime.getRuntime().exec("/system/bin/busybox lsusb");

			BufferedReader br = new BufferedReader(new InputStreamReader(shell.getInputStream()));
			String line = "";
			
			while((line = br.readLine()) != null) {
			
				if(line.substring(23).equals("0483:5740")) {
					
					isConnect = true;
					
					if(ExternalDeviceBarcode != FILE_OPEN) {
					
						Log.w("shell", "line : " + line);
						
						mSerialPort.HHBarcodeSerialInit();
						mSerialPort.HHBarcodeRxStart();
						
						ExternalDeviceDisplay(); 
					}
				}
			}
			
			br.close(); 

			if(isConnect == false) {
			
				if(ExternalDeviceBarcode == FILE_OPEN) {
					
					SerialPort.Sleep(500);
					
					ExternalDeviceBarcode = FILE_CLOSE;
					
					ExternalDeviceDisplay();
				}
			}
			
		} catch (IOException e) {
	
			throw new RuntimeException(e);
		}
	}
	
	public void RealTimeSec() {
		
		Calendar c = Calendar.getInstance();
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		rTime[6] = dfm.format(c.get(Calendar.SECOND));
	}
	
	public void RealTime() { // Get current date and time
	
		Calendar c = Calendar.getInstance();
		
		DecimalFormat dfm = new DecimalFormat("00");
		
		rTime[0] = Integer.toString(c.get(Calendar.YEAR));
		rTime[1] = dfm.format(c.get(Calendar.MONTH) + 1);
		rTime[2] = dfm.format(c.get(Calendar.DAY_OF_MONTH));
		
		if(c.get(Calendar.AM_PM) == 0) {

			rTime[3] = "AM";			
		} else {

			rTime[3] = "PM";			
		}
		
		if(c.get(Calendar.HOUR) != 0) {
			
			rTime[4] = Integer.toString(c.get(Calendar.HOUR));
		} else {
		
			rTime[4] = "12";
		}
		rTime[5] = dfm.format(c.get(Calendar.MINUTE));		
		rTime[6] = dfm.format(c.get(Calendar.SECOND));
		rTime[7] = dfm.format(c.get(Calendar.HOUR_OF_DAY));		
	}
	
	public void ActivityParm(Activity act, int id) {
		
		activity = act;
		layoutid = id;
		
		CurrTimeDisplay();
		ExternalDeviceDisplay();
	}
	
	public void CurrTimeDisplay() {
		
//		Log.w("CurrTimeDisplay", "systemcheck : " + R.id.systemchecklayout + " layout : " + layoutid);
//		
		if(layoutid != R.id.systemchecklayout) {
			
//			Log.w("CurrTimeDisplay", "run");
		
			RealTime();
			
			timeText = (TextView) activity.findViewById(R.id.timeText);
			
			timeText.setText(rTime[3] + " " + rTime[4] + ":" + rTime[5]);
		}
	}
	
	public void ExternalDeviceDisplay() {
		
		if(layoutid != R.id.systemchecklayout) {
			Log.w("ExternalDeviceDisplay", "run");
			deviceImage = (ImageView) activity.findViewById(R.id.device);
			
			if(ExternalDeviceBarcode == FILE_OPEN) deviceImage.setBackgroundResource(R.drawable.main_usb_c);
			else deviceImage.setBackgroundResource(R.drawable.main_usb);
		}
	}
}
