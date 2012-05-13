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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.ActionListAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.HttpHelper;

/**
 * Activity that shows a list of all actions in our database.
 */
public class TimelineListActivity extends ListActivity {

	private static final String LOG_TAG = TimelineListActivity.class.getName();
	private ActionListAdapter adapter;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timelinelist);
		this.adapter = new ActionListAdapter(this);
		setListAdapter(this.adapter);
		this.addOnClickListeners();
	}

	@Override
	public void onStart() {
		super.onStart();
		new GetActionData().execute();
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
				new GetActionData().execute();
				break;
		}
		return true;
	}

	/**
	 * Async task to get timeline (actions) from server and update UI.
	 */
	private class GetActionData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			Log.d(LOG_TAG, "onPreExecute()");
			TimelineListActivity.this.progressDialog = ProgressDialog.show(TimelineListActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingData), true);
		}

		@Override
		protected JSONArray doInBackground(Void... voids) {
			Log.d(LOG_TAG, "doInBackground()");

			HttpHelper httpHelper = new HttpHelper();
			HttpResponse response = httpHelper.get(Res.getURI(Res.TIMELINE_COLLECTION));

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
			Log.d(LOG_TAG, "onPostExecute()");
			if (result != null) {
				adapter.updateData(result);
				adapter.notifyDataSetChanged();
			} // TODO handle else
			TimelineListActivity.this.progressDialog.dismiss();
		}
	}

	/**
	 * Add onClick listeners to UI elements.
	 */
	private void addOnClickListeners() {
		this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(LOG_TAG, "open detail");
//				 final Intent intent = new Intent(view.getContext(), BeerDetailActivity.class);
//				 intent.putExtra(BeerDetailActivity.EXTRA_BEER_ID, id);
//				 startActivity(intent);
			}
		});
	}
}
