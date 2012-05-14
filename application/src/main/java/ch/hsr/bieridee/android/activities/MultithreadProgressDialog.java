package ch.hsr.bieridee.android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * Multithread aware version of the android progress dialog. Allows to be called from multiple threads and maintain a
 * controlled state.
 * 
 */
public class MultithreadProgressDialog {

	private int callCounter;
	private ProgressDialog instance;

	/**
	 * Creates the dialog if it is not already displayed.
	 * 
	 * @param context
	 *            Context to pass over
	 * @param message
	 *            The message
	 * @param title
	 *            The title
	 * @param indeterminate
	 *            Is it indeterminate
	 */
	public synchronized void display(Context context, CharSequence message, CharSequence title, boolean indeterminate) {
		Log.d(this.getClass().getName(), "display(), count: " + this.callCounter);
		if (this.callCounter == 0) {
			this.instance = ProgressDialog.show(context, title, message, indeterminate);
		}
		++this.callCounter;
	}
	
	/**
	 * Hides the dialog when all callers are done.
	 */
	public synchronized void hide() {
		Log.d(this.getClass().getName(), "hide(), count: " + this.callCounter);
		--this.callCounter;
		if (this.callCounter == 0) {
			this.instance.dismiss();
		}
	}

}
