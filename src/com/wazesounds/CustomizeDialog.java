package com.wazesounds;

import java.net.URISyntaxException;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;


	/** Class Must extends with Dialog */
	/** Implement onClickListener to dismiss dialog when OK Button is pressed */
	public class CustomizeDialog extends Dialog implements OnClickListener {

		public enum Type {
			About, UPGRADE, ERROR, INTRO
		}
		
		
		@Override
		public void onWindowAttributesChanged(LayoutParams params) {
			super.onWindowAttributesChanged(params);

		}

		Activity mContext;
		View v = null;
		private Button _upgradeBtn;
		private Typeface _tf;
		private TextView _msg;
		private Button _agreeBtn;
		private TextView _email;

		

		public CustomizeDialog(Activity context,Type type ) {
			super(context);
			mContext = context;				
			requestWindowFeature(Window.FEATURE_NO_TITLE);	
			
			v = getWindow().getDecorView();
			v.setBackgroundResource(android.R.color.transparent);
			
			_tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/VAGLight.TTF");
						
			
			if(type==Type.About){
				initAboutDialog();	
			}
			
			if(type==Type.UPGRADE){
				initUpgradeDialog();	
			}
			
			if(type==Type.ERROR){
				initErrorDialog();	
			}
			
			if(type==Type.INTRO){
				initIntroDialog();	
			}
			

		}
		
		private void initIntroDialog() {
			setContentView(R.layout.my_intro_dialog);
			TextView msg_1 = (TextView)findViewById(R.id.message_1);
			TextView msg_2 = (TextView)findViewById(R.id.message_2);
			
			TextView revertMsg_1 = (TextView)findViewById(R.id.rever_msg);
			TextView revertMsg_2 = (TextView)findViewById(R.id.rever_msg_2);
			TextView tersm = (TextView)findViewById(R.id.terms_1);
			_agreeBtn = (Button)findViewById(R.id.agree_btn);
			
			TextView header = (TextView)findViewById(R.id.header);
															
			msg_1.setTypeface(_tf);
			msg_2.setTypeface(_tf);
			revertMsg_1.setTypeface(_tf);
			revertMsg_2.setTypeface(_tf);
			tersm.setTypeface(_tf);
			_agreeBtn.setTypeface(_tf);
			header.setTypeface(_tf);
			
			_agreeBtn.setOnClickListener(this);
			
		}

		public void setMessage(String msg){
			_msg.setText(msg);
		}
		
		
		private void initErrorDialog() {
			setContentView(R.layout.my_error_dialog);
			_msg = (TextView)findViewById(R.id.message);
			TextView header = (TextView)findViewById(R.id.header);
			
			_msg.setTypeface(_tf);
			header.setTypeface(_tf);
			
			
						
		}




		private void initUpgradeDialog() {
			setContentView(R.layout.my_upgrade_dialog);
			TextView msg = (TextView)findViewById(R.id.message);
			TextView header = (TextView)findViewById(R.id.header);
			_upgradeBtn = (Button)findViewById(R.id.upgrade_btn);
			_upgradeBtn.setOnClickListener(this);
			
			msg.setTypeface(_tf);
			header.setTypeface(_tf);
			_upgradeBtn.setTypeface(_tf);
		}




		private void initAboutDialog() {
			setContentView(R.layout.my_alert_dialog);
			TextView msg = (TextView)findViewById(R.id.message);
			TextView header = (TextView)findViewById(R.id.header);
			_email = (TextView)findViewById(R.id.email);
			TextView msg2 = (TextView)findViewById(R.id.message_2);
			
			
			
			msg.setTypeface(_tf);
			header.setTypeface(_tf);
			_email.setTypeface(_tf);		
		
			_email.setOnClickListener(this);
			
			//email.setText(Html.fromHtml("<a style=\"text-decoration:none;color:#000000;\" href=\"mailto:Lev.Vidrak@gmail.com?subject=WazeRec\" >Lev.Vidrak@gmail.com</a>"));
			_email.setMovementMethod(LinkMovementMethod.getInstance());					
			msg2.setTypeface(_tf);
			
			
		}





		
		@Override
		public void onClick(View v) {
			
			if(v==_upgradeBtn){
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "get_the_upgrade_btn",null);
				Constant.goToPro(mContext);
			}
		
			
			else if(v==_agreeBtn){
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "get_the_agree_btn",null);
				setCancelable(true);
			}
			
			else if(v==_email){
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "get_the_email_btn",null);
				Intent intent;
				try {
					intent = Intent.parseUri("mailto:Lev.Vidrak@gmail.com?subject=WazeRec", Intent.URI_INTENT_SCHEME);
					mContext.startActivity(intent);
				} catch (URISyntaxException e) {				
					e.printStackTrace();
				}
				
			}
			
			cancel();
		}





	}
	

