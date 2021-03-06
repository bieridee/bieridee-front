package ch.hsr.bieridee.android.activities;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ch.hsr.bieridee.android.config.Auth;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.ActionListAdapter;
import ch.hsr.bieridee.android.config.Conf;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;
import ch.hsr.bieridee.android.utils.ErrorHelper;

/**
 * Activity that shows a list of all actions in our database.
 */
public class TimelineListActivity extends ListActivity implements ListView.OnScrollListener {

	private static final String LOG_TAG = TimelineListActivity.class.getName();
	private ActionListAdapter adapter;
	private ProgressDialog progressDialog;
	public static final String EXTRA_USERNAME = "username";
	private String username;
	
	private TextView noActions;
	private Button gotoBeerlistButton;

	private long updateTimestamp = 0;
	private static final int SIMILARITY_TIMEDIFF = 15;

	private int currentPage = 0;
	private static final int VISIBLECOUNT = 7;
	private static final int PAGESIZE = 12;

	private View loadingFooter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timelinelist);
		final ListView list = TimelineListActivity.this.getListView();
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
		this.noActions = (TextView) findViewById(R.id_timelinelist.info);
		this.gotoBeerlistButton = (Button) findViewById(R.id_timelinelist.beerlistbutton);
		this.gotoBeerlistButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent(v.getContext(), BeerListActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		if (this.isUpdateNecessary()) {
			new GetActionData().execute();
			this.updateTimestamp = System.currentTimeMillis();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		if (this.username != null) {
			inflater.inflate(R.menu.refresh_menu, menu);
		} else {
			inflater.inflate(R.menu.timeline_menu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id_refreshmenu.refresh:
				new GetActionData().execute();
				this.currentPage++;
				break;
			case R.id_timeline_menu.profile:
				final Intent intent = new Intent(getBaseContext(), TimelineListActivity.class);
				intent.putExtra(EXTRA_USERNAME, Auth.getUsername());
				startActivity(intent);
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
			final Map<String, String> params = new HashMap<String, String>();
			params.put("items", PAGESIZE + "");
			params.put("page", TimelineListActivity.this.currentPage + "");
			if (TimelineListActivity.this.username != null) {
				params.put("username", TimelineListActivity.this.username);
			}
			final String resourceUri = Res.getURIwithGETParams(Res.TIMELINE_COLLECTION, params);
			final HttpHelper httpHelper = AuthJsonHttp.create();
			HttpResponse response = null;
			try {
				response = httpHelper.get(resourceUri);
			} catch (IOException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				ErrorHelper.onError(TimelineListActivity.this.getString(R.string.connectionError), TimelineListActivity.this);
			}

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						ErrorHelper.onError(TimelineListActivity.this.getString(R.string.malformedData), TimelineListActivity.this);
					} catch (JSONException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						ErrorHelper.onError(TimelineListActivity.this.getString(R.string.malformedData), TimelineListActivity.this);
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			if (result != null) {
				try {
					result = TimelineListActivity.this.collapseTimeline(result);
				} catch (JSONException e) {
					Log.d(LOG_TAG, e.getMessage(), e);
					ErrorHelper.onError(TimelineListActivity.this.getString(R.string.malformedData), TimelineListActivity.this);
				}
				TimelineListActivity.this.adapter.updateData(result);
				TimelineListActivity.this.adapter.notifyDataSetChanged();
				
				if(! TimelineListActivity.this.adapter.isEmpty()) {
					TimelineListActivity.this.noActions.setVisibility(View.GONE);
					TimelineListActivity.this.gotoBeerlistButton.setVisibility(View.GONE);
				} else {
					TimelineListActivity.this.noActions.setVisibility(View.VISIBLE);
					TimelineListActivity.this.gotoBeerlistButton.setVisibility(View.VISIBLE);
				}
			}
			if (this.showDialog) {
				TimelineListActivity.this.progressDialog.dismiss();
			}
			TimelineListActivity.this.loadingFooter.setVisibility(View.GONE);
			TimelineListActivity.this.currentPage++;
		}
	}

	private JSONArray collapseTimeline(JSONArray timeline) throws JSONException {
		int index = 0;
		final JSONArray collapsedTimeline = new JSONArray();

		// check complete timeline
		while (index < timeline.length()) {
			final JSONObject action = timeline.getJSONObject(index);
			collapsedTimeline.put(action);
			// only collapse ratings
			if ("rating".equals(action.getString("type"))) {
				// take next action to compare
				int counter = 1;
				JSONObject nextAction = timeline.optJSONObject(index + counter);
				// compare with next until end of timeline or non-similarity
				while (nextAction != null && isSimilar(action, nextAction)) {
					++counter;
					nextAction = timeline.optJSONObject(index + counter);
				}
				index += counter;
			} else {
				++index;
			}
		}
		return collapsedTimeline;
	}

	private boolean isSimilar(JSONObject current, JSONObject next) throws JSONException {
		// not similar if different type
		if (!current.getString("type").equals(next.getString("type"))) {
			return false;
		}
		// not similar if different beer
		if (current.getJSONObject("beer").getLong("id") != next.getJSONObject("beer").getLong("id")) {
			return false;
		}
		// not similar if different user
		if (!current.getJSONObject("user").getString("user").equals(next.getJSONObject("user").getString("user"))) {
			return false;
		}
		// not similar if time difference is greater than n seconds
		// SUPPRESS CHECKSTYLE: dividing by 1000 (magic number), miliseconds -> seconds
		final long timediff = current.getLong("timestamp") / 1000 - next.getLong("timestamp") / 1000;
		if (timediff >= SIMILARITY_TIMEDIFF) {
			return false;
		}
		// otherwise actions are similar
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

	// SUPPRESS CHECKSTYLE: Method not used but needed by interface
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// nuffin (muffin|bluffing)
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
				if (last >= (this.currentPage) * VISIBLECOUNT) {
					new GetActionData(false).execute();
				}
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				if (last >= (this.currentPage) * VISIBLECOUNT) {
					new GetActionData(false).execute();
				}
				break;
			case OnScrollListener.SCROLL_STATE_FLING:
				break;
		}
	}
	
	private boolean isUpdateNecessary() {
		return System.currentTimeMillis() - this.updateTimestamp > Conf.UPDATE_THRESHOLD;
	}
}
