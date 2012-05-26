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
import ch.hsr.bieridee.android.R;

/**
 * The BeerListAdapter adapts the JSON beer structure to the Android ListView.
 * 
 */
public class RecommendationsListAdapter extends JsonListAdapter {

	private static final String LOG_TAG = RecommendationsListAdapter.class.getName();

	/**
	 * @param activity
	 *            Activity
	 */
	public RecommendationsListAdapter(Activity activity) {
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
		final JSONObject beerJson = this.jsonArray.optJSONObject(position).optJSONObject("beer");
		return beerJson.optLong("id");
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
			convertView = this.activity.getLayoutInflater().inflate(R.layout.recommendationslist_item, null);
		}

		final JSONObject jsonRecommendation = (JSONObject) this.getItem(position);
		final LinearLayout wrapper = (LinearLayout) convertView;
		final TextView name = (TextView) wrapper.findViewById(R.id_recommendations.itemName);
		final TextView absoluteWeight = (TextView) wrapper.findViewById(R.id_recommendations.absoluteWeight);
		final RatingBar normalizedWeight = (RatingBar) wrapper.findViewById(R.id_recommendations.normalizedWeight);
		try {
			final JSONObject beerJson = jsonRecommendation.getJSONObject("beer");
			name.setText(beerJson.getString("name"));
			normalizedWeight.setRating((float) jsonRecommendation.getDouble("normalizedWeight"));
			final double absWeight = jsonRecommendation.getDouble("weight");
			absoluteWeight.setText(this.activity.getString(R.string.recommendations_absoluteWeight, absWeight));
		} catch (JSONException e) {
			Log.d(LOG_TAG, e.getMessage(),e);
		}

		return convertView;
	}

	@Override
	public String toString() {
		return "RecommendationsListAdapter{activity=" + this.activity.getClass().getName() + '}';
	}
}
