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
import ch.hsr.bieridee.android.adapters.RecommendationsListAdapter;
import ch.hsr.bieridee.android.config.Auth;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;

/**
 * Activity that shows a list of beer recommendations.
 */
public class RecommendationsListActivity extends ListActivity {

	//private static final String LOG_TAG = RecommendationsListActivity.class.getName();
	private RecommendationsListAdapter adapter;
	private ProgressDialog progressDialog;
	private HttpHelper httpHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommendationslist);

		this.httpHelper = AuthJsonHttp.create();

		this.adapter = new RecommendationsListAdapter(this);
		setListAdapter(this.adapter);
		this.addOnClickListeners();
		this.registerForContextMenu(this.getListView());
	}

	@Override
	public void onStart() {
		super.onStart();
		new GetRecommendationsData().execute();
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
				new GetRecommendationsData().execute();
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


	/**
	 * Async task to get recommedations from server and update UI.
	 */
	private class GetRecommendationsData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			RecommendationsListActivity.this.progressDialog = ProgressDialog.show(RecommendationsListActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingData), true);
		}

		@Override
		protected JSONArray doInBackground(Void... voids) {
			final HttpResponse response = RecommendationsListActivity.this.httpHelper.get(Res.getURI(Res.USER_RECOMMENDATION_COLLECTION, Auth.getUsername()));

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
				RecommendationsListActivity.this.adapter.updateData(result);
				RecommendationsListActivity.this.adapter.notifyDataSetChanged();
			}
			RecommendationsListActivity.this.progressDialog.dismiss();
		}
	}
}
