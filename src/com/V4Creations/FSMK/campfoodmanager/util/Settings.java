package com.V4Creations.FSMK.campfoodmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

	private static final String PREFS_IS_SOUND_ENABLED = "isSoundEnabled";
	private static final String PREFS_IS_VIBRATION_ENABLED = "isVibrationEnabled";
	private static final String PREFS_IS_CONFIRM_EEACH_QR_CODE = "isConfirmEachQRCode";

	public static boolean isSoundEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_SOUND_ENABLED, true);
	}

	public static boolean isVibrationEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_VIBRATION_ENABLED, true);
	}

	public static void setConfirmEachQRCode(Context context, boolean status) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(Settings.PREFS_IS_CONFIRM_EEACH_QR_CODE, status);
		edit.commit();
	}

	public static boolean isConfirmEachQRCode(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREFS_IS_CONFIRM_EEACH_QR_CODE, true);
	}
}
