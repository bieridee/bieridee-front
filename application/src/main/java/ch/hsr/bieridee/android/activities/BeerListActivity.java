package ch.hsr.bieridee.android.activities;

import java.io.IOException;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Uniform;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import ch.hsr.bieridee.android.adapters.BeerListAdapter;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.ClientResourceFactory;

/**
 * Activity that shows a list of all beers in our database.
 */
public class BeerListActivity extends ListActivity {

	private static final String LOG_TAG = BeerListActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerlist);
		setListAdapter(new BeerListAdapter(this));
	}

	@Override
	public void onStart() {
		super.onStart();
		updateBeerList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.refresh_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id_refreshmenu.refresh:
				this.updateBeerList();
				break;
		}
		return true;
	}

	/**
	 * Updates beer list data and redraws list view.
	 */
	private void updateBeerList() {
		Log.d(LOG_TAG, "Updating beer list");

		final BeerListAdapter adapter = (BeerListAdapter) getListAdapter();
		this.getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("infos", "clicked pos: " + position + " with id: " + id);
				final Intent intent = new Intent(view.getContext(), BeerDetailActivity.class);
				intent.putExtra(BeerDetailActivity.EXTRA_BEER_ID, id);
				startActivity(intent);
			}
		});

		// Show waiting dialog
		final String dialogTitle = getString(R.string.pleaseWait);
		final String dialogMessage = getString(R.string.loadingData);
		final ProgressDialog dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);

		// Do HTTP request
		final ClientResource cr = ClientResourceFactory.getClientResource(Res.getURI(Res.BEER_COLLECTION));
		cr.setOnResponse(new Uniform() {
			public void handle(Request request, Response response) {
				JSONArray beers;

				// Update data
				try {
					final String json = response.getEntity().getText();
					beers = new JSONArray(json);
					adapter.updateData(beers);
				} catch (IOException e) {
					e.printStackTrace(); // TODO
				} catch (JSONException e) {
					e.printStackTrace(); // TODO
				}

				// Update view
				runOnUiThread(new Runnable() {
					public void run() {
						adapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				});
			}
		});
		cr.get(MediaType.APPLICATION_JSON); // Async call
		cr.release();
	}
}
