package ch.hsr.bieridee.android.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.hsr.bieridee.android.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The BeerListAdapter adapts the JSON beer structure to the Android ListView.
 * 
 * For further information, see the Adapter interface:
 * http://developer.android.com/reference/android/widget/Adapter.html
 */
public class BeerListAdapter extends BaseAdapter {

	private final Activity activity;
	private JSONArray jsonBeers;

	/**
	 * @param activity
	 *            Activity
	 */
	public BeerListAdapter(Activity activity) {
		this.activity = activity;
		this.jsonBeers = new JSONArray();
	}

	/**
	 * @param activity
	 *            Activity
	 * @param jsonBeers
	 *            JSONArray with list data
	 */
	public BeerListAdapter(Activity activity, JSONArray jsonBeers) {
		this.jsonBeers = jsonBeers;
		this.activity = activity;
	}

	/**
	 * Returns the number of list items.
	 * 
	 * @return Count of list items
	 */
	public int getCount() {
		return this.jsonBeers.length();
	}

	/**
	 * Return the list item at the specified position.
	 * 
	 * @param position
	 *            Position in list
	 * @return JSON beer object at the specified position
	 */
	public Object getItem(int position) {
		return this.jsonBeers.optJSONObject(position);
	}

	/**
	 * Return an unique ID of the specified item.
	 * 
	 * @param position
	 *            Position in list
	 * @return The id of the item at the specified position
	 */
	public long getItemId(int position) {
		final JSONObject jsonBeer = (JSONObject) this.getItem(position);
		return jsonBeer.optLong("id");
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
		// Get & inflate beerlist item from XML
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(R.layout.beerlist_item, null);
		}

		// Assign values to beerlist item
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
			brewery.setText(this.activity.getString(R.string.brewery) + ": " + jsonBrewery.getString("name"));
		} catch (JSONException e) {
			e.printStackTrace(); // TODO Auto-generated catch block
		}

		return convertView;
	}

	/**
	 * Update the internal JSONArray with new data.
	 * 
	 * @param jsonBeers
	 *            New data to replace the old JSONArray
	 */
	public void updateData(JSONArray jsonBeers) {
		this.jsonBeers = jsonBeers;
	}

	@Override
	public String toString() {
		return "BeerListAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
