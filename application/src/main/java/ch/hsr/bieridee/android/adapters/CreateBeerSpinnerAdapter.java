package ch.hsr.bieridee.android.adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.hsr.bieridee.android.BierideeApplication;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.utils.ErrorHelper;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * The CreateBeerSpinnerAdapter adapts the JSON bewery and beertype structure to the Android Spinner.
 * 
 */
public class CreateBeerSpinnerAdapter extends JsonListAdapter implements SpinnerAdapter {
	
	private static final String LOG_TAG = CreateBeerSpinnerAdapter.class.getName();
	
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
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item, null);
		}

		final JSONObject jsonBrewery = (JSONObject) this.getItem(position);
		final CheckedTextView name = (CheckedTextView) convertView;
		try {
			name.setText(jsonBrewery.getString("name"));
		} catch (JSONException e) {
			Log.d(LOG_TAG, e.getMessage(), e);
			ErrorHelper.onError(BierideeApplication.getAppContext().getString(R.string.malformedData), this.activity);
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
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(android.R.layout.simple_spinner_item, null);
		}

		final JSONObject jsonBrewery = (JSONObject) this.getItem(position);
		final TextView name = (TextView) convertView;
		try {
			name.setText(jsonBrewery.getString("name"));
		} catch (JSONException e) {
			Log.d(LOG_TAG, e.getMessage(), e);
			ErrorHelper.onError(BierideeApplication.getAppContext().getString(R.string.malformedData), this.activity);
		}

		return convertView;
	}
	
	@Override
	public void updateData(JSONArray jsonArray) {
		final JSONObject unknownOption = new JSONObject();
		final JSONArray newJsonArray = new JSONArray();
		try {
			unknownOption.put("id", -1l);
			unknownOption.put("name", this.activity.getString(R.string.unknown));
			newJsonArray.put(unknownOption);
			for(int i = 0; i< jsonArray.length(); ++i) {
				newJsonArray.put(jsonArray.get(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.jsonArray = newJsonArray;
	}

	@Override
	public String toString() {
		return "BeerCreateSpinnerAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
