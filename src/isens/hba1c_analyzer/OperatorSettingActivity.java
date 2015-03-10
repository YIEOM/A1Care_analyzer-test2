package isens.hba1c_analyzer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import isens.hba1c_analyzer.HomeActivity.TargetIntent;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class OperatorSettingActivity extends Activity {
	
	final static byte PRE_VIEW  = 0,
					  NEXT_VIEW = 1;
	
	public OperatorController mOperatorController;
	public DatabaseHander mDatabaseHander;
	public TimerDisplay mTimerDisplay;
	
	public static TextView TimeText;
	public static ImageView deviceImage;
	
	private Button homeIcon,
				   backIcon,
				   addOperatorBtn,
				   modOperatorBtn,
				   delOperatorBtn,
				   nextViewBtn,
				   preViewBtn,
				   loginBtn;
	
	private ImageButton checkBoxBtn1,
						checkBoxBtn2,
						checkBoxBtn3,
						checkBoxBtn4,
						checkBoxBtn5;
	
	private TextView OperatorText[] = new TextView[5],
			 		 DateTimeText[] = new TextView[5],
			 		 PasswordText[] = new TextView[5],
			 		 CommentText [] = new TextView[5];
	
	private boolean checkFlag = false;
	private ImageButton whichBox = null;
	
	public TextView pageText;
	
	private int boxNum = 0;
	private static int pageNum = 1;

	public boolean btnState = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		setContentView(R.layout.operatorsetting);
		
		TimeText = (TextView) findViewById(R.id.timeText);
		deviceImage = (ImageView) findViewById(R.id.device);
		
		OperatorInit();
		
		/*Home Activity activation*/
		homeIcon = (Button)findViewById(R.id.homeicon);
		homeIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {

				if(!btnState) {
					
					btnState = true;
					homeIcon.setEnabled(false);
					
					WhichIntent(TargetIntent.Home);
				}
			}
		});
		
		checkBoxBtn1 = (ImageButton) findViewById(R.id.chdckbox1);
		checkBoxBtn1.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_DOWN) { 
					
					boxNum = 1;
					PressedCheckBox(checkBoxBtn1);
				}
				return false;
			}
		});
		
		checkBoxBtn2 = (ImageButton) findViewById(R.id.chdckbox2);
		checkBoxBtn2.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_DOWN) { 
					
					boxNum = 2;
					PressedCheckBox(checkBoxBtn2);
				}
				return false;
			}
		});
		
		checkBoxBtn3 = (ImageButton) findViewById(R.id.chdckbox3);
		checkBoxBtn3.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_DOWN) { 
					
					boxNum = 3;
					PressedCheckBox(checkBoxBtn3);
				}
				return false;
			}
		});
		
		checkBoxBtn4 = (ImageButton) findViewById(R.id.chdckbox4);
		checkBoxBtn4.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
	
				if(event.getAction() == MotionEvent.ACTION_DOWN) { 
					
					boxNum = 4;
					PressedCheckBox(checkBoxBtn4);
				}
				return false;
			}
		});
		
		checkBoxBtn5 = (ImageButton) findViewById(R.id.chdckbox5);
		checkBoxBtn5.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_DOWN) { 
					
					boxNum = 5;
					PressedCheckBox(checkBoxBtn5);
				}
				return false;
			}
		});
		
		preViewBtn = (Button)findViewById(R.id.previousviewbtn);
		preViewBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
				
					TurnPage(PRE_VIEW);
				}
			}
		});
		
		/*Login Operator pop-up window activation*/
		loginBtn = (Button)findViewById(R.id.loginbtn);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
					
					Login();
					
					btnState = false;
				}
			}
		});
		
		/*Modify Operator pop-up window activation*/
		modOperatorBtn = (Button)findViewById(R.id.modifybtn);
		modOperatorBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
				
					Modify();
					
					btnState = false;
				}
			}
		});
		
		/*Addition Operator pop-up window activation*/
		addOperatorBtn = (Button)findViewById(R.id.addbtn);
		addOperatorBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
				
					Add();
					
					btnState = false;
				}
			}
		});
		
		/* Delete Operator pop-up window activation */
		delOperatorBtn = (Button)findViewById(R.id.deletebtn);
		delOperatorBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					btnState = true;
				
					Delete();
					
					btnState = false;
				}
			}
		});
		
		nextViewBtn = (Button)findViewById(R.id.nextviewbtn);
		nextViewBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
			
				if(!btnState) {
					
					TurnPage(NEXT_VIEW);
				}
			}
		});
		
		/*Setting Activity activation*/
		backIcon = (Button)findViewById(R.id.backicon);
		backIcon.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {

				if(!btnState) {
					
					btnState = true;
					backIcon.setEnabled(false);
					
					WhichIntent(TargetIntent.Setting);
				}
			}
		});
	}	
	
	public void OperatorInit() {
		
		int count;
		
		mTimerDisplay = new TimerDisplay();
		mTimerDisplay.ActivityParm(this, R.id.operatorlayout);
		
		mOperatorController = new OperatorController(this, getApplicationContext(), R.id.operatorlayout);
		count = mOperatorController.OperatorCount();
		OperatorDisplay(this, mOperatorController.ReadOperator(count), count, count);
	}

	public void Login() {
		
		String id = null;
		
		if(checkFlag) id = OperatorText[boxNum - 1].getText().toString();
		
		mOperatorController = new OperatorController(this, this, R.id.operatorlayout);
		mOperatorController.OperatorLoginDisplay(id);
	}
	
	public void Add() {
		
		mOperatorController = new OperatorController(this, this, R.id.operatorlayout);
		mOperatorController.AddOperatorDisplay();
	}
	
	public void Modify() {
		
		if(checkFlag && !OperatorText[boxNum - 1].getText().toString().equals("")) {
		
			mOperatorController = new OperatorController(this, this, R.id.operatorlayout);
			mOperatorController.ModOperatorDisplay(OperatorText[boxNum - 1].getText().toString());
		}
	}
	
	public void Delete() {
		
		if(checkFlag && !OperatorText[boxNum - 1].getText().toString().equals("")) {
			
			mOperatorController = new OperatorController(this, this, R.id.operatorlayout);
			mOperatorController.DelOperatorDisplay(OperatorText[boxNum - 1].getText().toString());
		}
	}
	
	public void PressedCheckBox(ImageButton box) { // displaying the button pressed
		
		if(!checkFlag) { // whether or not box is checked
	
			checkFlag = true;
			box.setBackgroundResource(R.drawable.checkbox_s); // changing to checked box
			
		} else {

			whichBox.setBackgroundResource(R.drawable.checkbox); // changing to not checked box

			if(whichBox == box) {
				
				checkFlag = false;
				
			} else {
				
				box.setBackgroundResource(R.drawable.checkbox_s);
			}
		}
		
		whichBox = box;
	}
	
	public void OperatorText(Activity activity) {
		
		OperatorText[0] = (TextView) activity.findViewById(R.id.operator1);
		DateTimeText[0] = (TextView) activity.findViewById(R.id.dateTime1);
		PasswordText[0] = (TextView) activity.findViewById(R.id.password1);
		CommentText [0] = (TextView) activity.findViewById(R.id.comment1);
		
		OperatorText[1] = (TextView) activity.findViewById(R.id.operator2);
		DateTimeText[1] = (TextView) activity.findViewById(R.id.dateTime2);
		PasswordText[1] = (TextView) activity.findViewById(R.id.password2);
		CommentText [1] = (TextView) activity.findViewById(R.id.comment2);
		
		OperatorText[2] = (TextView) activity.findViewById(R.id.operator3);
		DateTimeText[2] = (TextView) activity.findViewById(R.id.dateTime3);
		PasswordText[2] = (TextView) activity.findViewById(R.id.password3);
		CommentText [2] = (TextView) activity.findViewById(R.id.comment3);
		
		OperatorText[3] = (TextView) activity.findViewById(R.id.operator4);
		DateTimeText[3] = (TextView) activity.findViewById(R.id.dateTime4);
		PasswordText[3] = (TextView) activity.findViewById(R.id.password4);
		CommentText [3] = (TextView) activity.findViewById(R.id.comment4);
		
		OperatorText[4] = (TextView) activity.findViewById(R.id.operator5);
		DateTimeText[4] = (TextView) activity.findViewById(R.id.dateTime5);
		PasswordText[4] = (TextView) activity.findViewById(R.id.password5);
		CommentText [4] = (TextView) activity.findViewById(R.id.comment5);
		
		pageText = (TextView) activity.findViewById(R.id.pagetext);
	}

	public void OperatorDisplay(Activity activity, String[][] Operator, int last, int numofRow) {
		
		int first,
			dataCnt;
		
		if(last > 5) {
			
			first = last - 5;
			dataCnt = 5;
		
		} else {
			
			first = 0;
			dataCnt = last;
		}
		
		OperatorText(activity);
		
		for(int i = 0; i < 5; i++) {
			
			OperatorText[i].setText("");
			DateTimeText[i].setText("");
			PasswordText[i].setText("");
			CommentText [i].setText("");
		}
		
		for(int i = 0; i < dataCnt; i++) {	
			
			OperatorText[i].setText(Operator[0][i]);
			DateTimeText[i].setText(Operator[1][i]);
			PasswordText[i].setText(Operator[2][i]);
			CommentText [i].setText(Operator[3][i]);
		}
		
		if(last == numofRow) pageNum = 1;
		
		int tPage = (numofRow+4)/5;
		if(tPage == 0) tPage = 1;
		String page = Integer.toString(pageNum) + " / " + Integer.toString(tPage);
		
		pageText.setText(page);
		
//		Log.w("Operator display", "last : " + last + " page : " + pageNum);
	}
	
	public void TurnPage(int direction) {
		
		int count,
			last,
			tPage;
		
//		Log.w("Turn page", "page : " + pageNum);
		
		mOperatorController = new OperatorController(this, this, R.id.operatorlayout);
		count = mOperatorController.OperatorCount();
		
		switch(direction) {
		
		case PRE_VIEW	:
			if(pageNum > 1) {
				pageNum--;
				last = count-((pageNum-1)*5);
				OperatorDisplay(this, mOperatorController.ReadOperator(last), last, count);
			}
			break;
			
		case NEXT_VIEW	:
			tPage = (count+4)/5;
			
			if(tPage > pageNum) {
				pageNum++;
				last = count-((pageNum-1)*5);
				OperatorDisplay(this, mOperatorController.ReadOperator(last), last, count);
			}
			break;
			
		default	:
			break;
		}
	}
	
	public void WhichIntent(TargetIntent Itn) { // Activity conversion
		
		Intent nextIntent = null;
		
		switch(Itn) {
		
		case Home		:				
			nextIntent = new Intent(getApplicationContext(), HomeActivity.class);
			break;
						
		case Setting	:				
			nextIntent = new Intent(getApplicationContext(), SettingActivity.class);
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