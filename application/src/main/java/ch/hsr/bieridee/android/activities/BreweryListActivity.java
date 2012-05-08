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
import android.widget.AdapterView.OnItemClickListener;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.BreweryListAdapter;
import ch.hsr.bieridee.android.config.Res;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brewerylist);
		this.adapter = new BreweryListAdapter(this);
		setListAdapter(this.adapter);
		this.addOnClickListeners();
	}

	@Override
	public void onStart() {
		super.onStart();
		new GetBreweryData().execute();
	}

	/**
	 * Async task to get breweries from server and update UI.
	 */
	class GetBreweryData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			Log.d(LOG_TAG, "onPreExecute()");
			BreweryListActivity.this.progressDialog = ProgressDialog.show(
					BreweryListActivity.this,
					getString(R.string.pleaseWait),
					getString(R.string.loadingData),
					true);
		}
		@Override
		protected JSONArray doInBackground(Void... voids) {
			Log.d(LOG_TAG, "doInBackground()");

			final HttpGet get = new HttpGet(Res.getURI(Res.BREWERY_COLLECTION));
			final HttpClient client = new DefaultHttpClient();
			final ResponseHandler<String> responseHandler = new BasicResponseHandler();
			HttpResponse response = null;
			try {
				response = client.execute(get);
			} catch (IOException e) {
				e.printStackTrace();
			}

			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				final String responseText;
				try {
					responseText = responseHandler.handleResponse(response);
					return new JSONArray(responseText);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(JSONArray result) {
			Log.d(LOG_TAG, "onPostExecute()");
			adapter.updateData(result);
			adapter.notifyDataSetChanged();
			BreweryListActivity.this.progressDialog.dismiss();
		}
	}

	/**
	 * Add onClick listeners to UI elements.
	 */
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
}
