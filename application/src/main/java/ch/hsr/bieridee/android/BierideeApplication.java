package ch.hsr.bieridee.android;

import android.app.Application;
import android.content.Context;

/**
 * Application class to provide a context for all nonstatic and static classes.
 */
public class BierideeApplication extends Application {
	private static Context context;

	public void onCreate() {
		super.onCreate();
		BierideeApplication.context = getApplicationContext();
	}

	public static Context getAppContext() {
		return BierideeApplication.context;
	}
}