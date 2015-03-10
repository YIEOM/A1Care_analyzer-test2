package isens.hba1c_analyzer;

import java.text.DecimalFormat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class Barcode {

	final static double a1ref  = 0.01, 
						b1ref  = -0.07, 
						a21ref = 0.05, 
						b21ref = 0.03, 
						a22ref = 0.035, 
						b22ref = 0.04;
	
	private float refScale[] = {1f, 1f, 1f, 1f, 0.6f, 1.4f, 1f};
	
	public static String RefNum;
	public static double a1, 
						 b1,
						 a21, 
						 b21, 
						 a22, 
						 b22, 
						 a23, 
						 b23, 
						 L,
						 M,
						 H;
	
	public static double Sm, Im, Ss, Is, Asm, Aim, Ass, Ais;
	
	public void BarcodeCheck(StringBuffer buffer) { // Check a barcode data
		
		int len, 
			test, 
			year, 
			month, 
			day, 
			line, 
			locate, 
			check, 
			sum; 		
		
		float scale;
		
		len = buffer.length();
		
		if(len == 18) { // Check length of barcode data

			try {
				
				/* Classification for each digit barcode */
				test   = (int) buffer.charAt(0) - 64;
				scale = refScale[test - 1];
				year   = (int) buffer.charAt(1) - 64;
				if(year > 26) year -= 6;
				month  = (int) buffer.charAt(2) - 64;
				day    = (int) buffer.charAt(3) - 64;
				if(day > 26) day -= 6;
				line   = (int) buffer.charAt(4) - 64;
				locate = (int) buffer.charAt(5) - 42;
				check  = (int) buffer.charAt(15) - 48;
				
				RefNum = buffer.substring(0, 5);
				
				Sm = 0.0237 * (((int) buffer.charAt(6) - 42) - 1) + 0.1;
				Im = 0.158 * (((int) buffer.charAt(7) - 42) - 1) - 6;
				Ss = 0.0003 * (((int) buffer.charAt(8) - 42) - 1);
				Is = 0.002 * (((int) buffer.charAt(9) - 42) - 1);
				
				Asm = 0.000316 * (((int) buffer.charAt(10) - 42) - 1) - 0.01;
				Aim = 0.00237 * (((int) buffer.charAt(11) - 42) - 1) - 0.1;
				Ass = 0.000004 * (((int) buffer.charAt(12) - 42) - 1);
				Ais = 0.00003 * (((int) buffer.charAt(13) - 42) - 1);
				
				Log.w("Barcode", "sm : " + Sm + " im : " + Im + " ss : " + Ss + " is : " + Is);
				Log.w("Barcode", "asm idx : " + ((int) buffer.charAt(10) - 42) + " aim idx : " + ((int) buffer.charAt(11) - 42) + " ass idx : " + ((int) buffer.charAt(12) - 42) + " ais idx : " + ((int) buffer.charAt(13) - 42));
				
				a1 = 0.009793532;
				b1 = -0.028;
				a21 = 0.060055;
				b21 = -0.003032;
				a22 = 0.05014;
				b22 = -0.004829;
				a23 = 0.039032;
				b23 = -0.005064;
				L   = 5.1;
				M 	= 7.1;
				H   = 11.7;
			
				sum = (test + year + month + day + line + locate) % 10; // Checksum bit
				
//				Log.w("Barcode", "scale : " + scale + " test : " + test + " year : " + year + " month : " + month + " day : " + day + " line : " + line + " locate : " + locate + " check : " + check);
//				Log.w("Barcode", "a1ref : " + scale * a1ref + " b1ref : " + scale * b1ref + " a21ref : " + scale * a21ref + " b21ref : " + scale * b21ref + " a22ref : " + scale * a22ref + " b22ref : " + scale * b22ref);
//				Log.w("Barcode", "a1 : " + a1 + " b1 : " + b1 + " a21 : " + a21 + " b21 : " + b21 + " a22 : " + a22 + " b22 : " + b22 + " L : " + L + " H : " + H);
				Log.w("Barcode", "asm : " + Asm + " aim : " + Aim + " ass : " + Ass + " ais : " + Ais);
				
				if( sum == check ) { // Whether or not the correct barcode code
	
//					Log.w("Barcode", "Correct Data : " + buffer);
					BarcodeStop(true);
						
				} else {
					
					BarcodeStop(false);	
				}
			
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
			}
		} else {
			
			BarcodeStop(false);
		}
	}
	
	public void BarcodeStop(boolean state) { // Turn off barcode module
		
		Log.w("BarcodeStop", "state : " + state);
		
		if(state) {
			
			ActionActivity.IsCorrectBarcode = true;
			ActionActivity.BarcodeCheckFlag = true;
			
		} else {
//			Log.w("BarcodeStop", "BarcodeCheck : " + ActionActivity.BarcodeCheckFlag);
			ActionActivity.IsCorrectBarcode = false;
			ActionActivity.BarcodeCheckFlag = true;
		}
	}
}
