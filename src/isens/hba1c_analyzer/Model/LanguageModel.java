package isens.hba1c_analyzer.Model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import isens.hba1c_analyzer.R;
import isens.hba1c_analyzer.SystemSettingActivity;

public class LanguageModel {

	final static int KO = 0,
			 		 EN = 1;

	Activity activity;
			
	private String[] languageTable = new String[] {"ko", "en"};
	
	private int idx = 0;
	
	public LanguageModel(Activity activity) {
		
		this.activity = activity;
	}
	
	public void initLanguage() {
		
		Locale systemLocale = activity.getResources().getConfiguration().locale;
		String language = systemLocale.getLanguage();
		
		getLanguageIdx(language);
	}
	
	public int getLanguage() {
		
		int language;
		
		switch(idx) {
		
		case KO	:
			language = R.string.korean;
			break;
			
		case EN	:
			language = R.string.english;
			break;
			
		default	:
			getLanguageIdx("en");
			language = R.string.english;
			break;
		}	
		
		return language;
	}
	
	public void getLanguageIdx(String language) {
		
		int i;
		
		for(i = 0; i < languageTable.length; i++) {
			
			idx = i;
			
			if(languageTable[i].equals(language)) break;
		}
	}
	
	public void upLanguageIdx() {
		
		if(idx-- == 0) idx = languageTable.length - 1;
	}
	
	public void downLanguageIdx() {
		
		if(idx++ == (languageTable.length - 1)) idx = 0;
	}
	
	public void setLocale() {
		
		try {
			
			Locale locale = new Locale(languageTable[idx]);
			
			Class<?> amnClass = Class.forName("android.app.ActivityManagerNative");
			Object amn = null;
			Configuration config = null;
			
			Method methodGetDefault = amnClass.getMethod("getDefault");
			methodGetDefault.setAccessible(true);
			amn = methodGetDefault.invoke(amnClass);
			
			Method methodGetConfiguration = amnClass.getMethod("getConfiguration");
			methodGetConfiguration.setAccessible(true);
			config = (Configuration) methodGetConfiguration.invoke(amn);
			
			Class<? extends Configuration> configClass = config.getClass();
			Field f = configClass.getField("userSetLocale");
			f.setBoolean(config, true);
			
			config.locale = locale;
			
			Method methodUpdateConfiguration = amnClass.getMethod("updateConfiguration", Configuration.class);
			methodUpdateConfiguration.setAccessible(true);
			methodUpdateConfiguration.invoke(amn, config);
		
		} catch(Exception e) {
			
		}
	}
}
