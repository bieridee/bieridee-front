package ch.hsr.bieridee.android.adapters;

import org.json.JSONException;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * The BrewerySpinnerAdapter adapts the JSON brewerySize structure to the Android ListView.
 * 
 */
public class BrewerySizeSpinnerAdapter extends JsonListAdapter implements SpinnerAdapter {

	/**
	 * @param activity
	 *            The activity
	 */
	public BrewerySizeSpinnerAdapter(Activity activity) {
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
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item, null);
		}

		final String brewerySize = (String) this.getItem(position);
		final CheckedTextView name = (CheckedTextView) convertView;
		name.setText(brewerySize);

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
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(android.R.layout.simple_spinner_item, null);
		}

		final String brewerySize = (String) this.getItem(position);
		final TextView name = (TextView) convertView;
		name.setText(brewerySize);

		return convertView;
	}

	@Override
	public String toString() {
		return "BrewerySizeSpinnerAdapter{activity=" + this.activity.getClass().getName() + '}';
	}

	@Override
	public Object getItem(int position) {
		String value = "empty";
		try {
			value = (String) this.jsonArray.get(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * Return an unique ID of the specified item.
	 * 
	 * @param position
	 *            Position in list
	 * @return The id of the item at the specified position
	 */

	@Override
	public long getItemId(int position) {
		return position;
	}
}
