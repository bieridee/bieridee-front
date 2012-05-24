package ch.hsr.bieridee.android.adapters;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * The BreweryListAdapter adapts the JSON brewery structure to the Android ListView.
 * 
 * For further information, see the Adapter interface:
 * http://developer.android.com/reference/android/widget/Adapter.html
 */
public abstract class JsonListAdapter extends BaseAdapter {

	protected final Activity activity;
	protected JSONArray jsonArray;

	/**
	 * @param activity
	 *            Activity
	 */
	public JsonListAdapter(Activity activity) {
		this.activity = activity;
		this.jsonArray = new JSONArray();
	}

	/**
	 * @param activity
	 *            Activity
	 * @param jsonArray
	 *            JSONArray with list data
	 */
	public JsonListAdapter(Activity activity, JSONArray jsonArray) {
		this.jsonArray = jsonArray;
		this.activity = activity;
	}

	/**
	 * Returns the number of list items.
	 * 
	 * @return Count of list items
	 */
	public int getCount() {
		return this.jsonArray.length();
	}

	/**
	 * Return the list item at the specified position.
	 * 
	 * @param position
	 *            Position in list
	 * @return JSON brewery object at the specified position
	 */
	public Object getItem(int position) {
		return this.jsonArray.optJSONObject(position);
	}

	/**
	 * Return an unique ID of the specified item.
	 * 
	 * @param position
	 *            Position in list
	 * @return The id of the item at the specified position
	 */
	public long getItemId(int position) {
		final JSONObject jsonObject = (JSONObject) this.getItem(position);
		return jsonObject.optLong("id");
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
	public abstract View getView(int position, View convertView, ViewGroup parent);

	/**
	 * Update the internal JSONArray with new data.
	 * 
	 * @param jsonArray
	 *            New data to replace the old JSONArray
	 */
	public void updateData(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	/**
	 * Gets the position of an element in the list by id.
	 * 
	 * @param id
	 *            Id of the object
	 * @return The position
	 */
	public int getPositionOf(long id) {
		for (int i = 0; i < getCount(); ++i) {
			if (this.getItemId(i) == id) {
				return i;
			}
		}
		return -1;
	}

}
