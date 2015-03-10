package isens.hba1c_analyzer;

import java.util.ResourceBundle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.renderscript.Sampler.Value;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class OperatorController {
	
	public DatabaseHander mDatabaseHander;
	public OperatorSettingActivity mOperatorSettingActivity;
	public ErrorPopup mErrorPopup;
	public HomeActivity mHomeActivity;
	public SettingActivity mSettingActivity;
	
	public Activity activity;
	public Context context;
	public int layoutid;
	
	public RelativeLayout hostLayout;
	public View popupView;
	public PopupWindow popupWindow;
	
	public EditText oIDEText,
	 				passEText;

	public Button loginBtn,
				  guestBtn,
				  cancelBtn,
		   		  checkBtn;
	
	public EditText addIDEText,
					addPasswordEText,
					addCPasswordEText;

	public Button addDoneBtn,
				  addCancelBtn;

	public TextView modOperatorText;
	
	public EditText modPasswordEText,
					modNPasswordEText,
					modCPasswordEText;

	public Button modDoneBtn,
				  modCancelBtn;
	
	public Button delOkBtn,
				  delCancelBtn;

	public TextView delOperatorText;

	
	public boolean btnState = false;
	
	public OperatorController(Activity activity, Context context, int layoutid) {
		
		this.activity = activity;
		this.context = context;
		this.layoutid = layoutid;
	}
	
	public void LoginDisplay() {
		
		hostLayout = (RelativeLayout)activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.loginpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);

		oIDEText = (EditText) popupView.findViewById(R.id.loginoid);
		passEText = (EditText) popupView.findViewById(R.id.loginpass);
		
		loginBtn = (Button)popupView.findViewById(R.id.loginbtn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					LoginCheck();
				}
			}
		});
		
		guestBtn = (Button)popupView.findViewById(R.id.guestbtn);
		guestBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					LoginGuest();
				}
			}
		});
		
		checkBtn = (Button)popupView.findViewById(R.id.checkbtn);
		checkBtn.setOnClickListener(new View.OnClickListener() { 
			
			public void onClick(View v) {
	
				if(!btnState) {
					
					btnState = true;
				
					CheckBoxDisplay(checkBtn);
					
					btnState = false;
				}
			}
		});
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
				AutoWriteLogin();
			}
		});
	}
	
	public void OperatorLoginDisplay(String id) {
		
		hostLayout = (RelativeLayout)activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.osloginpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);

		oIDEText = (EditText) popupView.findViewById(R.id.loginoid);
		passEText = (EditText) popupView.findViewById(R.id.loginpass);
		
		oIDEText.setText(id);
		
		loginBtn = (Button)popupView.findViewById(R.id.loginbtn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					LoginCheck();
				}
			}
		});
		
		cancelBtn = (Button)popupView.findViewById(R.id.cancelbtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					PopupClose();
				}
			}
		});
		
		checkBtn = (Button)popupView.findViewById(R.id.checkbtn);
		checkBtn.setOnClickListener(new View.OnClickListener() { 
			
			public void onClick(View v) {
	
				if(!btnState) {
					
					btnState = true;
				
					CheckBoxDisplay(checkBtn);
					
					btnState = false;
				}
			}
		});
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
				AutoWriteLogin();
			}
		});
	}
	
	public void LoginCheck() {
		
		int Count;
		
		String id = oIDEText.getText().toString(), 
			   password = passEText.getText().toString();
		
		if(!id.equals("")) {
			
			mDatabaseHander = new DatabaseHander(context);
			mDatabaseHander.getWritableDatabase();
			
			if(mDatabaseHander.CheckIDDuplication(id)) {
				password = mDatabaseHander.GetPassword(id);
				
				Log.w("Login Check", "password : " + password);
				
				if(passEText.getText().toString().equals(password)) {
					
					popupWindow.dismiss();
					
					CheckFlagSave();
					
					mDatabaseHander.UpdateLastLogIn(id);
					
					switch(layoutid) {
					
					case R.id.homelayout		:
						HomeActivity.LoginFlag = false;
						
						mHomeActivity = new HomeActivity();
						mHomeActivity.OperatorDisplay(activity, context);
						break;
						
					case R.id.operatorlayout	:
						mOperatorSettingActivity = new OperatorSettingActivity();
						Count = OperatorCount();
						mOperatorSettingActivity.OperatorDisplay(activity, ReadOperator(Count), Count, Count);
						break;
						
					default	:
						break;
					}
					
				} else {
							
					mErrorPopup = new ErrorPopup(activity, context, layoutid);
					mErrorPopup.ErrorBtnDisplay(R.string.w018);
				}
			} else {
				
				mErrorPopup = new ErrorPopup(activity, context, layoutid);
				mErrorPopup.ErrorBtnDisplay(R.string.w005);
			}
			
		} else {
		
			mErrorPopup = new ErrorPopup(activity, context, layoutid);
			mErrorPopup.ErrorBtnDisplay(R.string.w011);
		}
	
		btnState = false;
	}
	
	public void LoginGuest() {
		
		HomeActivity.LoginFlag = false;
		
		mDatabaseHander = new DatabaseHander(context);
		mDatabaseHander.UpdateGuestLogIn();
		
		mHomeActivity = new HomeActivity();
		mHomeActivity.OperatorDisplay(activity, context);
		
		popupWindow.dismiss();
		
		btnState = false;
	}
	
	public void AutoWriteLogin() {
		
		String id,
			   password;
		
		mDatabaseHander = new DatabaseHander(context);
			
		if(HomeActivity.CheckFlag) {
		
			checkBtn.setBackgroundResource(R.drawable.login_checkbox_check);
		
			id = mDatabaseHander.GetLastLoginID();
			password = mDatabaseHander.GetPassword(id);
			
			oIDEText.setText(id);
			passEText.setText(password);
			
		} else {
			
			checkBtn.setBackgroundResource(R.drawable.login_checkbox);
		}
	}
	
	public void CheckBoxDisplay(Button btn) {
		
		if(HomeActivity.CheckFlag) {
			
			HomeActivity.CheckFlag = false;
			btn.setBackgroundResource(R.drawable.login_checkbox);
			
		} else {
			
			HomeActivity.CheckFlag = true;
			btn.setBackgroundResource(R.drawable.login_checkbox_check);
		}
	}
	
	public void EngineerLoginDisplay() {
		
		hostLayout = (RelativeLayout)activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.egnloginpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);

		oIDEText = (EditText) popupView.findViewById(R.id.loginoid);
		passEText = (EditText) popupView.findViewById(R.id.loginpass);
		
		oIDEText.setText("ENGINEER");
		
		loginBtn = (Button)popupView.findViewById(R.id.loginbtn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					EngineerLoginCheck();
				}
			}
		});
		
		cancelBtn = (Button)popupView.findViewById(R.id.cancelbtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					EngineerPopupClose();
				}
			}
		});
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
			}
		});
	}
	
	public void EngineerLoginCheck() {
		
		String id = oIDEText.getText().toString(), 
			   password = passEText.getText().toString();
		
		if(id.equals("ENGINEER")) {
	
			if(passEText.getText().toString().equals("")) {
					
				popupWindow.dismiss();
				
				mSettingActivity = new SettingActivity();
				mSettingActivity.MaintenanceIntent(activity, context);
			}
		}
		
		btnState = false;
	}

	public void EngineerPopupClose() {
		
		mSettingActivity = new SettingActivity();
		mSettingActivity.CheatModeStop(activity);
		
		popupWindow.dismiss();
	}
	
	public void AddOperatorDisplay() {

		hostLayout = (RelativeLayout)activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.addoperatorpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);

		addIDEText = (EditText)popupView.findViewById(R.id.id);
		addPasswordEText = (EditText)popupView.findViewById(R.id.password);
		addCPasswordEText = (EditText)popupView.findViewById(R.id.cpassword);
		
		/*Add new account activation*/
		addDoneBtn = (Button)popupView.findViewById(R.id.donebtn);
		addDoneBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
				
					AddOperator();
				}
			}
		});
		
		/*AddOperator pop-up window termination*/
		addCancelBtn = (Button)popupView.findViewById(R.id.canclebtn);
		addCancelBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					PopupClose();
				
					btnState = false;
				}
			}
		});
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
			}
		});
	}
	
	public void AddOperator() {
		
		int Count;
		
		String id = addIDEText.getText().toString(),
			   aPW = addPasswordEText.getText().toString(),
			   cPW = addCPasswordEText.getText().toString();
		
		mDatabaseHander = new DatabaseHander(context);
		
		if(!id.equals("")) {
			
			if(!mDatabaseHander.CheckIDDuplication(id) & !id.equals("Guest")) {
				
				if(!aPW.equals("")) {
					
					if(!cPW.equals("")) {
						
						if(aPW.equals(cPW)) {
							 
							mDatabaseHander.AddOperator(id, TimerDisplay.rTime[0] + TimerDisplay.rTime[1] + TimerDisplay.rTime[2] + TimerDisplay.rTime[7] + TimerDisplay.rTime[5], aPW);
							
							PopupClose();
							
							mOperatorSettingActivity = new OperatorSettingActivity();
							Count = OperatorCount();
							mOperatorSettingActivity.OperatorDisplay(activity, ReadOperator(Count), Count, Count);
						
						} else {
							
							mErrorPopup = new ErrorPopup(activity, context, layoutid);
							mErrorPopup.ErrorBtnDisplay(R.string.w015);
						}
						
					} else {
						
						mErrorPopup = new ErrorPopup(activity, context, layoutid);
						mErrorPopup.ErrorBtnDisplay(R.string.w014);
					}
					
				} else {
					
					mErrorPopup = new ErrorPopup(activity, context, layoutid);
					mErrorPopup.ErrorBtnDisplay(R.string.w013);
				}
				
			} else {
				
				mErrorPopup = new ErrorPopup(activity, context, layoutid);
				mErrorPopup.ErrorBtnDisplay(R.string.w012);
			}
	
		} else {
			
			mErrorPopup = new ErrorPopup(activity, context, layoutid);
			mErrorPopup.ErrorBtnDisplay(R.string.w011);
		}
		
		btnState = false;
	}
	
	public void ModOperatorDisplay(String id) {
		
		hostLayout = (RelativeLayout)activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.modoperatorpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);
		
		modOperatorText = (TextView)popupView.findViewById(R.id.id);
		modPasswordEText = (EditText)popupView.findViewById(R.id.password);
		modNPasswordEText = (EditText)popupView.findViewById(R.id.npassword);
		modCPasswordEText = (EditText)popupView.findViewById(R.id.cpassword);
		
		modOperatorText.setText(id);
		
		/*Modify account activation*/
		modDoneBtn = (Button)popupView.findViewById(R.id.donebtn);
		modDoneBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
				
					ModOperator();
					
					btnState = false;
				}
			}
		});
		
		/*ModOperator pop-up window termination*/
		modCancelBtn = (Button)popupView.findViewById(R.id.canclebtn);
		modCancelBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					PopupClose();
					
					btnState = false;
				}
			}
		});
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
			}
		});
	}
	
	public void ModOperator() {
	
		int Count;
		
		String id = modOperatorText.getText().toString(), 
			   iPW = modPasswordEText.getText().toString(), 
			   nPW = modNPasswordEText.getText().toString(), 
			   cPW = modCPasswordEText.getText().toString();
		
		mDatabaseHander = new DatabaseHander(context);
		
		if(!iPW.equals("")) {
			
			if(iPW.equals(mDatabaseHander.GetPassword(id))) {
				
				if(!nPW.equals("")) {
					
					if(!cPW.equals("")) {
						
						if(nPW.equals(cPW)) {
							 
							mDatabaseHander.UpdateOperator(id, nPW);
							
							PopupClose();
							
							mOperatorSettingActivity = new OperatorSettingActivity();
							Count = OperatorCount();
							mOperatorSettingActivity.OperatorDisplay(activity, ReadOperator(Count), Count, Count);
						
						} else {
							
							mErrorPopup = new ErrorPopup(activity, context, layoutid);
							mErrorPopup.ErrorBtnDisplay(R.string.w018);
						}
						
					} else {
						
						mErrorPopup = new ErrorPopup(activity, context, layoutid);
						mErrorPopup.ErrorBtnDisplay(R.string.w014);
					}
					
				} else {
					
					mErrorPopup = new ErrorPopup(activity, context, layoutid);
					mErrorPopup.ErrorBtnDisplay(R.string.w017);
				}
				
			} else {
				
				mErrorPopup = new ErrorPopup(activity, context, layoutid);
				mErrorPopup.ErrorBtnDisplay(R.string.w016);
			}
	
		} else {
			
			mErrorPopup = new ErrorPopup(activity, context, layoutid);
			mErrorPopup.ErrorBtnDisplay(R.string.w013);
		}
	}
	
	public void DelOperatorDisplay(final String id) {
		
		hostLayout = (RelativeLayout)activity.findViewById(layoutid);
		popupView = View.inflate(context, R.layout.oxpopup, null);
		popupWindow = new PopupWindow(popupView, 800, 480, true);
		
		delOperatorText = (TextView)popupView.findViewById(R.id.oxtext);
		
		delOperatorText.setText("ID : " + id + " " + context.getResources().getText((R.string.delete)));
		
		/*Delete account activation*/
		delOkBtn = (Button)popupView.findViewById(R.id.yesbtn);
		delOkBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
				
					DelOperator(id);
					
					btnState = false;
				}
			}
		});
		
		/*DelOperator pop-up window termination*/
		delCancelBtn = (Button)popupView.findViewById(R.id.nobtn);
		delCancelBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				if(!btnState) {
					
					btnState = true;
					
					PopupClose();
				
					btnState = false;
				}
			}
		});
		
		hostLayout.post(new Runnable() {
			public void run() {
		
				popupWindow.showAtLocation(hostLayout, Gravity.CENTER, 0, 0);
				popupWindow.setAnimationStyle(0);
			}
		});
	}
	
	public void DelOperator(String id) {
		
		int Count;
		
		mDatabaseHander = new DatabaseHander(context);
		mDatabaseHander.DeleteOperator(id);
		
		PopupClose();
		
		mOperatorSettingActivity = new OperatorSettingActivity();
		Count = OperatorCount();
		mOperatorSettingActivity.OperatorDisplay(activity, ReadOperator(Count), Count, Count);
	}
	
	public void PopupClose() {
		
		popupWindow.dismiss();
	}
	
	public String[][] ReadOperator(int last) {
		
		int first,
			dataCnt;
		
		String tempDate,
			   tempPassword,
			   formDate,
			   formPassword;
		
		String tempHour[] = new String[2],
			   rowData[] = new String[4];
		
		String Operator[][] = new String[4][5];
		
		mDatabaseHander = new DatabaseHander(context);
		
		if(last > 5) {
			
			first = last - 5;
			dataCnt = 5;
		
		} else {
			
			first = 0;
			dataCnt = last;
		}
		
		Log.w("Operator display", "first : " + first + " last : " + last);
		
		for(int i = 0; i < dataCnt; i++) {
			
			rowData = mDatabaseHander.GetRowWithNumber(last - (i + 1));
				
			tempDate = rowData[1];
			tempPassword = rowData[2];
			
			tempHour = TimeHandling(tempDate.substring(8, 10));
			
			formDate = tempDate.substring(0, 4) + "." + tempDate.substring(4, 6) + "." + tempDate.substring(6, 8) + " " + tempHour[0] + " " + tempHour[1] + ":" + tempDate.substring(10, 12);
			formPassword = PasswordHandling(tempPassword);	
			
			Operator[0][i] = rowData[0];
			Operator[1][i] = formDate;
			Operator[2][i] = formPassword;
			Operator[3][i] = rowData[3];
		}
		
		return Operator;
	}
	
	public int OperatorCount() {
		
		int count;
		
		mDatabaseHander = new DatabaseHander(context);
		count = mDatabaseHander.GetRowCount();
		
		Log.w("Get operator", "the number : " + count);
		
		return count;
	}
	
	public String[] TimeHandling(String time) {
		
		String pTime[] = new String[2];
		int tempTime = Integer.parseInt(time);
		
		if(tempTime > 12) {
			
			tempTime -= 12;
			pTime[0] = "PM";
			
		} else if((0 < tempTime) && (tempTime < 12)) {
			
			pTime[0] = "AM";
		
		} else if(tempTime == 12) {
			
			pTime[0] = "PM";
		
		} else {
			
			tempTime = 12;
			pTime[0] = "AM";
		}
		
		pTime[1] = Integer.toString(tempTime);
		
		return pTime;
	}
	
	public String PasswordHandling(String password) {
		
		String pPassword = password.substring(0, 1);
		
		for(int i = 0; i < (password.length() - 1); i++) {
			
			pPassword = pPassword + "*";
		}
		
		return pPassword;
	}

	public void CheckFlagSave() { // Saving number of user define parameter
		
		SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor loginedit = loginPref.edit();
		
		loginedit.putBoolean("Check Box", HomeActivity.CheckFlag);
		loginedit.commit();
	}
}
