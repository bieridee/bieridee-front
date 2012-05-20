package ch.hsr.bieridee.android.exceptions;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ch.hsr.bieridee.android.BierideeApplication;
import ch.hsr.bieridee.android.activities.HomeScreenActivity;

/**
 * A catch all exception handler.
 *
 */
public class CatchAllExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler previous;

	/**
	 * Creates the CatchAllHandler.
	 * 
	 * @param previous
	 *            The previous UncaughtExceptionHandler
	 */
	public CatchAllExceptionHandler(UncaughtExceptionHandler previous) {
		this.previous = previous;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	// SUPPRESS CHECKSTYLE: no javadoc needed
	public void uncaughtException(Thread thread, Throwable ex) {
		final BierIdeeException bierIdeeException = checkForBierIdeeExceptions(ex);

		if (bierIdeeException != null) {
			final Context appContext = BierideeApplication.getAppContext();
			final Intent intent = new Intent(appContext, HomeScreenActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("errormessage", bierIdeeException.getMessage());
			appContext.startActivity(intent);
		} else {
			this.previous.uncaughtException(thread, ex);
		}

	}

	private BierIdeeException checkForBierIdeeExceptions(Throwable ex) {
		if (ex instanceof BierIdeeException) {
			return (BierIdeeException) ex;
		}
		while (ex.getCause() != null) {
			ex = ex.getCause();
			if (ex instanceof BierIdeeException) {
				return (BierIdeeException) ex;
			}
		}
		return null;
	}

}
