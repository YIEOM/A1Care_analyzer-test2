package isens.hba1c_analyzer;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class FactorActivity extends Activity {
	
	final static byte CORRELATION_FACTOR_SETTING = 0,
					  ADJUSTMENT_FACTOR_SETTING = 1,
					  ABSORBANCE_FACTOR_SETTING = 2;
	
	public TimerDisplay mTimerDisplay;
	
	public CustomKeyboard mCustomKeyboard;
	
	private ImageView titleImage,
					  iconImage,
					  fct1stImage,
					  fct2ndImage;
	
	private Button escBtn,
				   backBtn;
	
	private EditText fct1stEText, 
					 fct2ndEText;
	
	private boolean btnState = false;
	
	public int itnData;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting2);
		
		titleImage = (ImageView) findViewById(R.id.title);
		iconImage = (ImageView) findViewById(R.id.icon);
		fct1stImage = (ImageView) findViewById(R.id.fct1st);
		fct2ndImage = (ImageView) findViewById(R.id.fct2nd);
		
		fct1stEText = (EditText) findViewById(R.id.fct1stText);
		fct2ndEText = (EditText) findViewById(R.id.fct2ndText);
		
		if(HomeActivity.ANALYZER_SW == HomeActivity.DEVEL) {
		
			/* */
			
			mCustomKeyboard = new CustomKeyboard(this, R.id.customKeyboard, R.layout.numkeyboard);
			mCustomKeyboard.RegisterEditText(R.id.fct2ndText);
		
			/* */
		}
		
		FactorInit();
	}
	
	public void FactorInit() {
		
		Intent itn = getIntent();
		itnData = itn.getIntExtra("Factor Mode", 0);
		
		switch(itnData) {
			
		case CORRELATION_FACTOR_SETTING :
			DisplayImage(R.drawable.cf_title, R.drawable.cf_icon, R.drawable.af_slope, R.drawable.af_intercept, RunActivity.CF_Slope, RunActivity.CF_Offset);
			DisplayBackButton();
			break;
		
		case ADJUSTMENT_FACTOR_SETTING :
			DisplayImage(R.drawable.af_title, R.drawable.af_icon, R.drawable.af_slope, R.drawable.af_intercept, RunActivity.AF_Slope, RunActivity.AF_Offset);
			DisplayESCButton();
			break;
		
		case ABSORBANCE_FACTOR_SETTING :
			DisplayImage(R.drawable.sf_title, R.drawable.sf_icon, R.drawable.sf_f1, R.drawable.sf_f2, RunActivity.SF_F1, RunActivity.SF_F2);
			DisplayESCButton();
			break;
			
		default :
			break;
		}
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.factorlayout);
	}
	
	public void DisplayImage(int title, int icon, int fct1st, int fct2nd, float fct1stVal, float fct2ndVal) {
		
		titleImage.setBackgroundResource(title);
		iconImage.setBackgroundResource(icon);
		fct1stImage.setBackgroundResource(fct1st);
		fct2ndImage.setBackgroundResource(fct2nd);
		
		fct1stEText.setText(Float.toString(fct1stVal));
		fct2ndEText.setText(Float.toString(fct2ndVal));
	}
	
	public void DisplayBackButton() {
		
		/*System setting Activity activation*/
		backBtn = (Button)findViewById(R.id.backBtn);
		backBtn.setBackgroundResource(R.drawable.back_selector);
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				if(!btnState) {
					
					btnState = true;
				
					backBtn.setEnabled(false);
					
					GetFactor();
					
					WhichIntent(TargetIntent.SystemSetting);
				}
			}
		});
	}

	public void DisplayESCButton() {
		
		/*System setting Activity activation*/
		escBtn = (Button)findViewById(R.id.escBtn);
		escBtn.setBackgroundResource(R.drawable.esc_selector);
		escBtn.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
		
				if(!btnState) {
					
					btnState = true;
				
					escBtn.setEnabled(false);
					
					GetFactor();
					
					WhichIntent(TargetIntent.Maintenance);
				}
			}
		});
	}
	
	public void GetFactor() {
		
		float fct1st,
			  fct2nd;
		
		try {
		
			fct1st = Float.valueOf(fct1stEText.getText().toString()).floatValue();
			fct2nd = Float.valueOf(fct2ndEText.getText().toString()).floatValue();
			
		} catch (NumberFormatException e) {
			
			switch(itnData) {
			
			case CORRELATION_FACTOR_SETTING :
				fct1st = RunActivity.CF_Slope;
				fct2nd = RunActivity.CF_Offset;
				break;
			
			case ADJUSTMENT_FACTOR_SETTING :
				fct1st = RunActivity.AF_Slope;
				fct2nd = RunActivity.AF_Offset;
				break;
			
			case ABSORBANCE_FACTOR_SETTING :
				fct1st = RunActivity.SF_F1;
				fct2nd = RunActivity.SF_F2;
				break;
			
			default : 
				fct1st = 1.0f;
				fct2nd = 0.0f;
				break;
			}
		}
		
		SaveFactor(fct1st, fct2nd);
	}
	
	public void SaveFactor(float fct1st, float fct2nd) { // Saving number of user define parameter
		
		SharedPreferences mPref = getSharedPreferences("User Define", MODE_PRIVATE);
		SharedPreferences.Editor mEdit = mPref.edit();
		
		switch(itnData) {
		
		case CORRELATION_FACTOR_SETTING :
			mEdit.putFloat("CF SlopeVal", fct1st);
			mEdit.putFloat("CF OffsetVal", fct2nd);
			
			RunActivity.CF_Slope = fct1st;
			RunActivity.CF_Offset = fct2nd;
			break;
		
		case ADJUSTMENT_FACTOR_SETTING :
			mEdit.putFloat("AF SlopeVal", fct1st);
			mEdit.putFloat("AF OffsetVal", fct2nd);
			
			RunActivity.AF_Slope = fct1st;
			RunActivity.AF_Offset = fct2nd;
			break;
		
		case ABSORBANCE_FACTOR_SETTING :
			mEdit.putFloat("SF Fct1stVal", fct1st);
			mEdit.putFloat("SF Fct2ndVal", fct2nd);
			
			RunActivity.SF_F1 = fct1st;
			RunActivity.SF_F2 = fct2nd;
			break;
		
		default : 
			break;
		}
		
		mEdit.commit();		
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Maintenance	:
			nextIntent = new Intent(getApplicationContext(), MaintenanceActivity.class);
			break;

		case SystemSetting	:
			nextIntent = new Intent(getApplicationContext(), SystemSettingActivity.class);
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