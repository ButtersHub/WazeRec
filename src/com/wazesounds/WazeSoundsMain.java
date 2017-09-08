package com.wazesounds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.fbstuff.FBUtils;
import com.fbstuff.FacebookConnector;
import com.google.analytics.tracking.android.EasyTracker;

public class WazeSoundsMain extends ListActivity {

	private static final String PREFS_NAME = "PREFS_WAZEREC";
	public static File pathToWazeDir = new File(Environment.getExternalStorageDirectory() + File.separator + "Waze");
	public static File pathToSoundsDir = new File(pathToWazeDir + File.separator + "sound");
	public static File pathToCurrSounds = new File(Environment.getExternalStorageDirectory() + File.separator + " Waze" + File.separator + "sound" + File.separator + "eng");
	public static File pathToBackupSounds = new File(Environment.getExternalStorageDirectory() + File.separator + "WazeSounds");

	public static String Tag = "WazeSounds";

	private List<String> _SoundFileList;
	private SoundFileListAdapter _adapter;
	List<String> _dirList = new ArrayList<String>();

	private ImageButton _playBtn;
	private ImageButton _recordBtn;
	private ImageButton _restoreBtn;

	// facebook stuff
	private FacebookConnector facebookConnector;
	private ImageButton _facebookBtn;

	static int _selected = -1;
	static String mFileName = "";
	private String mRecFileName = "";
	private String mBackupName = "";
	static String mCurrDirName = "";

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	public boolean mStartPlaying = true;
	private boolean mStartRecording = true;

	private ImageButton _threeDotMenu;
	private Button _voicePackBtn;

