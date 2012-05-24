package ch.hsr.bieridee.android.adapters;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.hsr.bieridee.android.BierideeApplication;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.utils.ErrorHelper;

/**
 * The BreweryListAdapter adapts the JSON brewery structure to the Android ListView.
 * 
 */
public class BreweryListAdapter extends JsonListAdapter {
	
	private static final String LOG_TAG = BreweryListAdapter.class.getName();
	
	/**
	 * @param activity
	 *            Activity
	 */
	public BreweryListAdapter(Activity activity) {
		super(activity);
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
			convertView = this.activity.getLayoutInflater().inflate(R.layout.brewerylist_item, null);
		}

		final JSONObject jsonBrewery = (JSONObject) this.getItem(position);
		final LinearLayout wrapper = (LinearLayout) convertView;
		final TextView name = (TextView) wrapper.findViewById(R.id_brewerylist.itemName);
		final TextView description = (TextView) wrapper.findViewById(R.id_brewerylist.itemDescription);
		try {
			name.setText(jsonBrewery.getString("name"));
			description.setText(jsonBrewery.getString("description"));
		} catch (JSONException e) {
			Log.d(LOG_TAG, e.getMessage(), e);
			ErrorHelper.onError(BierideeApplication.getAppContext().getString(R.string.malformedData), this.activity);
		}

		return convertView;
	}

	@Override
	public String toString() {
		return "BreweryListAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
