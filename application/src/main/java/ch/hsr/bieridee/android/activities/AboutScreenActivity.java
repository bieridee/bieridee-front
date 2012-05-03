package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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