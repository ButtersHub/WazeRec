package com.wazesounds;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;

import android.R.bool;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
public class CustomizeMenu extends Dialog implements OnClickListener {

	@Override
	public void onWindowAttributesChanged(LayoutParams params) {
		super.onWindowAttributesChanged(params);

	}

	Activity mContext;
	View v = null;
	private Button _aboutBtn;
	//private Button _zipExBtn;
	private Button _upgradeBtn;
	private Button _feedbackBtn;
	private Button _revertAll;
	private Button  _rateBtn;

	public CustomizeMenu(Activity context) {
		super(context);
		mContext = context;

		/** 'Window.FEATURE_NO_TITLE' - Used to hide the mTitle */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Design the dialog in main.xml file */

		setContentView(R.layout.my_menu_dialog);
		v = getWindow().getDecorView();
		v.setBackgroundResource(android.R.color.transparent);

		_aboutBtn = (Button) findViewById(R.id.about_btn);
	//	_zipExBtn = (Button) findViewById(R.id.zipex_btn);
		_upgradeBtn = (Button) findViewById(R.id.upgrade_btn);
		_feedbackBtn = (Button) findViewById(R.id.feedback_btn);
		_revertAll = (Button) findViewById(R.id.revertall_btn_2);
		_rateBtn = (Button) findViewById(R.id.rate);

		Typeface tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/VAGLight.TTF");
		_aboutBtn.setTypeface(tf);
	//	_zipExBtn.setTypeface(tf);
		_upgradeBtn.setTypeface(tf);
		_feedbackBtn.setTypeface(tf);
		_revertAll.setTypeface(tf);
		_rateBtn.setTypeface(tf);

		_aboutBtn.setOnClickListener(this);
		//_zipExBtn.setOnClickListener(this);
		_upgradeBtn.setOnClickListener(this);
		_feedbackBtn.setOnClickListener(this);
		_revertAll.setOnClickListener(this);
		_rateBtn.setOnClickListener(this);
		
		
		File currBackupDir = new File(WazeSoundsMain.pathToBackupSounds + File.separator + WazeSoundsMain.mCurrDirName);
		if(!currBackupDir.exists()){
			_revertAll.setEnabled(false);
			_revertAll.setTextColor(Color.GRAY);
		}else{
			_revertAll.setEnabled(true);
			_revertAll.setTextColor(Color.BLACK);
		}
		

	}

	@Override
	public void onClick(View v) {
		if (v == _aboutBtn) {
			Constant.Log_d("pressed_aboutBtn");
			EasyTracker.getTracker().sendEvent("ui_action", "button_press", "about_btn",null);
			CustomizeDialog customizeDialog = new CustomizeDialog(mContext, CustomizeDialog.Type.About);
			customizeDialog.setCanceledOnTouchOutside(true);
			customizeDialog.show();

		} else if (v == _revertAll) {
			Constant.Log_d("pressed _revertAll");
			EasyTracker.getTracker().sendEvent("ui_action", "button_press", "revert_all_btn",null);
			new RevertAll().execute();

		} else if (v == _upgradeBtn) {
			Constant.Log_d("pressed menu_upgradeBtn");
			EasyTracker.getTracker().sendEvent("ui_action", "button_press", "upgrade_btn",null);
			Constant.goToPro(mContext);

		} else if (v == _feedbackBtn) {
			Constant.Log_d("pressed _feedbackBtn");
			EasyTracker.getTracker().sendEvent("ui_action", "button_press", "feedbackBtn_btn",null);
			Intent intent;
			try {
				intent = Intent.parseUri("mailto:Lev.Vidrak@gmail.com?subject=WazeRec - Feedback & Ideas", Intent.URI_INTENT_SCHEME);
				mContext.startActivity(intent);
			} catch (Exception e) {		
				e.printStackTrace();
				Constant.Log_e("Failed in menu - _feedbackBtn e: "+e.getMessage());
			}
			
			
			

	

		} else if (v == _rateBtn) {
			Constant.Log_d("pressed menu_rateBtn");
			EasyTracker.getTracker().sendEvent("ui_action", "button_press", "rateBtn_btn",null);
			Constant.goToLite(mContext);
	}

		
		cancel();
	}
	
	
	



	// The definition of our task class
	private class RevertAll extends AsyncTask<String, Integer, String> {
		private CustomizeProgressBar pdialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pdialog = new CustomizeProgressBar(mContext);
			pdialog.setMessage("Reverting all " + Constant.nameDictionary(WazeSoundsMain.mCurrDirName) + " sounds...");			
			pdialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				File currBackupDir = new File(WazeSoundsMain.pathToBackupSounds + File.separator + WazeSoundsMain.mCurrDirName);

				List<String> revertFileList;
				revertFileList = WazeSoundsMain.loadFileList(currBackupDir);

				for (int i = 0; i < revertFileList.size(); i++) {
					String backUpFile = revertFileList.get(i);
					String pathToCurr = WazeSoundsMain.pathToCurrSounds + File.separator + backUpFile;
					String pathToBackup = currBackupDir + File.separator + backUpFile;
					WazeSoundsMain.copyFile(pathToBackup, pathToCurr);
/*					File f = new File(pathToBackup);
					f.delete();*/
				}

				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return "All Done!";
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pdialog.isShowing()) {
				pdialog.dismiss();
			}
		}
	}

}
