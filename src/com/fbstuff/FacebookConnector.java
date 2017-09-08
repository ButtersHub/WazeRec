package com.fbstuff;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.android.AsyncFacebookRunner;

import com.facebook.android.DialogError;

import com.facebook.android.Facebook;
import com.facebook.android.Util;

import com.facebook.android.R;

import com.facebook.android.Facebook.DialogListener;

import com.facebook.android.FacebookError;
import com.fbstuff.SessionEvents.AuthListener;
import com.fbstuff.SessionEvents.LogoutListener;
import com.wazesounds.Constant;
import com.wazesounds.WazeSoundsMain;

public class FacebookConnector {

	private Facebook facebook = null;
	private Context context;
	private String[] permissions;
	private Handler mHandler;
	private Activity activity;
	private SessionListener mSessionListener = new SessionListener();


	public FacebookConnector(String appId, Activity activity, Context context, String[] permissions) {
		
		Constant.Log_d("in FacebookConnector");
		this.facebook = new Facebook(appId);
		
		SessionStore.restore(facebook, context);
		SessionEvents.addAuthListener(mSessionListener);
		SessionEvents.addLogoutListener(mSessionListener);
		
		this.context = context;
		this.permissions = permissions;
		this.mHandler = new Handler();
		this.activity = activity;
	}

	public void login() {
		Constant.Log_d("in FacebookConnector - login");
		if (!facebook.isSessionValid()) {
			facebook.authorize(this.activity, this.permissions, new LoginDialogListener());
		}
	}

	public void logout() {
		SessionEvents.onLogoutBegin();
		AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(this.facebook);
		asyncRunner.logout(this.context, new LogoutRequestListener());
	}

	public class SampleDialogListener extends BaseDialogListener {

		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(facebook);
				Constant.Log_d("dialog Success, post_id=" + postId);				
				asyncRunner.request(postId, new WallPostRequestListener());

			} else {
				try {
					postMessageInThread();

				} catch (Exception e) {
					e.printStackTrace();
					Constant.Log_e("Failed in SampleDialogListener e: "+e.getMessage());					
				}

			}
		}
		public void onCancel() { 
			postMessageInThread();
		}
	}

	public class WallPostRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
		}
		public void onCancel() { 
			postMessageInThread();
		}
	}
	
	


	public void postMessageOnWall(Context context) {
		Constant.Log_d("in FacebookConnector - postMessageOnWall");
		if (facebook.isSessionValid()) {
						
			facebook.dialog(context, "feed",getRandomParameters(), new SampleDialogListener());			
		} else {
			login();
		}
	}

	
	
	
	private Bundle getRandomParameters() {
		Bundle parameters = new Bundle();

		parameters.putString("message", "This is an AWESOME application, try it.");
		parameters.putString("name", "WazeRec");
		parameters.putString("link", "https://play.google.com/store/apps/details?id=com.buyzer&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5idXl6ZXIiXQ");
		parameters.putString("picture", "https://lh3.ggpht.com/9Gfl83OWbB6hRvBcMD1ucSka3078wOkMA8TRX80zq28MIstroAYodQ4bpp6VbUO9nA");
		parameters.putString("caption", "Record & customize your Waze GPS sounds.");

		return parameters;

	}
	
	
	
	private void postMessageInThread() {
		Constant.Log_e("in SampleDialogListener");
		Thread t = new Thread() {
			public void run() {
				try {
					Bundle parameters = getRandomParameters();
					String response;
					response = facebook.request("me/feed", parameters, "POST");
					Constant.Log_d("in postMessageInThread response: "+response);								
				} catch (Exception e) {
					Constant.Log_e("failed post on wall (postMessageInThread) e: "+e.getMessage());
				}
			}
		};
		t.start();
	}
	
	
	
	
	

	
	
	
	private final class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			SessionEvents.onLoginSuccess();
		}

		public void onFacebookError(FacebookError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onError(DialogError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled");
		}
	}

	public class LogoutRequestListener extends BaseRequestListener {
		public void onComplete(String response, final Object state) {
			mHandler.post(new Runnable() {
				public void run() {
					SessionEvents.onLogoutFinish();
				}
			});
		}
	}

	private class SessionListener implements AuthListener, LogoutListener {

		public void onAuthSucceed() {
			SessionStore.save(facebook, context);
		}

		public void onAuthFail(String error) {
		}

		public void onLogoutBegin() {
		}

		public void onLogoutFinish() {
			SessionStore.clear(context);
		}
	}

	public Facebook getFacebook() {
		return this.facebook;
	}
	
	


	public void inviteFriends(Context context){
		Bundle parameters = new Bundle();
		parameters.putString( "message", "Check this out!" );
		facebook.dialog( context, "apprequests", parameters,
		  new Facebook.DialogListener()
		  {
		    public void onComplete(Bundle values) {
				final String requestId  = values.getString("request");
				if (requestId  != null) {
					AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(facebook);
					Constant.Log_d("Dialog Success! request=" + requestId );
					asyncRunner.request(requestId , new WallPostRequestListener());
				}
		    }
		    public void onFacebookError( FacebookError e ) {
		    	Constant.Log_e("Failed in inviteFriends e: "+e);
		    }
		    public void onError(DialogError e) { 
		    	Constant.Log_e("Failed in inviteFriends e: "+e);
		    }
		    public void onCancel() { }

		  } );
	}
	
}
