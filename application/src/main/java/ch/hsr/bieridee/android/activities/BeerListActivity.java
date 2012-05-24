package ch.hsr.bieridee.android.activities;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.BeerListAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;
import ch.hsr.bieridee.android.utils.ErrorHelper;

/**
 * Activity that shows a list of all beers in our database.
 */
public class BeerListActivity extends ListActivity {

	private BeerListAdapter adapter;
	private ProgressDialog progressDialog;
	private HttpHelper httpHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerlist);

		this.httpHelper = AuthJsonHttp.create();

		this.adapter = new BeerListAdapter(this);
		setListAdapter(this.adapter);
		this.addOnClickListeners();
	}

	@Override
	public void onStart() {
		super.onStart();
		new GetBeerData().execute();
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
				new GetBeerData().execute();
				break;
		}
		return true;
	}

	/**
	 * Async task to get beers from server and update UI.
	 */
	private class GetBeerData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			BeerListActivity.this.progressDialog = ProgressDialog.show(BeerListActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingData), true);
		}

		@Override
		protected JSONArray doInBackground(Void... voids) {
			HttpResponse response = null;
			try {
				response = BeerListActivity.this.httpHelper.get(Res.getURI(Res.BEER_COLLECTION));
			} catch (IOException e) {
				ErrorHelper.onError(getString(R.string.connectionError), BeerListActivity.this);
			}

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						ErrorHelper.onError(getString(R.string.connectionError), BeerListActivity.this);
					} catch (JSONException e) {
						ErrorHelper.onError(getString(R.string.connectionError), BeerListActivity.this);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			if (result != null) {
				BeerListActivity.this.adapter.updateData(result);
				BeerListActivity.this.adapter.notifyDataSetChanged();
			}
			BeerListActivity.this.progressDialog.dismiss();
		}
	}

	/**
	 * Add onClick listeners to UI elements.
	 */
	private void addOnClickListeners() {
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Intent intent = new Intent(view.getContext(), BeerDetailActivity.class);
				intent.putExtra(BeerDetailActivity.EXTRA_BEER_ID, id);
				startActivity(intent);
			}
		});
	}
}
