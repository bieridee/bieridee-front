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
	private String message;
	private String title;

	/**
	 * Creates a new Dialog.
	 * 
	 * @param message
	 *            The message of the dialog
	 * @param title
	 *            The title of the dialog
	 */
	public MultithreadProgressDialog(String title, String message) {
		this.title = title;
		this.message = message;
	}

	/**
	 * Creates the dialog if it is not already displayed.
	 * 
	 * @param context
	 *            Context to pass over
	 * @param indeterminate
	 *            Is it indeterminate
	 */
	public synchronized void display(Context context, boolean indeterminate) {
		Log.d(this.getClass().getName(), "display(), count: " + this.callCounter);
		if (this.callCounter == 0) {
			this.instance = ProgressDialog.show(context, this.title, this.message, indeterminate);
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
