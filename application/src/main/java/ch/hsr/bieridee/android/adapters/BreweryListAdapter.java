package ch.hsr.bieridee.android.adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.hsr.bieridee.android.R;

/**
 * The BreweryListAdapter adapts the JSON brewery structure to the Android ListView.
 * 
 * For further information, see the Adapter interface:
 * http://developer.android.com/reference/android/widget/Adapter.html
 */
public class BreweryListAdapter extends BaseAdapter {

	protected final Activity activity;
	protected JSONArray jsonBreweries;

	/**
	 * @param activity Activity	public BrewerySpinnerAdapter(Activity activity) {
		super(activity);
	}

	 */
	public BreweryListAdapter(Activity activity) {
		this.activity = activity;
		this.jsonBreweries = new JSONArray();
	}
	/**
	 * @param activity Activity
	 * @param jsonBreweries JSONArray with list data
	 */
	public BreweryListAdapter(Activity activity, JSONArray jsonBreweries) {
		this.jsonBreweries = jsonBreweries;
		this.activity = activity;
	}

	/**
	 * Returns the number of list items.
	 * 
	 * @return Count of list items
	 */
	public int getCount() {
		return this.jsonBreweries.length();
	}

	/**
	 * Return the list item at the specified position.
	 * 
	 * @param position
	 *            Position in list
	 * @return JSON brewery object at the specified position
	 */
	public Object getItem(int position) {
		return this.jsonBreweries.optJSONObject(position);
	}

	/**
	 * Return an unique ID of the specified item.
	 * 
	 * @param position
	 *            Position in list
	 * @return The id of the item at the specified position
	 */
	public long getItemId(int position) {
		final JSONObject jsonBrewery = (JSONObject) this.getItem(position);
		return jsonBrewery.optLong("id");
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
		// Get & inflate brewerylist item from XML
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(R.layout.brewerylist_item, null);
		}

		// Assign values to brewerylist item
		final JSONObject jsonBrewery = (JSONObject) this.getItem(position);
		final LinearLayout wrapper = (LinearLayout) convertView;
		final TextView name = (TextView) wrapper.findViewById(R.id_brewerylist.itemName);
		final TextView description = (TextView) wrapper.findViewById(R.id_brewerylist.itemDescription);
		try {
			name.setText(jsonBrewery.getString("name"));
			description.setText(jsonBrewery.getString("description"));
		} catch (JSONException e) {
			e.printStackTrace(); // TODO Auto-generated catch block
		}

		return convertView;
	}

	/**
	 * Update the internal JSONArray with new data.
	 * 
	 * @param jsonBreweries
	 *            New data to replace the old JSONArray
	 */
	public void updateData(JSONArray jsonBreweries) {
		this.jsonBreweries = jsonBreweries;
	}

	@Override
	public String toString() {
		return "BreweryListAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
