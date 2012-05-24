package ch.hsr.bieridee.android.activities;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Multithread aware version of the android progress dialog. Allows to be called from multiple threads and maintain a
 * controlled state.
 * 
 */
public class MultithreadProgressDialog {

	private int callCounter;
	private ProgressDialog instance;
	private final String message;
	private final String title;

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
		if (this.callCounter == 0) {
			this.instance = ProgressDialog.show(context, this.title, this.message, indeterminate);
		}
		++this.callCounter;
	}

	/**
	 * Hides the dialog when all callers are done.
	 */
	public synchronized void hide() {
		--this.callCounter;
		if (this.callCounter == 0) {
			if(this.instance.getContext() != null) {
				this.instance.dismiss();
			}
		}
	}

}
