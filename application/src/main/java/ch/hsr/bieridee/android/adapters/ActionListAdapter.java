package ch.hsr.bieridee.android.adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.hsr.bieridee.android.R;

/**
 * The AcitonListAdapter adapts the JSON action structure to the Android ListView.
 * 
 * For further information, see the Adapter interface:
 * http://developer.android.com/reference/android/widget/Adapter.html
 */
public class ActionListAdapter extends BaseAdapter {

	private final Activity activity;
	private JSONArray jsonActivities;

	/**
	 * @param activity
	 *            Activity
	 */
	public ActionListAdapter(Activity activity) {
		this.activity = activity;
		this.jsonActivities = new JSONArray();
	}

	/**
	 * @param activity
	 *            Activity
	 * @param jsonActivities
	 *            JSONArray with list data
	 */
	public ActionListAdapter(Activity activity, JSONArray jsonActivities) {
		this.jsonActivities = jsonActivities;
		this.activity = activity;
	}

	/**
	 * Returns the number of list items.
	 * 
	 * @return Count of list items
	 */
	public int getCount() {
		return this.jsonActivities.length();
	}

	/**
	 * Return the list item at the specified position.
	 * 
	 * @param position
	 *            Position in list
	 * @return JSON action object at the specified position
	 */
	public Object getItem(int position) {
		return this.jsonActivities.optJSONObject(position);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		// Assign values to actionlist item
		final JSONObject jsonAction = (JSONObject) this.getItem(position);
		final LinearLayout wrapper = (LinearLayout) convertView;
		final TextView name = (TextView) wrapper.findViewById(R.id_timelinelist.itemName);
		final TextView time = (TextView) wrapper.findViewById(R.id_timelinelist.itemTime);
		final ImageView icon = (ImageView) wrapper.findViewById(R.id_timelinelist.icon);
		final TextView description = (TextView) wrapper.findViewById(R.id_timelinelist.itemDescription);
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
			e.printStackTrace(); // TODO Auto-generated catch block
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
		this.jsonActivities = jsonActions;
	}

	@Override
	public String toString() {
		return "ActionListAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