	private static WazeSoundsMain _self;
	protected RecordAmplitude recordAmplitude;
	private ImageView _visBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Constant.Log_d("onCreate");
		EasyTracker.getInstance().setContext(this);		
		setContentView(R.layout.sound_file_list);
		_self = this;

		
		try {
			
			if(!checkFolderState()){			
				String msg = "Device external storage is not readable/writable (try unplug the device from your computer)";
				throw new Exception(msg);				
			}

			checkWazeDirState();

			
			// create backup directory
			pathToBackupSounds.mkdirs();
			
			
			_threeDotMenu = (ImageButton) findViewById(R.id.popupMenuBtn_1);
			_voicePackBtn = (Button) findViewById(R.id.dirs_spinner);
			_facebookBtn = (ImageButton) findViewById(R.id.fb_btn);
			_playBtn = (ImageButton) findViewById(R.id.play_btn);
			_recordBtn = (ImageButton) findViewById(R.id.record_btn_1);
			_restoreBtn = (ImageButton) findViewById(R.id.restore_btn1);
			_visBtn = (ImageView) findViewById(R.id.vis_0);
			_visBtn.setVisibility(View.INVISIBLE);
			
			

			initHeaderBtns();
			initPlayerBtns();
			getSoundsDirs();

			if (_dirList.isEmpty()) {
				String msg = "sounds dir (" + pathToSoundsDir + ") is empty";									
				Constant.showErrorMsg(_self,msg,null,false);
			}

			// set default directory
			if (_dirList.contains("eng")) {
				onSelectedPackClick(_dirList.indexOf(("eng")));
			} else {
				Constant.Log_d("no english dir, setting first one");
				onSelectedPackClick(0);
			}

			Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/VAGBlack.TTF");
			_voicePackBtn.setTypeface(tf);

			_voicePackBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					EasyTracker.getTracker().sendEvent("ui_action", "button_press", "voice_pack_btn", null);
					Constant.Log_d("_voicePackBtn onClick");
					CustomizeSpinner CustomizeSpinner = new CustomizeSpinner(_self);
					CustomizeSpinner.setCanceledOnTouchOutside(true);

					for (int i = 0; i < _dirList.size(); i++) {
						CustomizeSpinner.addVoicePack(Constant.nameDictionary(_dirList.get(i)), i);
					}
					
					if(!_dirList.isEmpty()){
						CustomizeSpinner.show();	
					}				
					
				}
			});
			
			setFirstTime();
			if(isFirstTime()){
				Constant.showIntroMsg(this);				
			}
			
			
		} catch (Exception e) {
			Constant.Log_e("Exception in onCreate e: "+e.getMessage());
			Constant.showErrorMsg(_self,e.getMessage(), e,true);			
		}
			
		
		
	
	}

	private boolean isFirstTime() {
		boolean res = true;
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if(settings!=null){
			res = settings.getBoolean("first_time", true);
		}
		return res;
	}
	
	

	void setFirstTime() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if(settings!=null){
		settings.edit().putBoolean("first_time", true).commit(); 
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	private void initPlayerBtns() {

		Constant.Log_d("initPlayerBtns");
		setDisabledBtn(_playBtn, R.drawable.play_2_disable);
		setDisabledBtn(_recordBtn, R.drawable.mic_1_disabled);
		setDisabledBtn(_restoreBtn, R.drawable.restore_1_disable);
		_selected = -1;

		_playBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Constant.Log_d("_playBtn onClick ");
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "play_btn", (long) _selected);
				onPlayBtn();
			}
		});

		_recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Constant.Log_d("_recordBtn onClick");
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "record_btn", (long) _selected);
				onRecordBtn();
			}
		});

		_restoreBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Constant.Log_d("_restoreBtn onClick");
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "restore_btn", (long) _selected);

				File backup = new File(mBackupName);
				if (backup.exists()) {	
					new OnRestoreAsyncTask().execute(mBackupName);
				} else {
					Constant.Log_e("Backup (" + backup + ") file not exist");
				}

			}
		});
	}

	private void initHeaderBtns() {
		_facebookBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Constant.Log_d("_facebookBtn onClick");
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "facebook_btn", null);
				postOnFacebook();
			}
		});

		_threeDotMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Constant.Log_d("_threeDotMenu onClick");
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "menu_btn", null);
				CustomizeMenu CustomizeSpinner = new CustomizeMenu(_self);
				CustomizeSpinner.setCanceledOnTouchOutside(true);
				CustomizeSpinner.show();

			}
		});

	}

	private boolean checkFolderState() {
		String state = Environment.getExternalStorageState();
		boolean externalStorageWriteable = false;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			externalStorageWriteable = true;
			Constant.Log_d("SdCard is writable, state: " + state);
		} else {
			externalStorageWriteable = false;
			Constant.Log_e("SdCard is not writable, state: " + state);
		}

		return externalStorageWriteable;
	}
	
	private void checkWazeDirState() throws Exception {
		if(!pathToWazeDir.exists()){
			
			String msg = "Can't find Waze application (Waze file directory) on your device.";
			throw new Exception(msg);	
			
			/*Constant.showErrorMsg(_self,"Can't find Waze application (Waze file directory) on your device.", null,true);
			return false;*/
		}
		else if(!pathToSoundsDir.exists())
		{
			
			String msg = "Can't find Waze sounds files on your device.";
			throw new Exception(msg);	
/*			
			Constant.showErrorMsg(_self,"Can't find Waze sounds files on your device.", null,true);
			return false;*/
		}
	}
	
	
	
	

	/****************************************** end initialization ************************************************************/

	private void setDisabledBtn(ImageButton btn, int resId) {
		btn.setEnabled(false);
		btn.setImageDrawable(getResources().getDrawable(resId));
		btn.setAlpha(0.45f);

	}

	private void setEnabledBtn(ImageButton btn, int resId) {
		btn.setEnabled(true);
		btn.setImageDrawable(getResources().getDrawable(resId));
		btn.setAlpha(1.0f);
	}

	void invalidateList() {
		getListView().invalidateViews();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Constant.Log_d("onListItemClick id=" + id + " position=" + position);
		if (!mStartPlaying || !mStartRecording) {
			return;
		}
		int prevSelected = _selected;
		_selected = (int) id;
		String selectedFileName = _SoundFileList.get(_selected);

		if (Constant.isLockedItem(selectedFileName)) {
			EasyTracker.getTracker().sendEvent("ui_action", "button_press", "listItemClick_upgrade", (long) _selected);
			Constant.showUpgradeMessage(_self, prevSelected);
			invalidateList();
			return;
		}

		setEnabledBtn(_playBtn, R.drawable.play_1);
		setEnabledBtn(_recordBtn, R.drawable.mic_1);

		mRecFileName = pathToBackupSounds + File.separator + selectedFileName;
		mFileName = pathToCurrSounds + File.separator + selectedFileName;

		Constant.Log_d("mFileName=" + mFileName);
		Constant.Log_d("mRecFileName=" + mRecFileName);

		File currBackupDir = new File(pathToBackupSounds + File.separator + mCurrDirName);
		currBackupDir.mkdirs();
		mBackupName = currBackupDir + File.separator + selectedFileName;

		Constant.Log_d("mBackupName=" + mBackupName);

		File backup = new File(mBackupName);
		if (backup.exists()) {
			Constant.Log_d("found mBackupName file");
			setEnabledBtn(_restoreBtn, R.drawable.restore_1);
		} else {
			setDisabledBtn(_restoreBtn, R.drawable.restore_1_disable);
		}

		super.onListItemClick(l, v, position, id);
		invalidateList();
	}

	public boolean isSelected(int position) {
		return position == _selected;
	}

	public void onSelectedPackClick(int id) {

		mCurrDirName = _dirList.get(id);
		pathToCurrSounds = new File(pathToSoundsDir + File.separator + mCurrDirName);
		Constant.Log_d("onSelectedPackClick with id=" + id + " pathToCurrSounds=" + pathToCurrSounds);

		EasyTracker.getTracker().sendEvent("ui_action", "button_press", "selected_voice_pack_" + mCurrDirName, null);

		_SoundFileList = loadFileList(pathToCurrSounds);
		_adapter = new SoundFileListAdapter(this, R.layout.sound_file_list, _SoundFileList);
		setListAdapter(_adapter);

		_voicePackBtn.setText(Constant.nameDictionary(mCurrDirName));

		setDisabledBtn(_playBtn, R.drawable.play_2_disable);
		setDisabledBtn(_recordBtn, R.drawable.mic_1_disabled);
		setDisabledBtn(_restoreBtn, R.drawable.restore_1_disable);

		_selected = -1;
	}

	/****************************************** start brows stuff ************************************************************/

	public void backupFile() {
		Constant.Log_d("in backupFile");
		File backup = new File(mBackupName);
		if (!backup.exists()) {
			copyFile(mFileName, backup.getPath());
		}
	}

	private void getSoundsDirs() {
		// Checks whether path exists
		if (pathToSoundsDir.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					
					return (sel.isDirectory() && !sel.isHidden() && !TextUtils.equals(sel.getName(), "common"));
				}
			};

			String[] dList = pathToSoundsDir.list(filter);
			_dirList = Arrays.asList(dList);

		} else {
			String msg = "Path to sounds dir not exist (" + pathToSoundsDir + ")";
			Constant.Log_e(msg);		
		}

	}

	static List<String> loadFileList(File wantedDir) {

		Constant.Log_d("in loadFileList");
		List<String> retList = new ArrayList<String>();

		if (wantedDir.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					boolean isBinFiel = sel.getName().endsWith(".bin");
					return (sel.isFile() || sel.isDirectory()) && !sel.isHidden() && isBinFiel;
				}
			};

			String[] fList = wantedDir.list(filter);

			if (fList == null || fList.length == 0) {
				Constant.Log_e("no files in wantedDir (" + wantedDir + ")");
				//Constant.showErrorMsg(_self,"no files in wantedDir (" + wantedDir + ")", null, false);
				return retList;
			}

			return Arrays.asList(fList);

		} else {
			Constant.Log_e("in loadFileList - wantedDir (" + wantedDir + ") is not exist");
			return retList;
		}
	}

	static void copyFile(String inFile, String outFile) {

		Constant.Log_d("in copyFile with: inFile=" + inFile + " outFile=" + outFile);

		InputStream in;
		OutputStream out;
		try {
			in = new FileInputStream(inFile);
			out = new FileOutputStream(outFile);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Constant.Log_e("Failed in copyFile e:" + e.getMessage());
		}
	}

	/****************************************** end brows stuff ************************************************************/

	
	
	
	// The definition of our task class
		private class OnRestoreAsyncTask extends AsyncTask<String, Integer, String> {
			private CustomizeProgressBar pdialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				_restoreBtn.setImageDrawable(getResources().getDrawable(R.drawable.restore_1_press));
				pdialog = new CustomizeProgressBar(_self);
				
				String fileName = _SoundFileList.get(_selected);
				fileName = fileName.replace(".bin", "");
				pdialog.setMessage("Reverting " + Constant.nameListFixer(fileName)+ "...");
				pdialog.show();
			}

			@Override
			protected String doInBackground(String... params) {
				try {
					
					File backup = new File(params[0]);								
					copyFile(backup.getPath(), mFileName);
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
					Constant.Log_e("Failed in OnRestoreAsyncTask e:" + e.getMessage());
				}

				return "All Done!";
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				_restoreBtn.setImageDrawable(getResources().getDrawable(R.drawable.restore_1));
				if (pdialog.isShowing()) {
					pdialog.dismiss();
				}
			}
		}
	

	/****************************************** start record ************************************************************/

	public void onRecordBtn() {
		try {
			onRecord(mStartRecording);
			mStartRecording = !mStartRecording;
		} catch (Exception e) {
			Constant.Log_e("Failed in onRecordBtn e:" + e.getMessage());
		}
	}

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	synchronized private void startRecording() {

		Constant.Log_d("in startRecording");
		try {

			backupFile();
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mRecorder.setOutputFile(mRecFileName);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.prepare();		
			
			setDisabledBtn(_playBtn, R.drawable.play_2_disable);
			setDisabledBtn(_restoreBtn, R.drawable.restore_1_disable);
			_recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.mic_2));
			Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);			
			v.vibrate(70);			
			
			mRecorder.start();
			_visBtn.setVisibility(View.VISIBLE);
			recordAmplitude = new RecordAmplitude();
			recordAmplitude.execute();
			

		
		} catch (Exception e) {
			Constant.Log_e("Failed in startRecording e:" + e.getMessage());			
		}
	}

	synchronized private void stopRecording() {
		Constant.Log_d("in stopRecording");

		if (mRecorder != null) {
			setEnabledBtn(_playBtn, R.drawable.play_1);
			setEnabledBtn(_restoreBtn, R.drawable.restore_1);
			
			recordAmplitude.cancel(false);			
			_recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.mic_1));
			
			
			Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);			
			v.vibrate(70);
			
			
			mRecorder.stop();
		    mRecorder.reset();		   
			mRecorder.release();
			mRecorder = null;
			
			_visBtn.setVisibility(View.INVISIBLE);

			copyFile(mRecFileName, mFileName);


			
			
