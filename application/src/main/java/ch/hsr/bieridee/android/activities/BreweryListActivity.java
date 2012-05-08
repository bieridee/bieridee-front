package ch.hsr.bieridee.android.activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.BreweryListAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.ClientResourceFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Uniform;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import java.io.IOException;

/**
 * Activity that shows a list of all breweries in our database.
 */
public final class BreweryListActivity extends ListActivity {

	private static final String LOG_TAG = BreweryListActivity.class.getName();
	private BreweryListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brewerylist);
		this.adapter = new BreweryListAdapter(this);
		setListAdapter(this.adapter);
		addOnClickListeners();
	}

	@Override
	public void onStart() {
		Log.d(LOG_TAG, "onStart");
		super.onStart();
		updateBreweryList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.refresh_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id_refreshmenu.refresh:
				this.updateBreweryList();
				break;
		}
		return true;
	}

	/**
	 * Updates brewery list data and redraws list view.
	 */
	private void updateBreweryList() {
		Log.d(LOG_TAG, "Updating brewery list");

		// Show waiting dialog
		final String dialogTitle = getString(R.string.pleaseWait);
		final String dialogMessage = getString(R.string.loadingData);
		final ProgressDialog dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);

		// Do and handle HTTP request
		final ClientResource cr = ClientResourceFactory.getClientResource(Res.getURI(Res.BREWERY_COLLECTION));
		cr.setOnResponse(new Uniform() {
			public void handle(Request request, Response response) {
				JSONArray breweries;

				// Update data
				try {
					final String json = response.getEntity().getText();
					breweries = new JSONArray(json);
					BreweryListActivity.this.adapter.updateData(breweries);
				} catch (IOException e) {
					e.printStackTrace(); // TODO
				} catch (JSONException e) {
					e.printStackTrace(); // TODO
				}

				// Update view
				runOnUiThread(new Runnable() {
					public void run() {
						BreweryListActivity.this.adapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				});
			}
		});
		cr.get(MediaType.APPLICATION_JSON); // Async call
		cr.release();
	}

	private void addOnClickListeners() {
		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("infos", "clicked pos: " + position + " with id: " + id);
				final Intent intent = new Intent(view.getContext(), BreweryDetailActivity.class);
				intent.putExtra(BreweryDetailActivity.EXTRA_BREWERY_ID, id);
				startActivity(intent);
			}
		});
	}
}
