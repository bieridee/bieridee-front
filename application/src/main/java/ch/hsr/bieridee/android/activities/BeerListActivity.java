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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.BeerListAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;

/**
 * Activity that shows a list of all beers in our database.
 */
public class BeerListActivity extends ListActivity {

	private static final String LOG_TAG = BeerListActivity.class.getName();
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
		this.registerForContextMenu(this.getListView());
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Log.d("context", "contextmenu was called");
		menu.setHeaderTitle(BeerListActivity.this.getString(R.string.beerlistContextTitle));
		menu.add(0, v.getId(), 0, BeerListActivity.this.getString(R.string.beerlistContextDelete));
		menu.add(0, v.getId(), 0, BeerListActivity.this.getString(R.string.beerlistContextEdit));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if (item.getTitle() == BeerListActivity.this.getString(R.string.beerlistContextDelete)) {
			new DeleteBeer().execute(info.id);
		} else if (item.getTitle() == BeerListActivity.this.getString(R.string.beerlistContextEdit)) {
			// TODO call edit beer activity.
		} else {
			return false;
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
			final HttpResponse response = BeerListActivity.this.httpHelper.get(Res.getURI(Res.BEER_COLLECTION));

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
				BeerListActivity.this.adapter.updateData(result);
				BeerListActivity.this.adapter.notifyDataSetChanged();
			} // TODO handle else
			BeerListActivity.this.progressDialog.dismiss();
		}
	}

	/**
	 * Async task to delete beer from server and update UI.
	 */
	private class DeleteBeer extends AsyncTask<Long, Void, Void> {
		@Override
		protected void onPreExecute() {
			BeerListActivity.this.progressDialog = ProgressDialog.show(BeerListActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingData), true);
			Log.d(LOG_TAG, "Delete was called");
		}

		@Override
		protected Void doInBackground(Long... ids) {
			final String uri = Res.getURI(Res.BEER_DOCUMENT, ids[0].toString());
			final HttpResponse response = BeerListActivity.this.httpHelper.delete(uri);

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					BeerListActivity.this.adapter.remove(ids[0]);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			BeerListActivity.this.adapter.notifyDataSetChanged();
			BeerListActivity.this.progressDialog.dismiss();
		}
	}

}
