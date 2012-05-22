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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.ActionListAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;

/**
 * Activity that shows a list of all actions in our database.
 */
public class TimelineListActivity extends ListActivity implements ListView.OnScrollListener {

	private ActionListAdapter adapter;
	private ProgressDialog progressDialog;
	public static final String EXTRA_USERNAME = "username";
	private String username;

	public static final long THRESHOLD = 30000;
	private long updateTimestamp = 0;

	private int currentPage = 0;
	private static final int VISIBLECOUNT = 7;
	private static final int PAGESIZE = 12;

	private View loadingFooter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timelinelist);
		final ListView list = (ListView) TimelineListActivity.this.getListView();
		this.loadingFooter = getLayoutInflater().inflate(R.layout.loading_item, list, false);
		list.addFooterView(this.loadingFooter, null, false);
		this.adapter = new ActionListAdapter(this);
		setListAdapter(this.adapter);
		this.addOnClickListeners();
		final Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			this.username = extras.getString(EXTRA_USERNAME);
			if (this.username != null) {
				this.setTitle(this.getString(R.string.timelinelist_title) + " " + this.username);
			}
		}
		getListView().setOnScrollListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (System.currentTimeMillis() - this.updateTimestamp > THRESHOLD) {
			new GetActionData().execute();
			this.updateTimestamp = System.currentTimeMillis();
		}
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
		private boolean showDialog = true;

		public GetActionData() {
			this.showDialog = true;
		}

		public GetActionData(boolean showDialog) {
			this.showDialog = showDialog;
		}

		@Override
		protected void onPreExecute() {
			TimelineListActivity.this.loadingFooter.setVisibility(View.VISIBLE);
			if (this.showDialog) {
				TimelineListActivity.this.progressDialog = ProgressDialog.show(TimelineListActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingData), true);
			}
		}

		@Override
		protected JSONArray doInBackground(Void... voids) {
			String resourceUri = Res.TIMELINE_COLLECTION + "?nOfItems=" + PAGESIZE + "&page=" + TimelineListActivity.this.currentPage;
			if (TimelineListActivity.this.username != null) {
				resourceUri += "?username=" + TimelineListActivity.this.username;
			}
			final HttpHelper httpHelper = AuthJsonHttp.create();
			final HttpResponse response = httpHelper.get(Res.getURI(resourceUri));

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
				TimelineListActivity.this.adapter.updateData(result);
				TimelineListActivity.this.adapter.notifyDataSetChanged();
			}
			if (this.showDialog) {
				TimelineListActivity.this.progressDialog.dismiss();
			}
			TimelineListActivity.this.loadingFooter.setVisibility(View.GONE);
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

	// SUPPRESS CHECKSTYLE: Method not used but needed by interface
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// nuffin
	}

	/**
	 * Triggers on scroll state changed.
	 * 
	 * @param view
	 *            The list view
	 * @param scrollState
	 *            The scrollstate
	 * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
	 */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		final int last = view.getLastVisiblePosition();
		switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_IDLE:
				if (last >= (this.currentPage + 1) * VISIBLECOUNT) {
					new GetActionData(false).execute();
					this.currentPage++;
				}
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				if (last >= (this.currentPage + 1) * VISIBLECOUNT) {
					new GetActionData(false).execute();
					this.currentPage++;
				}
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				break;
		}
	}
}
