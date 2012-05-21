package ch.hsr.bieridee.android.activities;

import android.app.ExpandableListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.BeertypeListAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Activity that shows a list of all beertype in our database.
 */
public class BeertypeListActivity extends ExpandableListActivity {

	private static final String LOG_TAG = BeertypeListActivity.class.getName();
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
		new GetBeertypeData().execute();
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
			final HttpResponse response = BeertypeListActivity.this.httpHelper.get(Res.getURI(Res.BEERTYPE_COLLECTION));

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
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
