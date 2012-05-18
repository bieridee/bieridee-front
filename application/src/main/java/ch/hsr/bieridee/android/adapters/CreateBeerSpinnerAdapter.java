package ch.hsr.bieridee.android.adapters;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * The BreweryListAdapter adapts the JSON brewery structure to the Android ListView.
 * 
 * For further information, see the Adapter interface:
 * http://developer.android.com/reference/android/widget/Adapter.html
 */
public class CreateBeerSpinnerAdapter extends BreweryListAdapter implements SpinnerAdapter {

	/**
	 * @param activity The activity
	 */
	public CreateBeerSpinnerAdapter(Activity activity) {
		super(activity);
	}

	
	/**
	 * Return the view for specified position, as dropdown.
	 * 
	 * @param position
	 *            Position in list
	 * @param convertView
	 *            The old view to reuse, if possible
	 * @param parent
	 *            The parent that this view will eventually be attached to
	 * @return A View corresponding to the data at the specified position.
	 */
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// Get & inflate brewerylist item from XML
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item, null);
		}

		// Assign values to brewerylist item
		final JSONObject jsonBrewery = (JSONObject) this.getItem(position);
		final CheckedTextView name = (CheckedTextView) convertView;
		try {
			name.setText(jsonBrewery.getString("name"));
		} catch (JSONException e) {
			e.printStackTrace(); // TODO Auto-generated catch block
		}

		return convertView;
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
			convertView = this.activity.getLayoutInflater().inflate(android.R.layout.simple_spinner_item, null);
		}

		// Assign values to brewerylist item
		final JSONObject jsonBrewery = (JSONObject) this.getItem(position);
		final TextView name = (TextView) convertView;
		try {
			name.setText(jsonBrewery.getString("name"));
		} catch (JSONException e) {
			e.printStackTrace(); // TODO Auto-generated catch block
		}

		return convertView;
	}

	@Override
	public String toString() {
		return "BeerCreateSpinnerAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
