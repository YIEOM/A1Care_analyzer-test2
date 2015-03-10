package isens.hba1c_analyzer.Model;

import android.app.Activity;
import android.content.SharedPreferences;

public class FileSystem {

	private Activity activity;
	
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;
	
	public FileSystem(Activity activity) {
		
		this.activity = activity;
	}

	public void setPreferences(String name, int mode) {
		
		mSharedPreferences = activity.getSharedPreferences(name, mode);
		mEditor = mSharedPreferences.edit();
	}
	
	public void putBooleanPref(String key, boolean value) {
		
		mEditor.putBoolean(key, value);
	}
	
	public void putIntPref(String key, int value) {
		
		mEditor.putInt(key, value);
	}
	
	public void putStringPref(String key, String value) {
		
		mEditor.putString(key, value);
	}

	public void putFloatPref(String key, float value) {
		
		mEditor.putFloat(key, value);
	}
	
	public void commitPref() {
		
		mEditor.commit();
	}
}
