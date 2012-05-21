package ch.hsr.bieridee.android;

import android.app.Application;
import android.content.Context;

/**
 * Application class to provide a context for all nonstatic and static classes.
 */
public class BierideeApplication extends Application {
	private static Context CONTEXT;
	public final static String VERSION = "0.1.0";

	/**
	 * Constructor
	 * {@inheritDoc}
	 */
	public void onCreate() {
		super.onCreate();
		BierideeApplication.CONTEXT = getApplicationContext();
	}

	/**
	 * Return the Application Context
	 * @return Application Context object
	 */
	public static Context getAppContext() {
		return BierideeApplication.CONTEXT;
	}
}
