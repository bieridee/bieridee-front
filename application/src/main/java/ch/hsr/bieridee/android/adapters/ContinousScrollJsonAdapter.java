package ch.hsr.bieridee.android.adapters;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Adapter for data in JSONArrays, and continous scrolling. Updating data means the data will be appended!
 * 
 */
public abstract class ContinousScrollJsonAdapter extends BaseAdapter {

	protected final Activity activity;
	protected ArrayList<JSONObject> data;

	/**
	 * @param activity
	 *            Activity
	 */
	public ContinousScrollJsonAdapter(Activity activity) {
		this.activity = activity;
		this.data = new ArrayList<JSONObject>();
	}

	/**
	 * @param activity
	 *            Activity
	 * @param jsonActivities
	 *            JSONArray with list data
	 */
	public ContinousScrollJsonAdapter(Activity activity, JSONArray jsonActivities) {
		this.activity = activity;
		this.data.addAll(this.convertJSONArrayToList(jsonActivities));
	}

	/**
	 * Returns the number of list items.
	 * 
	 * @return Count of list items
	 */
	public int getCount() {
		return this.data.size();
	}

	/**
	 * Return the list item at the specified position.
	 * 
	 * @param position
	 *            Position in listtext
	 * @return JSON action object at the specified position
	 */
	public Object getItem(int position) {
		return this.data.get(position);
	}

	/**
	 * Return an unique ID of the specified item.
	 * 
	 * @param position
	 *            Position in list
	 * @return The id of the item at the specified position
	 */
	public abstract long getItemId(int position);

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
	 * @param jsonActions
	 *            New data to replace the old JSONArray
	 */
	public void updateData(JSONArray jsonActions) {
		this.data.addAll(convertJSONArrayToList(jsonActions));
	}

	private ArrayList<JSONObject> convertJSONArrayToList(JSONArray jsonArray) {
		final ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArray.length(); ++i) {
			list.add(jsonArray.optJSONObject(i));
		}
		return list;
	}

}
