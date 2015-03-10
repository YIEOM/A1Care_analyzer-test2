package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.Calendar;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FileLoadActivity extends Activity {

	public DataStorage mDataStorage;

	final static byte CONTROL = 1,
					  PATIENT = 2;
	
	private String fileDateTime  [] = new String[5],
			   	   fileTestNum   [] = new String[5],
				   fileRefNum    [] = new String[5],
				   filePatientID [] = new String[5],
				   fileOperatorID[] = new String[5],
				   filePrimary   [] = new String[5],
				   fileHbA1c     [] = new String[5];
			   
	String filePath = "",
		   loadData;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);
		
		StringInit();
		
		FileLoad();
	}
	
	public void StringInit(){
		
		for(int i = 0; i < 5; i++) {
			
			fileDateTime  [i] = null;
			fileTestNum   [i] = null;
			fileRefNum    [i] = null;
			filePatientID [i] = null;
			fileOperatorID[i] = null;
			filePrimary   [i] = null;
			fileHbA1c     [i] = null;
		}
	}
	
	public void FileLoad() { // loading 10 recently saved data
		
		int dataPage,
			type;
		
		int	pIdx,
			pLen,
			oIdx,
			oLen;
		
		Intent itn = getIntent();
		
		FileSaveActivity.DataCnt = itn.getIntExtra("DataCnt", 0);
		dataPage = itn.getIntExtra("DataPage", 0);
		type = itn.getIntExtra("Type", 0);
		
		mDataStorage = new DataStorage();
		
		for (int i = 0; i < 5 ; i++) {
		
			filePath = mDataStorage.FileCheck(dataPage*5 + i + 1, type);
			
			if(filePath != null) { // If file exist
				
				loadData = mDataStorage.DataLoad(filePath);
				
				pIdx = 23 + 2;
				pLen = Integer.parseInt(loadData.substring(23, pIdx));
				oIdx = pIdx + pLen + 2;
				oLen = Integer.parseInt(loadData.substring(pIdx + pLen, oIdx));
				
				fileDateTime  [i] = loadData.substring(0, 4) + loadData.substring(4, 6) + loadData.substring(6, 8) + loadData.substring(8, 10) + 
								loadData.substring(10, 12) + loadData.substring(12, 14);
				fileTestNum   [i] = loadData.substring(14, 18);
				fileRefNum    [i] = loadData.substring(18, 23);
				filePatientID [i] = loadData.substring(pIdx, pIdx + pLen);
				fileOperatorID[i] = loadData.substring(oIdx, oIdx + oLen);
				filePrimary   [i] = loadData.substring(oIdx + oLen, oIdx + oLen + 1);
				fileHbA1c     [i] = loadData.substring(oIdx + oLen + 1);
			}
		}
		
		WhichIntent(type);
	}
	
	public void WhichIntent(int type) { // Activity conversion
	
		Intent nextIntent = null;
		
		switch(type) {
		
		case CONTROL	:
			nextIntent = new Intent(getApplicationContext(), ControlTestActivity.class);
			break;
			
		case PATIENT	:
			nextIntent = new Intent(getApplicationContext(), PatientTestActivity.class);
			break;
			
		default	:
			break;
		}
		
		nextIntent.putExtra("DateTime", fileDateTime);
		nextIntent.putExtra("TestNum", fileTestNum);
		nextIntent.putExtra("RefNumber", fileRefNum);
		nextIntent.putExtra("PatientID", filePatientID);
		nextIntent.putExtra("OperatorID", fileOperatorID);
		nextIntent.putExtra("Primary", filePrimary);
		nextIntent.putExtra("HbA1c", fileHbA1c);
		startActivity(nextIntent);
				
		finish();
	}
	
	public void finish() {
		
		super.finish();
	}
}