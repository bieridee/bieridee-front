package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.exceptions.BierIdeeException;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Activity that shows a brewery detail.
 */
public final class BreweryDetailActivity extends Activity {

	private TextView name;
	private TextView size;
	private TextView description;
	private long breweryId;

	private static final String LOG_TAG = BreweryDetailActivity.class.getName();
	public static final String EXTRA_BREWERY_ID = "breweryId";
	private ProgressDialog progressDialog;
	private HttpHelper httpHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brewerydetail);
		this.httpHelper = AuthJsonHttp.create();
	}

	@Override
	public void onStart() {
		super.onStart();

		// Get brewery ID from intent
		this.breweryId = this.getIntent().getExtras().getLong(EXTRA_BREWERY_ID);
		Log.d(LOG_TAG, "onStart() with breweryId " + this.breweryId);

		// Get view objects
		this.name = (TextView) this.findViewById(R.id_brewerydetail.breweryName);
		this.size = (TextView) this.findViewById(R.id_brewerydetail.brewerySize);
		this.description = (TextView) this.findViewById(R.id_brewerydetail.breweryDescription);

		// Load data
		new GetBreweryDetail().execute();
	}

	/**
	 * Async task to get brewery detail from server and update UI.
	 */
	private class GetBreweryDetail extends AsyncTask<Void, Void, JSONObject> {
		@Override
		protected void onPreExecute() {
			BreweryDetailActivity.this.progressDialog = ProgressDialog.show(
					BreweryDetailActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingData), true);
		}

		@Override
		protected JSONObject doInBackground(Void... voids) {
			final String uri = Res.getURI(Res.BREWERY_DOCUMENT, Long.toString(BreweryDetailActivity.this.breweryId));
			final HttpResponse response = BreweryDetailActivity.this.httpHelper.get(uri);

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONObject(responseText);
					} catch (IOException e) {
						throw new BierIdeeException("IOException in GetBreweryDetail::doInBackground", e);
					} catch (JSONException e) {
						Toast.makeText(BreweryDetailActivity.this, getString(R.string.brewerydetail_fail_loadDetail), Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				} else {
					Log.e(LOG_TAG, "HTTP Response " + statusCode + " in GetBreweryDetail::doInBackground");
				}
			}
			Log.e(LOG_TAG, "HTTP Response was null in GetBreweryDetail::doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			Log.d(LOG_TAG, "onPostExecute()");
			if (result != null) {
				try {
					final String name = result.getString("name");
					final String size = result.getString("size");
					final String description = result.getString("description");
					BreweryDetailActivity.this.name.setText(name);
					BreweryDetailActivity.this.size.setText(BreweryDetailActivity.this.translateSize(size));
					BreweryDetailActivity.this.description.setText(description);
				} catch (JSONException e) {
					Toast.makeText(BreweryDetailActivity.this, getString(R.string.brewerydetail_fail_loadDetail), Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else {
				Log.w(LOG_TAG, "Result was null in GetBreweryDetail::onPostExecute");
			}
			BreweryDetailActivity.this.progressDialog.dismiss();
		}
	}
	
	private String translateSize(String size) {
		final StringBuilder sizeString = new StringBuilder(getString(R.string.size)).append(": ");
		if ("micro".equals(size)) {
			sizeString.append(this.getString(R.string.brewerydetail_micro));
		}
		if("regional".equals(size)) {
			sizeString.append(this.getString(R.string.brewerydetail_regional));
		}
		if("national".equals(size)) {
			sizeString.append(this.getString(R.string.brewerydetail_national));
		}
		if("international".equals(size)) {
			sizeString.append(this.getString(R.string.brewerydetail_international));
		}
		return sizeString.toString();
	}
	
	@Override
	public String toString() {
		return "BreweryDetailActivity{name=" + this.name + '}';
	}
}