/*			final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.beep_end);
			mp1.start();
			mp1.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp1.release();
				}
			});*/
		}
	}

	private class RecordAmplitude extends AsyncTask<Void, Integer, Void> {
		
		
		
		
		
		@Override
		protected Void doInBackground(Void... params) {
			while (!mStartRecording) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
					Constant.Log_e("Failed in RecordAmplitude e:" + e.getMessage());
				}
				if(mRecorder != null){
					publishProgress(mRecorder.getMaxAmplitude());
				}
			}
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {						
			float procent = (progress[0] / 32767.0f) * 100.0f;
			int w = 2;
			int w_full = w * 400;
			int width = w;
			if (procent != 0) {
				width = (int) ((w_full * procent) / 100);
			}
			RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) (_visBtn.getLayoutParams());
			p.width = width;
			_visBtn.setLayoutParams(p);

		}
	}

	/****************************************** end record ************************************************************/

	/**************************************************** start play ***************************************/

	public void onPlayBtn() {
		try {
			onPlay(mStartPlaying);
			mStartPlaying = !mStartPlaying;
		} catch (Exception e) {
			Constant.Log_e("Failed in onPlayBtn e:" + e.getMessage());
		}

	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {

		try {
			Constant.Log_d("in startPlaying");

			_playBtn.setImageDrawable(getResources().getDrawable(R.drawable.play_1_press));
			mPlayer = new MediaPlayer();

			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();

			mPlayer.start();

			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {

					Constant.Log_d("onCompletion");
					stopPlaying();
					mStartPlaying = !mStartPlaying;
				}
			});

		} catch (Exception e) {
			Constant.Log_e("Failed in startPlaying e: " + e.getMessage());
		}
	}

	private void stopPlaying() {
		Constant.Log_d("in stopPlaying");
		_playBtn.setImageDrawable(getResources().getDrawable(R.drawable.play_1));

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}

	}

	/**************************************************** stop play ***************************************/

	/**************************************************** Facebook stuff ***************************************/

	private void postOnFacebook() {
		facebookConnector = new FacebookConnector(FBUtils.FACEBOOK_APPID, _self, getApplicationContext(), new String[] { FBUtils.FACEBOOK_PERMISSION });
		facebookConnector.postMessageOnWall(WazeSoundsMain.this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.facebookConnector.getFacebook().authorizeCallback(requestCode, resultCode, data);
	}

	/**************************************************** end facebook stuff ***************************************/

	private void ziptest() {

		List<String> arrList = new ArrayList<String>();
		for (String str : _SoundFileList) {
			String tmp = pathToCurrSounds + File.separator + str;
			arrList.add(tmp);
		}

		String[] arrFile = new String[_SoundFileList.size()];
		arrList.toArray(arrFile);

		String zipFileName = pathToBackupSounds + File.separator + "allZip.zip";
		Compress zipCom = new Compress(arrFile, zipFileName);
		zipCom.zip();
	}

}
