package ch.hsr.bieridee.android.activities;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ExpandableListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.BeertypeListAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;
import ch.hsr.bieridee.android.utils.ErrorHelper;

/**
 * Activity that shows a list of all beertype in our database.
 */
public class BeertypeListActivity extends ExpandableListActivity {

	private static final String LOG_TAG = BeertypeListActivity.class.getName();
	private static final long UPDATE_THRESHOLD = 30000;
	private long updateTimestamp = 0;
	
	private BeertypeListAdapter adapter;
	private MultithreadProgressDialog progressDialog;
	private HttpHelper httpHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beertypelist);

		this.httpHelper = AuthJsonHttp.create();

		this.progressDialog = new MultithreadProgressDialog(getString(R.string.pleaseWait), getString(R.string.loadingData));

		this.adapter = new BeertypeListAdapter(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (System.currentTimeMillis() - this.updateTimestamp > UPDATE_THRESHOLD) {
			new GetBeertypeData().execute();
			this.updateTimestamp = System.currentTimeMillis();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.beertypelist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id_beertypelist_menu.refresh:
				new GetBeertypeData().execute();
				break;
			case R.id_beertypelist_menu.expandall:
				for (int i = 0; i < getExpandableListAdapter().getGroupCount(); i++) {
					getExpandableListView().expandGroup(i);
				}
				break;
		}
		return true;
	}

	/**
	 * Async task to get beers from server and update UI.
	 */
	private class GetBeertypeData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			BeertypeListActivity.this.progressDialog.display(BeertypeListActivity.this, true);
		}

		@Override
		protected JSONArray doInBackground(Void... voids) {
			HttpResponse response = null;
			try {
				response = BeertypeListActivity.this.httpHelper.get(Res.getURI(Res.BEERTYPE_COLLECTION));
			} catch (IOException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				ErrorHelper.onError(BeertypeListActivity.this.getString(R.string.connectionError), BeertypeListActivity.this);
			}

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						ErrorHelper.onError(BeertypeListActivity.this.getString(R.string.malformedData), BeertypeListActivity.this);
					} catch (JSONException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						ErrorHelper.onError(BeertypeListActivity.this.getString(R.string.malformedData), BeertypeListActivity.this);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			if (result != null) {
				BeertypeListActivity.this.adapter.updateData(result);
				BeertypeListActivity.this.adapter.notifyDataSetChanged();
				BeertypeListActivity.this.setListAdapter(BeertypeListActivity.this.adapter);
			}
			BeertypeListActivity.this.progressDialog.hide();
		}
	}
}
