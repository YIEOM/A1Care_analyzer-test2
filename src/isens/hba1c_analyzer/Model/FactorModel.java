package isens.hba1c_analyzer.Model;

import android.app.Activity;
import isens.hba1c_analyzer.RunActivity;

public class FactorModel {

	public final static byte CORRELATION_FACTOR = 0,
			  				 ADJUSTMENT_FACTOR  = 1,
			  				 ABSORBANCE_FACTOR  = 2,
			  				 CORRECTION_FACTOR1 = 3,
							 CORRECTION_FACTOR2 = 4;
	
	private Activity activity;
	
	public FactorModel(Activity activity) {
		
		this.activity = activity;
	}
	
	public String getStrFactor(float factor) {
		
		return Float.toString(factor);
	}
	
	public void setFactor(int mode, String key1, float value1, String key2, float value2) {

		switch(mode) {
		
		case CORRELATION_FACTOR	:
			RunActivity.CF_Slope = value1;
			RunActivity.CF_Offset = value2;
			break;
			
		case ADJUSTMENT_FACTOR	:
			RunActivity.AF_Slope = value1;
			RunActivity.AF_Offset = value2;
			break;
			
		case CORRECTION_FACTOR1	:
			RunActivity.RF1_Slope = value1;
			RunActivity.RF1_Offset = value2;
			break;
			
		case CORRECTION_FACTOR2	:
			RunActivity.RF2_Slope = value1;
			RunActivity.RF2_Offset = value2;
			break;
			
		case ABSORBANCE_FACTOR	:
			RunActivity.SF_F1 = value1;
			RunActivity.SF_F2 = value2;
			break;
		}
		
		FileSystem mFileSystem = new FileSystem(activity);
		mFileSystem.setPreferences("User Define", Activity.MODE_PRIVATE);
		mFileSystem.putFloatPref(key1, value1);
		mFileSystem.putFloatPref(key2, value2);
		mFileSystem.commitPref();
	}
}
