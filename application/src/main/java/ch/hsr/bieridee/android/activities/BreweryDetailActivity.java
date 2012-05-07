package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.ClientResourceFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Uniform;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import java.io.IOException;

/**
 * Activity that shows a brewery detail.
 */
public final class BreweryDetailActivity extends Activity {

	private TextView name;
	private TextView size;
	private TextView description;
	private String breweryJSON;

	private static final String LOG_TAG = BreweryDetailActivity.class.getName();
	public static final String EXTRA_BREWERY_ID = "breweryId";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.brewerydetail);
	}

	@Override
	public void onStart() {
		super.onStart();

		this.name = (TextView) this.findViewById(R.id_brewerydetail.breweryName);
		this.size = (TextView) this.findViewById(R.id_brewerydetail.brewerySize);
		this.description = (TextView) this.findViewById(R.id_brewerydetail.breweryDescription);

		this.loadBreweryDetail();
	}

	/**
	 * Load brewery document and fill view with data.
	 */
	private void loadBreweryDetail() {
		// Get brewery ID from intent
		final Bundle extras = this.getIntent().getExtras();
		final long breweryId = extras.getLong(EXTRA_BREWERY_ID);

		// Show loading dialog
		final String dialogTitle = getString(R.string.pleaseWait);
		final String dialogMessage = getString(R.string.loadingData);
		final ProgressDialog dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);

		// Do and handle HTTP request
		final ClientResource cr = ClientResourceFactory.getClientResource(Res.getURI(Res.BREWERY_DOCUMENT, Long.toString(breweryId)));
		cr.setOnResponse(new Uniform() {
			public void handle(Request request, Response response) {
				try {
					BreweryDetailActivity.this.breweryJSON = response.getEntity().getText();
				} catch (IOException e) {
					e.printStackTrace(); // TODO
				}

				runOnUiThread(new Runnable() {
					public void run() {
						try {
							final JSONObject brewery = new JSONObject(BreweryDetailActivity.this.breweryJSON);
							final String name = brewery.getString("name");
							final String size = brewery.getString("size");
							final String description = brewery.getString("description");
							BreweryDetailActivity.this.name.setText(name);
							BreweryDetailActivity.this.size.setText(size);
							BreweryDetailActivity.this.description.setText(description);
						} catch (JSONException e) {
							e.printStackTrace(); // TODO
						}
						dialog.dismiss();
					}
				});
			}
		});
		cr.get(MediaType.APPLICATION_JSON); // Async call
		cr.release();
	}

	@Override
	public String toString() {
		return "BreweryDetailActivity{name=" + name + '}';
	}
}