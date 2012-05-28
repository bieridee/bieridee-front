package ch.hsr.bieridee.android.adapters;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.hsr.bieridee.android.BierideeApplication;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.utils.ErrorHelper;

/**
 * The BeerListAdapter adapts the JSON beer structure to the Android ListView.
 * 
 */
public class BeerListAdapter extends ContinousScrollJsonAdapter {

	private static final String LOG_TAG = BeerListAdapter.class.getName();

	/**
	 * @param activity
	 *            Activity
	 */
	public BeerListAdapter(Activity activity) {
		super(activity);
	}

	/**
	 * Return an unique ID of the specified item.
	 * 
	 * @param position
	 *            Position in list
	 * @return The id of the item at the specified position
	 */
	public long getItemId(int position) {
		return this.data.get(position).optLong("id");
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
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(R.layout.beerlist_item, null);
		}

		final JSONObject jsonBeer = (JSONObject) this.getItem(position);
		final LinearLayout wrapper = (LinearLayout) convertView;
		final TextView name = (TextView) wrapper.findViewById(R.id.beerListItemName);
		final RatingBar avgRating = (RatingBar) wrapper.findViewById(R.id.beerListItemAveragerating);
		final TextView brand = (TextView) wrapper.findViewById(R.id.beerListItemBrand);
		final TextView brewery = (TextView) wrapper.findViewById(R.id.beerListItemBrewery);
		try {
			name.setText(jsonBeer.getString("name"));
			avgRating.setRating((float) jsonBeer.getDouble("rating"));
			brand.setText(this.activity.getString(R.string.brand) + ": " + jsonBeer.getString("brand"));
			final JSONObject jsonBrewery = jsonBeer.getJSONObject("brewery");
			if (jsonBrewery.optBoolean("unknown")) {
				brewery.setText(this.activity.getString(R.string.brewery) + ": " + this.activity.getString(R.string.unknown));
			} else {
				brewery.setText(this.activity.getString(R.string.brewery) + ": " + jsonBrewery.getString("name"));
			}

		} catch (JSONException e) {
			Log.d(LOG_TAG, e.getMessage(), e);
			ErrorHelper.onError(BierideeApplication.getAppContext().getString(R.string.malformedData), this.activity);
		}

		return convertView;
	}

	/**
	 * @param id
	 *            id of beer to be removed
	 * 
	 */
	public void remove(long id) {
		for (int i = 0; i < this.data.size(); ++i) {
			try {
				if (this.data.get(i).getLong("id") == id) {
					this.data.remove(i);
				}
			} catch (JSONException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				ErrorHelper.onError(BierideeApplication.getAppContext().getString(R.string.malformedData), this.activity);
			}
		}
	}

	@Override
	public String toString() {
		return "BeerListAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
