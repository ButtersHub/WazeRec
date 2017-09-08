package com.wazesounds;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
	public class CustomizeProgressBar extends Dialog {

		
		
		@Override
		public void onWindowAttributesChanged(LayoutParams params) {
			super.onWindowAttributesChanged(params);

		}

		Activity mContext;
		View v = null;
		private TextView _msg;

		

		public CustomizeProgressBar(Activity context) {
			super(context);
			mContext = context;	
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			
			setContentView(R.layout.my_progressbar_dialog);
	

			v = getWindow().getDecorView();
			v.setBackgroundResource(android.R.color.transparent);
			
			_msg = (TextView) findViewById(R.id.message);
			
			setCanceledOnTouchOutside(false);

			Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/VAGLight.TTF");
			_msg.setTypeface(tf);
						
		}
		
		
		public void setMessage(String msg){				
			_msg.setText(msg);		
		}
		
		
		
		
		
		


	



	}
	

