package ch.hsr.bieridee.android.activities;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.BreweryListAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;
import ch.hsr.bieridee.android.utils.ErrorHelper;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Activity that shows a list of all breweries in our database.
 */
public final class BreweryListActivity extends ListActivity {

	private static final String LOG_TAG = BreweryListActivity.class.getName();
	private BreweryListAdapter adapter;
	private ProgressDialog progressDialog;
	private HttpHelper httpHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brewerylist);

		this.httpHelper = AuthJsonHttp.create();

		this.adapter = new BreweryListAdapter(this);
		setListAdapter(this.adapter);
		this.addOnClickListeners();
	}

	@Override
	public void onStart() {
		super.onStart();
		new GetBreweryData().execute();
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
				new GetBreweryData().execute();
				break;
		}
		return true;
	}

	/**
	 * Async task to get breweries from server and update UI.
	 */
	private class GetBreweryData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			Log.d(LOG_TAG, "onPreExecute()");
			BreweryListActivity.this.progressDialog = ProgressDialog.show(BreweryListActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingData), true);
		}

		@Override
		protected JSONArray doInBackground(Void... voids) {
			Log.d(LOG_TAG, "doInBackground()");

			HttpResponse response = null;
			try {
				response = BreweryListActivity.this.httpHelper.get(Res.getURI(Res.BREWERY_COLLECTION));
			} catch (IOException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				ErrorHelper.onError(BreweryListActivity.this.getString(R.string.connectionError), BreweryListActivity.this);
			}

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						ErrorHelper.onError(BreweryListActivity.this.getString(R.string.malformedData), BreweryListActivity.this);
					} catch (JSONException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						ErrorHelper.onError(BreweryListActivity.this.getString(R.string.malformedData), BreweryListActivity.this);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Log.d(LOG_TAG, "onPostExecute()");
			if (result != null) {
				BreweryListActivity.this.adapter.updateData(result);
				BreweryListActivity.this.adapter.notifyDataSetChanged();
			}
			BreweryListActivity.this.progressDialog.dismiss();
		}
	}

	/**
	 * Add onClick listeners to UI elements.
	 */
	private void addOnClickListeners() {
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final Intent intent = new Intent(view.getContext(), BreweryDetailActivity.class);
				intent.putExtra(BreweryDetailActivity.EXTRA_BREWERY_ID, id);
				startActivity(intent);
			}
		});
	}
}
