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
import ch.hsr.bieridee.android.adapters.BeerListAdapter;
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
 * Activity that shows a list of all beers in our database.
 */
public class BeerListActivity extends ListActivity {

	private static final String LOG_TAG = BeerListActivity.class.getName();
	private BeerListAdapter adapter;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerlist);
		this.adapter = new BeerListAdapter(this);
		setListAdapter(this.adapter);
		this.addOnClickListeners();
	}

	@Override
	public void onStart() {
		super.onStart();
		new GetBeerData().execute();
	}

	/**
	 * Async task to get beers from server and update UI.
	 */
	class GetBeerData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			Log.d(LOG_TAG, "onPreExecute()");
			BeerListActivity.this.progressDialog = ProgressDialog.show(
					BeerListActivity.this,
					getString(R.string.pleaseWait),
					getString(R.string.loadingData),
					true);
		}
		@Override
		protected JSONArray doInBackground(Void... voids) {
			Log.d(LOG_TAG, "doInBackground()");

			final HttpGet get = new HttpGet(Res.getURI(Res.BEER_COLLECTION));
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
			BeerListActivity.this.progressDialog.dismiss();
		}
	}

	/**
	 * Add onClick listeners to UI elements.
	 */
	private void addOnClickListeners() {
		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("infos", "clicked pos: " + position + " with id: " + id);
				final Intent intent = new Intent(view.getContext(), BeerDetailActivity.class);
				intent.putExtra(BeerDetailActivity.EXTRA_BEER_ID, id);
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
				new GetBeerData().execute();
				break;
		}
		return true;
	}
}
