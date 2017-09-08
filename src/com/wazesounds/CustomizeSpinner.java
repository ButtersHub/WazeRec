package com.wazesounds;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
	public class CustomizeSpinner extends Dialog implements OnClickListener {

		@Override
		public void onWindowAttributesChanged(LayoutParams params) {
			super.onWindowAttributesChanged(params);

			// params.height = 500;
		}


	
	//	TextView headerMessage;	
		Activity mContext;
		View v = null;
		private RelativeLayout ll;

		
		public CustomizeSpinner(Activity context) {
			super(context);
			mContext = context;
			
			
			/** 'Window.FEATURE_NO_TITLE' - Used to hide the mTitle */
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			/** Design the dialog in main.xml file */
	
			setContentView(R.layout.my_spinner_dialog);
			v = getWindow().getDecorView();
			v.setBackgroundResource(android.R.color.transparent);
			//headerMessage = (TextView) findViewById(R.id.dialogHeader);
	
			
		
			//headerMessage.setVisibility(View.GONE);
						
			ll = (RelativeLayout) findViewById(R.id.LinearLayout);			

		}

		
		
		
		
		public void addVoicePack(String text,int id){
			Button newBtn = new Button(mContext);
			id=id+1;
			//LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) (newBtn.getLayoutParams());
			//p.addRule(RelativeLayout.BELOW, R.id.degem_name);
			
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);				
			if(id!=0){
				p.addRule(RelativeLayout.BELOW, id-1);
				
			}				
			p.setMargins(4, 4, 4, 4);			
			newBtn.setLayoutParams(p);
			
			newBtn.setBackgroundResource(R.drawable.pp_selector_1);
			Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/VAGLight.TTF");
			newBtn.setTypeface(tf);
			
			newBtn.setText(text);
			newBtn.setGravity(Gravity.CENTER_VERTICAL);
			newBtn.setId(id);
			newBtn.setOnClickListener(this);
			ll.addView(newBtn);	
		}
		
		
		@Override
		public void onClick(View v) {
			/** When OK Button is clicked, dismiss the dialog */
			
				((WazeSoundsMain)mContext).onSelectedPackClick(v.getId()-1);
				cancel();
		}

		@Override
		public void setTitle(CharSequence title) {
			super.setTitle(title);
			
		}

		@Override
		public void setTitle(int titleId) {
			super.setTitle(titleId);

		//	mTitle.setText(mContext.getResources().getString(titleId));

		}

		/**
		 * Set the message text for this dialog's window.
		 * 
		 * @param message
		 *            - The new message to display in the title.
		 */
		public void setMessage(CharSequence message) {
		//	mMessage.setText(message);
		//	mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
		}

		/**
		 * Set the message text for this dialog's window. The text is retrieved from the resources with the supplied
		 * identifier.
		 * 
		 * @param messageId
		 *            - the message's text resource identifier <br>
		 * @see <b>Note : if resourceID wrong application may get crash.</b><br>
		 *      Exception has not handle.
		 */
		public void setMessage(int messageId) {
		//	mMessage.setText(mContext.getResources().getString(messageId));
		//	mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
		}

	}
	

