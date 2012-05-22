package ch.hsr.bieridee.android.adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.hsr.bieridee.android.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * The BeerListAdapter adapts the JSON beer structure to the Android ListView.
 * 
 * For further information, see the Adapter interface:
 * http://developer.android.com/reference/android/widget/Adapter.html
 */
public class BeertypeListAdapter extends BaseExpandableListAdapter {

	private final Activity activity;
	private JSONArray jsonArray;
	private String[] groups;
	private String[][] children;

	/**
	 * @param activity
	 *            Activity
	 */
	public BeertypeListAdapter(Activity activity) {
		this.activity = activity;
		this.jsonArray = new JSONArray();
	}

	/**
	 * @param activity
	 *            Activity
	 * @param jsonBeertypes
	 *            JSONArray with list data
	 */
	public BeertypeListAdapter(Activity activity, JSONArray jsonBeertypes) {
		this.jsonArray = jsonBeertypes;
		this.activity = activity;
		initializeGroupsAndChildren();
	}

	private void initializeGroupsAndChildren() {
		this.groups = new String[this.jsonArray.length()];
		this.children = new String[this.jsonArray.length()][1]; // only one child per group (beertype description)
		for (int i = 0; i < this.jsonArray.length(); ++i) {
			JSONObject beertypeJson;
			try {
				beertypeJson = this.jsonArray.getJSONObject(i);
				this.groups[i] = beertypeJson.getString("name");
				this.children[i][0] = beertypeJson.getString("description"); // no second loop needed, bc we have always
																				// only one child
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Update the internal JSONArray with new data.
	 * 
	 * @param jsonBeertypes
	 *            New data to replace the old JSONArray
	 */
	public void updateData(JSONArray jsonBeertypes) {
		this.jsonArray = jsonBeertypes;
		this.initializeGroupsAndChildren();
	}

	public String getChild(int groupPosition, int childPosition) {
		return this.children[groupPosition][childPosition];
	}

	
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(R.layout.beertypelist_item_child, null);
		}
		
		final TextView wrapper = (TextView) convertView.findViewById(R.id_beertype.description);
		wrapper.setText(this.children[groupPosition][childPosition]);
		
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return this.children[groupPosition].length;
	}

	public String getGroup(int groupPosition) {
		return this.groups[groupPosition];
	}

	public int getGroupCount() {
		return this.groups.length;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(R.layout.beertypelist_item_group, null);
		}
		
		final TextView wrapper = (TextView) convertView.findViewById(R.id_beertype.name);
		wrapper.setText(this.groups[groupPosition]);
		
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}