package ch.hsr.bieridee.android;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ch.hsr.bieridee.android.config.Conf;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * Activity that shows a list of all beers in our database.
 */
public class BeerListActivity extends ListActivity {

	private static final String TAG = "bieridee-front";

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being shut down then this
	 *            Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
	 *            <b>Note: Otherwise it is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerlist);

		JSONArray jsonarray = null;
		try {
			final ClientResource cr = new ClientResource(Conf.API_URL + "beers");
			final Representation rep = cr.get(MediaType.APPLICATION_JSON);

			final String json = rep.getText();
			jsonarray = new JSONArray(json);
		} catch (ResourceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		setListAdapter(new BeerListAdapter(this, jsonarray));
	}

	@Override
	public void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
	}

}