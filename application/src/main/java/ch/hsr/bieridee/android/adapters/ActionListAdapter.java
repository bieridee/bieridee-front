package ch.hsr.bieridee.android.adapters;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.hsr.bieridee.android.BierideeApplication;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.utils.ErrorHelper;

/**
 * The ActionListAdapter adapts the JSON action structure to the Android ListView.
 * 
 */
public class ActionListAdapter extends BaseAdapter {
	
	private static final String LOG_TAG = ActionListAdapter.class.getName();
	private final Activity activity;
	private ArrayList<JSONObject> actions;

	/**
	 * @param activity Activity
	 */
	public ActionListAdapter(Activity activity) {
		this.activity = activity;
		this.actions = new ArrayList<JSONObject>();
	}

	/**
	 * @param activity
	 *            Activity
	 * @param jsonActivities
	 *            JSONArray with list data
	 */
	public ActionListAdapter(Activity activity, JSONArray jsonActivities) {
		this.activity = activity;
		this.actions.addAll(this.convertJSONArrayToList(jsonActivities));
	}

	/**
	 * Returns the number of list items.
	 * 
	 * @return Count of list items
	 */
	public int getCount() {
		return this.actions.size();
	}

	/**
	 * Return the list item at the specified position.
	 * 
	 * @param position
	 *            Position in listtext
	 * @return JSON action object at the specified position
	 */
	public Object getItem(int position) {
		return this.actions.get(position);
	}

	/**
	 * Return an unique ID of the specified item.
	 * 
	 * @param position
	 *            Position in list
	 * @return The id of the item at the specified position
	 */
	public long getItemId(int position) {
		final JSONObject jsonAction = (JSONObject) this.getItem(position);
		JSONObject beer;
		long id = 0;
		try {
			beer = jsonAction.getJSONObject("beer");
			id = beer.getLong("id");
		} catch (JSONException e) {
			Log.d(LOG_TAG, e.getMessage(), e);
			ErrorHelper.onError(BierideeApplication.getAppContext().getString(R.string.malformedData), this.activity);
		}
		return id;
	}

	/**
	 * Return the view for specified position.
	 * 
	 * @param position
	 *            Position in list
	 * @param convertView
	 *            The old view to reuse, if possible
	 * @param parent
	 *            The parent that this view will eventually be attached to
	 * @return A View corresponding to the data at the specified position.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get & inflate actionlist item from XML
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(R.layout.timelinelist_item, null);
		}

		final LinearLayout wrapper = (LinearLayout) convertView;
		final TextView name = (TextView) wrapper.findViewById(R.id_timelinelist.itemName);
		final TextView time = (TextView) wrapper.findViewById(R.id_timelinelist.itemTime);
		final ImageView icon = (ImageView) wrapper.findViewById(R.id_timelinelist.icon);
		final TextView description = (TextView) wrapper.findViewById(R.id_timelinelist.itemDescription);

		if (position <= this.actions.size()) {
			// Assign values to actionlist item
			final JSONObject jsonAction = (JSONObject) this.getItem(position);
			try {
				final JSONObject beer = jsonAction.getJSONObject("beer");
				final JSONObject user = jsonAction.getJSONObject("user");
				final long secondsAgo = Long.parseLong(jsonAction.getString("secondsAgo"));

				final String title = user.getString("user");
				String detailText;
				detailText = createDetailText(jsonAction, user, beer);
				time.setText(getTimeString(secondsAgo));

				if ("consumption".equals(jsonAction.getString("type"))) {
					icon.setImageResource(R.drawable.ic_consumation2);
				} else {
					icon.setImageResource(R.drawable.ic_rating);
				}

				name.setText(title);
				description.setText(detailText);
			} catch (JSONException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				ErrorHelper.onError(BierideeApplication.getAppContext().getString(R.string.malformedData), this.activity);
			}
		}

		return convertView;
	}

	private String getTimeString(long secondsAgo) {
		final Resources res = this.activity.getResources();
		final long seconds = secondsAgo;
		final long minutes = seconds / 60;
		final long hours = minutes / 60;
		final long days = hours / 24;
		if (seconds < 60) {
			return seconds + " " + res.getQuantityString(R.plurals.numberOfSeconds, (int) seconds);
		} else if (minutes < 60) {
			return minutes + " " + res.getQuantityString(R.plurals.numberOfMinutes, (int) minutes);
		} else if (hours < 24) {
			return hours + " " + res.getQuantityString(R.plurals.numberOfHours, (int) hours);
		} else if (days < 100) {
			return days + " " + res.getQuantityString(R.plurals.numberOfDays, (int) days);
		} else {
			return this.activity.getString(R.string.timelinelist_longAgo);
		}
	}

	private String createDetailText(JSONObject jsonAction, JSONObject user, JSONObject beer) throws JSONException {
		final String beerName = beer.getString("name");
		final String username = user.getString("user");

		if ("consumption".equals(jsonAction.getString("type"))) {
			return username + " " + this.activity.getString(R.string.timelinelist_feltThirty) + " " + beerName;
		} else {
			final int ratingValue = jsonAction.getInt("rating");
			return this.activity.getString(R.string.timelinelist_ratedBeer, username, beerName, ratingValue);
		}
	}

	/**
	 * Update the internal JSONArray with new data.
	 * 
	 * @param jsonActions
	 *            New data to replace the old JSONArray
	 */
	public void updateData(JSONArray jsonActions) {
		this.actions.addAll(convertJSONArrayToList(jsonActions));
	}
	
	private ArrayList<JSONObject> convertJSONArrayToList(JSONArray jsonArray) {
		final ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		for(int i = 0; i < jsonArray.length(); ++i) {
			list.add(jsonArray.optJSONObject(i));
		}
		return list;
	}

	@Override
	public String toString() {
		return "ActionListAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
