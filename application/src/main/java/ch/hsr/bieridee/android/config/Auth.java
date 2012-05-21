package ch.hsr.bieridee.android.config;

import android.content.Context;
import android.content.SharedPreferences;
import ch.hsr.bieridee.android.BierideeApplication;

/**
 * Class to store authentication information.
 */
public final class Auth {

	private Auth() {
		// Do not instantiate
	}

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
	 * @param username Username string
	 * @param password Password string
	 */
	public static void setAuth(String username, String password) {
		final SharedPreferences.Editor editor = getEditor();
		editor.putString(USERNAME_KEY, username);
		editor.putString(PASSWORD_KEY, password);
		editor.commit();
	}

	/**
	 * Removes all saved settings from the shared settings editor.
	 */
	public static void clearAuth() {
		final SharedPreferences.Editor editor = getEditor();
		editor.clear();
		editor.commit();
	}

	/**
	 * Returns the username setting. May be empty if not set.
	 * @return The username
	 */
	public static String getUsername() {
		final SharedPreferences authStore = getSharedPreferences();
		return authStore.getString(USERNAME_KEY, "");
	}

	/**
	 * Returns the password setting. May be empty if not set.
	 * @return The password
	 */
	public static String getPassword() {
		final SharedPreferences authStore = getSharedPreferences();
		return authStore.getString(PASSWORD_KEY, "");
	}

	/**
	 * Returns whether or not username and password are available.
	 * @return Whether or not username and password are available
	 */
	public static boolean dataAvailable() {
		final SharedPreferences authStore = getSharedPreferences();
		final boolean keysAvailable = authStore.contains(USERNAME_KEY) && authStore.contains(PASSWORD_KEY);
		final boolean nonEmptyValues = !(getUsername().isEmpty() || getPassword().isEmpty());
		return keysAvailable && nonEmptyValues;
	}
}
