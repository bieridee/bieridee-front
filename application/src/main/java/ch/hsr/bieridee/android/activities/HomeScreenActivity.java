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
 * Activity that shows a list of all beers in our database.
 */
public class HomeScreenActivity extends Activity {

	private static final String LOG_TAG = HomeScreenActivity.class.getName();

	/**
	 * Called when the activity is first created.
	 *
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the
	 *                           data it most recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.homescreen);
		this.addOnClickListener((Button) findViewById(R.id_dashboardscreen.buttonBeerlist), BeerListActivity.class);
	}

	private void addOnClickListener(Button button, final Class<? extends Activity> activityClass) {
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			final Intent intent = new Intent(v.getContext(), activityClass);
			startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			/*
				case R.id.icon:
					Toast.makeText(this, "You pressed the icon!", Toast.LENGTH_LONG).show();
					break;
				case R.id.text:
					Toast.makeText(this, "You pressed the text!", Toast.LENGTH_LONG).show();
					break;
				case R.id.icontext:
					Toast.makeText(this, "You pressed the icon and text!", Toast.LENGTH_LONG).show();
					break;
				*/
		}
		return true;
	}
}