package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.config.Auth;

/**
 * Activity that shows a list of all beers in our database.
 */
public class HomeScreenActivity extends Activity {

	private static final String LOG_TAG = HomeScreenActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.homescreen);
		this.addOnClickListener((Button) findViewById(R.id_dashboardscreen.buttonBeerlist), BeerListActivity.class);

		OnClickListener notYetImplementedListener = new OnClickListener() {
			public void onClick(View view) {
				Toast.makeText(HomeScreenActivity.this.getBaseContext(), "TODO implement!", Toast.LENGTH_LONG).show();
			}
		};
		findViewById(R.id_dashboardscreen.buttonBreweries).setOnClickListener(notYetImplementedListener);
		findViewById(R.id_dashboardscreen.buttonConsumption).setOnClickListener(notYetImplementedListener);
		findViewById(R.id_dashboardscreen.buttonProfile).setOnClickListener(notYetImplementedListener);
		findViewById(R.id_dashboardscreen.buttonRating).setOnClickListener(notYetImplementedListener);
		findViewById(R.id_dashboardscreen.buttonTimeline).setOnClickListener(notYetImplementedListener);
	}

	@Override
	public void onStart() {
		super.onStart();
		// Only show screen if login information have been set
		if (!Auth.dataAvailable()) {
			final Intent intent = new Intent(this.getBaseContext(), LoginScreenActivity.class);
			startActivity(intent);
		}
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
			case R.id_mainmenu.about:
				final Intent intent = new Intent(this.getBaseContext(), AboutScreenActivity.class);
				startActivity(intent);
				break;
			case R.id_mainmenu.logout:
				// TODO
				Toast.makeText(this, "TODO logout!", Toast.LENGTH_LONG).show();
				break;
		}
		return true;
	}
}