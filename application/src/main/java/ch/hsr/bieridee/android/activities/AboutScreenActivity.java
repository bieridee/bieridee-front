package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.os.Bundle;
import ch.hsr.bieridee.android.R;

/**
 * Activity that shows the "about" information.
 */
public class AboutScreenActivity extends Activity {

	private static final String LOG_TAG = AboutScreenActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.aboutscreen);
	}

}