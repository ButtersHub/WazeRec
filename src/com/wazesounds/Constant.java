package com.wazesounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class Constant {

	private final static String _START_DRIVE = "StartDrive";

	// [200meters, 200, 800meters, 800, 400meters, 400, 1000meters, 1000,
	// 1500meters, 1500, alert_1, ApproachSpeedCam, Arrive, bonus, click_long,
	// click, Exit, Fifth, First, Fourth, ft, KeepLeft, KeepRight, m, Police,
	// rec_end, rec_start, reminder, Roundabout, Second, Seventh, Sixth,
	// Straight, Third, TickerPoints, TurnLeft, TurnRight, within, ping, ping2,
	// message_ticker, AndThen, ApproachAccident, ApproachHazard,
	// ApproachTraffic, ExitLeft, ExitRight, StartDrive1, StartDrive2,
	// StartDrive3, StartDrive4, StartDrive5, StartDrive6, StartDrive7,
	// StartDrive8, StartDrive9, ApproachRedLightCam, StartDrive]

	static private final Logger LOG = LoggerFactory.getLogger(WazeSoundsMain.class);

	static String[] _lcokedList = { "ApproachSpeedCam", "Arrive", "Exit", "Fourth", "ft", "KeepRight", "m", "Police", "rec_end", "rec_start", "Second", "TurnLeft", "ApproachAccident",
			"ApproachHazard", "ApproachTraffic", "ExitRight", "StartDrive6", "StartDrive7", "StartDrive8", "StartDrive9", "ApproachRedLightCam", "StartDrive" };

	static boolean isLockedItem(String itemName) {
		boolean without = Arrays.asList(Constant._lcokedList).contains(itemName);
		itemName = itemName.replace(".bin", "");
		boolean with = Arrays.asList(Constant._lcokedList).contains(itemName);
		return without || with;
	}

	static String nameDictionary(String name) {

		name = name.replace("heb", "Hebrew");
		name = name.replace("eng", "English");
		name = name.replace('_', ' ');
		name = capitalizeString(name);

		return name;
	}

	static String nameListFixer(String fileName) {
		String[] camelCaseWords = fileName.split("(?=[A-Z])");
		String res = "";
		for (String s : camelCaseWords) {
			s = s + " ";
			res = res + s;
		}
		return res;
	}

	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You
																									// can
																									// add
																									// other
																									// chars
																									// here
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	public static void Log_d(String msg) {
		LOG.debug(msg);
		// android.util.Log.d(WazeSoundsMain.Tag, msg);
	}

	public static void Log_e(String msg) {
		LOG.error(msg);
		// android.util.Log.e(WazeSoundsMain.Tag, msg);
	}

	public static void goToPro(Context context) {
		String proAppName = "com.buyzer";
		try {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + proAppName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + proAppName)));
		}
	}

	public static void goToLite(Context context) {
		String proAppName = "com.buyzer";
		try {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + proAppName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + proAppName)));
		}
	}

	public static void showUpgradeMessage(final Activity _self, final int prevSelected) {
		CustomizeDialog customizeDialog = new CustomizeDialog(_self, CustomizeDialog.Type.UPGRADE);
		customizeDialog.setCanceledOnTouchOutside(true);
		customizeDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				WazeSoundsMain._selected = prevSelected;
				((WazeSoundsMain) _self).invalidateList();
			}
		});

		customizeDialog.show();
	}

	public static void showErrorMsg(final Activity _self, String msg, Exception e, final boolean exitApp) {

		CustomizeDialog errorDialog = new CustomizeDialog(_self, CustomizeDialog.Type.ERROR);
		errorDialog.setCanceledOnTouchOutside(true);
		errorDialog.setMessage(msg);
		errorDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (exitApp) {
					_self.finish();
				}
			}
		});
		errorDialog.show();
	}
	
	public static void showIntroMsg(final Activity _self) {

		CustomizeDialog introDialog = new CustomizeDialog(_self, CustomizeDialog.Type.INTRO);
		introDialog.setCancelable(false);
		introDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				((WazeSoundsMain)_self).setFirstTime();
			}
		});
		introDialog.show();
	}
}
