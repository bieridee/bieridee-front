package ch.hsr.bieridee.android.adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.hsr.bieridee.android.BierideeApplication;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.utils.ErrorHelper;

/**
 * The ActionListAdapter adapts the JSON action structure to the Android ListView.
 * 
 */
public class ActionListAdapter extends ContinousScrollJsonAdapter {
	
	private static final String LOG_TAG = ActionListAdapter.class.getName();

	/**
	 * @param activity Activity
	 */
	public ActionListAdapter(Activity activity) {
		super(activity);
	}

	/**
	 * @param activity
	 *            Activity
	 * @param jsonActivities
	 *            JSONArray with list data
	 */
	public ActionListAdapter(Activity activity, JSONArray jsonActivities) {
		super(activity, jsonActivities);
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
		final RatingBar rating = (RatingBar) wrapper.findViewById(R.id_timelinelist.rating);

		if (position <= this.data.size()) {
			// Assign values to actionlist item
			final JSONObject jsonAction = (JSONObject) this.getItem(position);
			try {
				final JSONObject beer = jsonAction.getJSONObject("beer");
				final JSONObject user = jsonAction.getJSONObject("user");
				final long secondsAgo = Long.parseLong(jsonAction.getString("secondsAgo"));

				if ("consumption".equals(jsonAction.getString("type"))) {
					icon.setImageResource(R.drawable.ic_consumation2);
					rating.setVisibility(View.GONE);
				} else {
					icon.setImageResource(R.drawable.ic_rating);
					final float ratingValue = (float)jsonAction.getInt("rating");
					rating.setRating(ratingValue);
					rating.setVisibility(View.VISIBLE);
				}

				name.setText(beer.getString("name"));
				time.setText(getTimeString(secondsAgo));
				description.setText(createDetailText(jsonAction, user));
			} catch (JSONException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				ErrorHelper.onError(BierideeApplication.getAppContext().getString(R.string.malformedData), this.activity);
			}
		}

		return convertView;
	}

	private String getTimeString(long secondsAgo) {
		final int secPerMin = 60;
		final int minPerHour = 60;
		final int hoursPerDay = 24;
		final int longAgoLimit = 100;
		
		final Resources res = this.activity.getResources();
		final long seconds = secondsAgo;
		final long minutes = seconds / secPerMin;
		final long hours = minutes / minPerHour;
		final long days = hours / hoursPerDay;
		if (seconds < secPerMin) {
			return res.getQuantityString(R.plurals.numberOfSeconds, (int) seconds, seconds);
		} else if (minutes < minPerHour) {
			return res.getQuantityString(R.plurals.numberOfMinutes, (int) minutes, minutes);
		} else if (hours < hoursPerDay) {
			return res.getQuantityString(R.plurals.numberOfHours, (int) hours, hours);
		} else if (days < longAgoLimit) {
			return res.getQuantityString(R.plurals.numberOfDays, (int) days, days);
		} else {
			return this.activity.getString(R.string.timelinelist_longAgo);
		}
	}

	private String createDetailText(JSONObject jsonAction, JSONObject user) throws JSONException {
		final String username = user.getString("user");

		if ("consumption".equals(jsonAction.getString("type"))) {
			return this.activity.getString(R.string.timelinelist_feltThirsty, username);
		} else {
			return this.activity.getString(R.string.timelinelist_ratedBeer, username);
		}
	}

	@Override
	public String toString() {
		return "ActionListAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
