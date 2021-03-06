package ch.hsr.bieridee.android.adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import ch.hsr.bieridee.android.BierideeApplication;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.utils.ErrorHelper;

/**
 * The BeerListAdapter adapts the JSON beer structure to the Android ListView.
 * 
 * For further information, see the Adapter interface:
 * http://developer.android.com/reference/android/widget/Adapter.html
 */
public class BeertypeListAdapter extends BaseExpandableListAdapter {
	
	private static final String LOG_TAG = BeertypeListAdapter.class.getName();
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
				Log.d(LOG_TAG, e.getMessage(), e);
				ErrorHelper.onError(BierideeApplication.getAppContext().getString(R.string.malformedData), this.activity);
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

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public String getChild(int groupPosition, int childPosition) {
		return this.children[groupPosition][childPosition];
	}

	
	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View, android.view.ViewGroup)
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(R.layout.beertypelist_item_child, null);
		}
		
		final TextView wrapper = (TextView) convertView.findViewById(R.id_beertype.description);
		wrapper.setText(this.children[groupPosition][childPosition]);
		
		return convertView;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public int getChildrenCount(int groupPosition) {
		return this.children[groupPosition].length;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public String getGroup(int groupPosition) {
		return this.groups[groupPosition];
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public int getGroupCount() {
		return this.groups.length;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View, android.view.ViewGroup)
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(R.layout.beertypelist_item_group, null);
		}
		
		final TextView wrapper = (TextView) convertView.findViewById(R.id_beertype.name);
		wrapper.setText(this.groups[groupPosition]);
		
		return convertView;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public boolean hasStableIds() {
		return true;
	}

	/* (non-Javadoc)
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	// SUPPRESS CHECKSTYLE: non-Javadoc
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
