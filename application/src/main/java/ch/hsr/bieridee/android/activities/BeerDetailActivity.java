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

	private static final String LOG_TAG = "BeerListActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beerlist);
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
		final long id = extras.getLong("id");

		final String dialogTitle = getString(R.string.pleaseWait);
		final String dialogMessage = getString(R.string.loadingData);
		final ProgressDialog dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);

		// Do HTTP request
		final ClientResource cr = ClientResourceFactory.getClientResource(Res.getURI(Res.BEER_DOCUMENT, Long.toString(id)));
		cr.setOnResponse(new Uniform() {
			public void handle(Request request, Response response) {
				JSONObject beer = null;

				// Update data
				try {
					final String json = response.getEntity().getText();
					beer = new JSONObject(json);
				} catch (IOException e) {
					e.printStackTrace(); // TODO
				} catch (JSONException e) {
					e.printStackTrace(); // TODO
				}

				try {
					final String name = beer.getString("name");
					final String brand = beer.getString("brand");
					final String averageRating = beer.getString("rating");
					// Update view
					runOnUiThread(new Runnable() {
						public void run() {
							BeerDetailActivity.this.name.setText(name);
							BeerDetailActivity.this.brand.setText(brand);
							BeerDetailActivity.this.averageRating.setText(averageRating);
							dialog.dismiss();
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
		cr.get(MediaType.APPLICATION_JSON); // Async call

	}

	/**
	 * Updates beer list data and redraws list view.
	 */
	// private void updateBeerList() {
	// Log.d(LOG_TAG, "Updating beer list");
	//
	// final BeerListAdapter adapter = (BeerListAdapter) getListAdapter();
	//
	// // Show waiting dialog
	// final String dialogTitle = getString(R.string.pleaseWait);
	// final String dialogMessage = getString(R.string.loadingData);
	// final ProgressDialog dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);
	//
	// // Do HTTP request
	// final ClientResource cr = ClientResourceFactory.getClientResource(Res.getURI(Res.BEER_COLLECTION));
	// cr.setOnResponse(new Uniform() {
	// public void handle(Request request, Response response) {
	// JSONArray beers = new JSONArray();
	//
	// // Update data
	// try {
	// final String json = response.getEntity().getText();
	// beers = new JSONArray(json);
	// adapter.updateData(beers);
	// } catch (IOException e) {
	// e.printStackTrace(); // TODO
	// } catch (JSONException e) {
	// e.printStackTrace(); // TODO
	// }
	//
	// // Update view
	// runOnUiThread(new Runnable() {
	// public void run() {
	// adapter.notifyDataSetChanged();
	// dialog.dismiss();
	// }
	// });
	// }
	// });
	// cr.get(MediaType.APPLICATION_JSON); // Async call
	// }
}