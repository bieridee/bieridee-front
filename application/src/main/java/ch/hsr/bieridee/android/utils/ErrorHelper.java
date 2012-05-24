package ch.hsr.bieridee.android.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Errorhanding helper.
 * 
 */
public final class ErrorHelper {

	private ErrorHelper() {
		// do not instantiate utility class
	}

	/**
	 * Generic procedure onError. Finish the current acivity and go back to the last one and show a toas with a custom
	 * message.
	 * 
	 * @param message The message to be shown
	 * @param activity The activity in which the error happened
	 */
	public static void onError(final String message, final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG).show();
			}
		});
		activity.finish();
	}

}
