package isens.hba1c_analyzer.View;

import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R.anim;
import isens.hba1c_analyzer.R.id;
import isens.hba1c_analyzer.R.layout;
import isens.hba1c_analyzer.Presenter.DisplayPresenter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayActivity extends Activity implements DisplayIView {
	
	private DisplayPresenter mDisplayPresenter;
	
	private ImageView titleImage, iconImage, barGaugeImage;
	
	private Button backBtn, minusBtn, plusBtn;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.setting3);
		
		mDisplayPresenter = new DisplayPresenter(this, this, this, R.layout.setting3);
		mDisplayPresenter.init();
	}
	
	public void setImageId() {
		
		titleImage = (ImageView) findViewById(R.id.title);
		iconImage = (ImageView) findViewById(R.id.icon);
		barGaugeImage = (ImageView) findViewById(R.id.bargauge);
	}
	
	public void setImage() {
		
		titleImage.setBackgroundResource(R.drawable.display_title);
		iconImage.setBackgroundResource(R.drawable.display);
	}
	
	public void setButtonId() {
		
		backBtn = (Button)findViewById(R.id.backBtn);
		minusBtn = (Button)findViewById(R.id.minusBtn);
		plusBtn = (Button)findViewById(R.id.plusBtn);
	}
	
	public void setButtonClick() {
		
		backBtn.setOnTouchListener(mTouchListener);
		minusBtn.setOnTouchListener(mTouchListener);
		plusBtn.setOnTouchListener(mTouchListener);
	}
	
	public void setButtonState(int btnId, boolean state) {
		
		findViewById(btnId).setEnabled(state);
	}

	Button.OnTouchListener mTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch(event.getAction()) {
			
			case MotionEvent.ACTION_DOWN	:
				
				switch(v.getId()) {
			
				case R.id.backBtn	:
					mDisplayPresenter.changeActivity();
					break;
					
				case R.id.minusBtn	:
					mDisplayPresenter.downBrightness();
					break;
					
				case R.id.plusBtn	:
					mDisplayPresenter.upBrightness();
					break;
					
				default	:
					break;
				}
				
				break;
			}
			
			return false;
		}
	};
	
	public void setBarGaugeImage(int drawable) {
		
		barGaugeImage.setBackgroundResource(drawable);
	}
}
