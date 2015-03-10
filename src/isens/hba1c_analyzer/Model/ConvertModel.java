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

public class ConvertModel {

	public final static byte NGSP = 0,
					 		 IFCC = 1;
	
	public static byte Primary;
	
	private Activity activity;

	private byte[] primaryTable = new byte[] {NGSP, IFCC};
	
	private int idx = 0;
	
	public ConvertModel(Activity activity) {
		
		this.activity = activity;
	}
	
	public void initPrimary() {
		
		getPrimaryIdx(Primary);
	}
	
	public int getPrimary() {
		
		int primary;
		
		switch(idx) {
		
		case NGSP	:
			primary = R.string.ngsp;
			break;
			
		case IFCC	:
			primary = R.string.ifcc;
			break;
			
		default	:
			getPrimaryIdx(NGSP);
			primary = R.string.ngsp;
			break;
		}	
		
		return primary;
	}
	
	public void getPrimaryIdx(byte primary) {
		
		int i;
		
		for(i = 0; i < primaryTable.length; i++) {
			
			idx = i;
			
			if(primaryTable[i] == primary) break;
		}
	}
	
	public void upPrimaryIdx() {
		
		if(idx-- == 0) idx = primaryTable.length - 1;
	}
	
	public void downPrimaryIdx() {
		
		if(idx++ == (primaryTable.length - 1)) idx = 0;
	}
	
	public void setPrimary() {

		FileSystem mFileSystem = new FileSystem(activity);
		mFileSystem.setPreferences("Primary", Activity.MODE_PRIVATE);
		mFileSystem.putIntPref("Convert", idx);
		mFileSystem.commitPref();
		
		Primary = (byte) idx;
	}
}
