package ch.hsr.bieridee.android.config;

import android.content.Context;
import android.content.SharedPreferences;
import ch.hsr.bieridee.android.BierideeApplication;

/**
 * Class to store authentication information.
 */
public final class Auth {

	private static final String PREFS_NAME = "auth";
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";

	/**
	 * Returns shared preferences.
	 * @return SharedPreferences instance
	 */
	private static SharedPreferences getSharedPreferences() {
		return BierideeApplication.getAppContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
	public static void setAuth(String username, String password) {
		SharedPreferences.Editor editor = getEditor();
		editor.putString(USERNAME_KEY, username);
		editor.putString(PASSWORD_KEY, password);
		editor.commit();
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
		return authStore.getString(USERNAME_KEY, "");
	}

	/**
	 * Returns the password setting. May be empty if not set.
	 * @return The password
	 * @throws RuntimeException Thrown if password could not be retrieved
	 */
	public static String getPassword() {
		SharedPreferences authStore = getSharedPreferences();
		return authStore.getString(PASSWORD_KEY, "");
	}

	/**
	 * Returns whether or not username and password are available.
	 * @return
	 */
	public static boolean dataAvailable() {
		SharedPreferences authStore = getSharedPreferences();
		boolean keysAvailable = authStore.contains(USERNAME_KEY) && authStore.contains(PASSWORD_KEY);

		boolean nonEmptyValues = !(getUsername().isEmpty() || getPassword().isEmpty());
		return keysAvailable && nonEmptyValues;
	}
}
