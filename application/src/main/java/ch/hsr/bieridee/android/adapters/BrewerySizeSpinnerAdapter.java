package ch.hsr.bieridee.android.adapters;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class BrewerySizeSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

	List<String> sizes;
	Activity activity;

	public BrewerySizeSpinnerAdapter(Activity activity) {
		super();
		this.activity = activity;
		sizes = new LinkedList<String>();
		sizes.add("Small");
		sizes.add("Medium");
		sizes.add("Large");
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return sizes.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return sizes.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(android.R.layout.simple_spinner_item, null);
		}

		// Assign values to brewerylist item
		final String brewerySize = (String) this.getItem(position);
		final TextView name = (TextView) convertView;
		name.setText(brewerySize);
		return convertView;
	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return sizes.isEmpty();
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// Get & inflate brewerylist item from XML
		if (convertView == null) {
			convertView = this.activity.getLayoutInflater().inflate(android.R.layout.simple_spinner_dropdown_item, null);
		}

		// Assign values to brewerylist item
		final String brewery = (String) this.getItem(position);
		final CheckedTextView name = (CheckedTextView) convertView;
		name.setText(brewery);

		return convertView;
	}

}
