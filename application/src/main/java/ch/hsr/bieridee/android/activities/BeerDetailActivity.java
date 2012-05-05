package ch.hsr.bieridee.android.activities;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Uniform;
import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.ClientResourceFactory;

/**
 * Activity that shows a list of all beers in our database.
 */
public class BeerDetailActivity extends Activity {

	Button consumptionButton;
	TextView name;
	TextView brand;
	TextView averageRating;
	RatingBar ratingBar;
	String beerJSON;

	private static final String LOG_TAG = "BeerListActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerdetail);
	}

	@Override
	public void onStart() {
		Log.d(LOG_TAG, "onStart");
		super.onStart();

		this.consumptionButton = (Button) this.findViewById(R.id_beerdetail.consumptionButton);
		this.name = (TextView) this.findViewById(R.id_beerdetail.beerName);
		this.brand = (TextView) this.findViewById(R.id_beerdetail.beerBrand);
		this.averageRating = (TextView) this.findViewById(R.id_beerdetail.beerAverageRating);
		this.ratingBar = (RatingBar) this.findViewById(R.id_beerdetail.ratingBar);

		loadBeerDetail();
	}

	private void loadBeerDetail() {
		final Bundle extras = this.getIntent().getExtras();
		final long beerId = extras.getLong("beerid");

		final String dialogTitle = getString(R.string.pleaseWait);
		final String dialogMessage = getString(R.string.loadingData);
		final ProgressDialog dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);

		// Do HTTP request
		final ClientResource cr = ClientResourceFactory.getClientResource(Res.getURI(Res.BEER_DOCUMENT, Long.toString(beerId)));
		cr.setOnResponse(new Uniform() {
			public void handle(Request request, Response response) {
				// Update data
				try {
					BeerDetailActivity.this.beerJSON = response.getEntity().getText();
				} catch (IOException e) {
					e.printStackTrace(); // TODO
				}

				// Update view
				runOnUiThread(new Runnable() {
					public void run() {
						try {
							final JSONObject beer = new JSONObject(BeerDetailActivity.this.beerJSON);
							final String name = beer.getString("name");
							final String brand = beer.getString("brand");
							final String averageRating = beer.getString("rating");
							BeerDetailActivity.this.name.setText(name);
							BeerDetailActivity.this.brand.setText(brand);
							BeerDetailActivity.this.averageRating.setText(averageRating);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				});

			}
		});
		cr.get(MediaType.APPLICATION_JSON); // Async call

	}
}