package isens.hba1c_analyzer.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.MaintenanceActivity;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SystemSettingActivity;

public class ActivityChange {

	Activity activity;
	Context context;
	Intent nextIntent = null, currIntent = null;
	
	public ActivityChange(Activity activity, Context context) {
		
		this.activity = activity;
		this.context = context;
	}
	
	public void whichIntent(TargetIntent Itn) { // Activity conversion
		
		switch(Itn) {
		
		case SystemSetting	:				
			nextIntent = new Intent(context, SystemSettingActivity.class);
			break;
						
		case Maintenance	:
			nextIntent = new Intent(context, MaintenanceActivity.class);
			break;
			
		default		:	
			break;			
		}
	}
	
	public void putIntIntent(String name, int data) {
		
		nextIntent.putExtra(name, data);
	}
	
	public void putStringIntent(String name, String data) {

		nextIntent.putExtra(name, data);		
	}
	
	public void setIntent() {
		
		currIntent = activity.getIntent();
	}
	
	public int getIntIntent(String name, int defaultValue) {
		
		return currIntent.getIntExtra(name, defaultValue);
	}
	
	public String getStringIntent(String name) {
		
		return currIntent.getStringExtra(name);
	}
	
	public void finish() {
		
		activity.startActivity(nextIntent);
		activity.finish();
		activity.overridePendingTransition(R.anim.fade, R.anim.hold);
	}
}
