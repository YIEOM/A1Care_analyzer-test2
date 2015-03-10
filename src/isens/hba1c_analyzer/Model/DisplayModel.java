package isens.hba1c_analyzer.Model;

import android.app.Activity;
import android.util.Log;
import android.view.WindowManager;
import isens.hba1c_analyzer.R;

public class DisplayModel {
		
	private Activity activity;
	
	public DisplayModel(Activity activity) {
		
		this.activity = activity;
	}
	
	public int getBarGauageImage(int Value) {
		
		int barGaugeImage;
		
		switch(Value) {
		
		case 51		:
			barGaugeImage = R.drawable.display_bar_gauge_green_5;
			break;

		case 102	:
			barGaugeImage = R.drawable.display_bar_gauge_green_4;
			break;
			
		case 153	:
			barGaugeImage = R.drawable.display_bar_gauge_green_3;
			break;
			
		case 204	:
			barGaugeImage = R.drawable.display_bar_gauge_green_2;
			break;
		
		case 255	:
			barGaugeImage = R.drawable.display_bar_gauge_green_1;
			break;
			
		default		:
			barGaugeImage = R.drawable.display_bar_gauge_green_3;
			break;
		}
		
		return barGaugeImage;
	}
	
	public int downBrightnessValue(int value) {
		
		if(value != 255) value += 51;
		
		return value;
	}
	
	public int upBrightnessValue(int value) {
		
		if(value != 51) value -= 51;
		
		return value;
	}
	
	public int getBrightnessValue() {
		
		int brightnessValue;
		
		try {
			
			brightnessValue = android.provider.Settings.System.getInt(activity.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
			Log.w("GetBrightness", "Brightness : " + brightnessValue);
			
		} catch(Exception e) {
			
			brightnessValue = 153;
		}
		
		return brightnessValue;
	}

	public void setBrightnessValue(int brightnessValue) {
	
		try {
			
			Log.w("SetBrightness", "Brightness : " + brightnessValue);
			
			WindowManager.LayoutParams params = activity.getWindow().getAttributes();
			params.screenBrightness = (float)brightnessValue/255;
			activity.getWindow().setAttributes(params);
			
			android.provider.Settings.System.putInt(activity.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
			
		} catch(Exception e) {
			
		}
	}
}
