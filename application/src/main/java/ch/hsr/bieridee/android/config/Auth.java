package ch.hsr.bieridee.android.config;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import ch.hsr.bieridee.android.BierideeApplication;
import ch.hsr.bieridee.android.activities.LoginScreenActivity;

/**
 * Class to store authentication information.
 */
public final class Auth {

	/**
	 * Returns shared preferences.
	 * @return SharedPreferences instance
	 */
	private static SharedPreferences getSharedPreferences() {
		return BierideeApplication.getAppContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
	}

	/**
	 * Returns shared preferences editor.
	 * @return SharedPreferences.Editor instance
	 */
	private static SharedPreferences.Editor getEditor() {
		return getSharedPreferences().edit();
	}

	/**
	 * Saves user data to the shared settings.
	 */
	public static void setAuth(String username, String password, boolean autologin) {
		SharedPreferences.Editor editor = getEditor();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.putBoolean("autologin", autologin);
		editor.commit();
	}

	/**
	 * Saves user data to the shared settings. Enables autologin.
	 */
	public static void setAuth(String username, String password) {
		setAuth(username, password, true);
	}

	/**
	 * Removes all saved settings from the shared settings editor.
	 */
	public static void clearAuth() {
		SharedPreferences.Editor editor = getEditor();
		editor.clear();
		editor.commit();
	}

	/**
	 * Returns the username setting. May be empty if not set.
	 * @return The username
	 * @throws RuntimeException Thrown if username could not be retrieved
	 */
	public static String getUsername() {
		SharedPreferences authStore = getSharedPreferences();
		return authStore.getString("username", "");
	}

	/**
	 * Returns the password setting. May be empty if not set.
	 * @return The password
	 * @throws RuntimeException Thrown if password could not be retrieved
	 */
	public static String getPassword() {
		SharedPreferences authStore = getSharedPreferences();
		return authStore.getString("password", "");
	}

	/**
	 * Returns the autologin setting.
	 * @return Whether or not autologin is enabled.
	 */
	public static Boolean getAutologin() {
		SharedPreferences authStore = getSharedPreferences();
		return authStore.getBoolean("autologin", false);
	}
}
