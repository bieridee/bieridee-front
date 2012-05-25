package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.homescreen);
		this.addOnClickListener((Button) findViewById(R.id_dashboardscreen.buttonBeerlist), BeerListActivity.class);
		this.addOnClickListener((Button) findViewById(R.id_dashboardscreen.buttonBreweries), BreweryListActivity.class);
		this.addOnClickListener((Button) findViewById(R.id_dashboardscreen.buttonCreate), BeerCreateActivity.class);
		this.addOnClickListener((Button) findViewById(R.id_dashboardscreen.buttonTimeline), TimelineListActivity.class);

		final OnClickListener notYetImplementedListener = new OnClickListener() {
			public void onClick(View view) {
				Toast.makeText(HomeScreenActivity.this.getBaseContext(), "TODO implement!", Toast.LENGTH_LONG).show();
			}
		};

		findViewById(R.id_dashboardscreen.buttonProfile).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				final Intent intent = new Intent(v.getContext(), TimelineListActivity.class);
				intent.putExtra(TimelineListActivity.EXTRA_USERNAME, Auth.getUsername());
				startActivity(intent);

			}
		});

		findViewById(R.id_dashboardscreen.buttonRating).setOnClickListener(notYetImplementedListener);
		
		if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("errormessage")) {
			final String message = this.getIntent().getExtras().getString("errormessage");
			final AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this);
			builder.setMessage(message)
			       .setCancelable(false)
			       .setPositiveButton(HomeScreenActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   dialog.cancel();
			           }
			       }) ;
			final AlertDialog info = builder.create();
			info.show();
		}

		/*findViewById(R.id_dashboardscreen.buttonRating).setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				final Intent intent = new Intent(view.getContext(), BarcodeScanActivity.class);
				startActivity(intent);
			}
		});*/
	}

	@Override
	public void onResume() {
		super.onResume();
		// Only show screen if login information have been set
		if (!Auth.dataAvailable()) {
			final Intent intent = new Intent(this.getBaseContext(), LoginScreenActivity.class);
			startActivityForResult(intent, LoginScreenActivity.REQUEST_CODE_LOGIN);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == LoginScreenActivity.RESULT_CODE_EXIT) {
			finish();
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
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final Intent intent;
		switch (item.getItemId()) {
			case R.id_mainmenu.about:
				intent = new Intent(this.getBaseContext(), AboutScreenActivity.class);
				startActivity(intent);
				break;
			case R.id_mainmenu.logout:
				Auth.clearAuth();
				Toast.makeText(this.getApplicationContext(), getString(R.string.dashboardscreen_loggedOut), Toast.LENGTH_SHORT).show();
				intent = new Intent(this.getBaseContext(), LoginScreenActivity.class);
				startActivity(intent);
				break;
		}
		return true;
	}
}