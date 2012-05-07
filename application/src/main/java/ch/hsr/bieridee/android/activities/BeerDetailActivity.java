package ch.hsr.bieridee.android.activities;

import java.io.IOException;

import ch.hsr.bieridee.android.config.Auth;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Uniform;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.ClientResourceFactory;

/**
 * Activity that shows a beers detail.
 */
public class BeerDetailActivity extends Activity {

	private Button consumptionButton;
	private TextView name;
	private TextView brand;
	private TextView averageRating;
	private RatingBar ratingBar;
	private String beerJSON;
	private String ratingJSON;
	private String avgRatingJSON;
	private long beerId;
	private String username;

	private static final String LOG_TAG = BeerDetailActivity.class.getName();
	public static final String EXTRA_BEER_ID = "beerId";

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

		this.username = Auth.getUsername();

		this.consumptionButton = (Button) this.findViewById(R.id_beerdetail.consumptionButton);

		this.name = (TextView) this.findViewById(R.id_beerdetail.beerName);
		this.brand = (TextView) this.findViewById(R.id_beerdetail.beerBrand);
		this.averageRating = (TextView) this.findViewById(R.id_beerdetail.beerAverageRating);

		this.ratingBar = (RatingBar) this.findViewById(R.id_beerdetail.ratingBar);
		this.ratingBar.setStepSize(1); // no half-star-ratings

		this.setConsumtionButtonAction();
		this.setRatingBarAction();
		this.loadBeerDetail();
		this.loadBeerRating();
	}

	private void setConsumtionButtonAction() {
		this.consumptionButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(v.getContext(), "TODO: Track consumption", Toast.LENGTH_LONG).show();
				final String consumptionUri = Res.getURI(Res.CONSUMPTION_DOCUMENT, Long.toString(BeerDetailActivity.this.beerId), BeerDetailActivity.this.username);
				final ClientResource cr = ClientResourceFactory.getClientResource(consumptionUri);
				cr.post(null);
			}
		});
	}

	private void setRatingBarAction() {
		this.ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				if (fromUser) {
					final String ratingUri = Res.getURI(Res.RATING_DOCUMENT, Long.toString(BeerDetailActivity.this.beerId), BeerDetailActivity.this.username);
					final ClientResource cr = ClientResourceFactory.getClientResource(ratingUri);
					final JSONObject newRating = new JSONObject();
					try {
						newRating.put("rating", rating);
					} catch (JSONException e) {
						e.printStackTrace(); // TODO
					}

					final Representation rep = new StringRepresentation(newRating.toString(), MediaType.APPLICATION_JSON);
					cr.setOnResponse(new Uniform() {

						public void handle(Request request, Response response) {
							try {
								BeerDetailActivity.this.avgRatingJSON = response.getEntity().getText();
							} catch (IOException e) {
								e.printStackTrace(); // TODO
							}

							runOnUiThread(new Runnable() {
								public void run() {
									try {
										final JSONObject avgRatingJson = new JSONObject(BeerDetailActivity.this.avgRatingJSON);
										final String avgRating = Double.toString(avgRatingJson.getDouble("averagerating"));
										BeerDetailActivity.this.averageRating.setText(avgRating);
									} catch (JSONException e) {
										e.printStackTrace(); // TODO
									}
								}
							});

						}
					});
					cr.post(rep);
					cr.release();
				}
			}
		});
	}

	private void loadBeerDetail() {
		final Bundle extras = this.getIntent().getExtras();
		this.beerId = extras.getLong(EXTRA_BEER_ID);

		final String dialogTitle = getString(R.string.pleaseWait);
		final String dialogMessage = getString(R.string.loadingData);
		final ProgressDialog dialog = ProgressDialog.show(this, dialogTitle, dialogMessage, true);

		final ClientResource cr = ClientResourceFactory.getClientResource(Res.getURI(Res.BEER_DOCUMENT, Long.toString(this.beerId)));
		cr.setOnResponse(new Uniform() {
			public void handle(Request request, Response response) {
				try {
					BeerDetailActivity.this.beerJSON = response.getEntity().getText();
				} catch (IOException e) {
					e.printStackTrace(); // TODO
				}

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

	private void loadBeerRating() {
		final ClientResource cr = ClientResourceFactory.getClientResource(Res.getURI(Res.RATING_DOCUMENT, Long.toString(this.beerId), this.username));
		cr.setOnResponse(new Uniform() {
			public void handle(Request request, Response response) {
				try {
					BeerDetailActivity.this.ratingJSON = null;
					if (response.getStatus() != Status.CLIENT_ERROR_NOT_FOUND) {
						BeerDetailActivity.this.ratingJSON = response.getEntity().getText();
					}
				} catch (IOException e) {
					e.printStackTrace(); // TODO
				}

				runOnUiThread(new Runnable() {
					public void run() {
						try {
							if (BeerDetailActivity.this.ratingJSON != null) {
								final JSONObject rating = new JSONObject(BeerDetailActivity.this.ratingJSON);
								final int currentRating = rating.getInt("rating");
								BeerDetailActivity.this.ratingBar.setRating(currentRating);
							} else {
								// beer not rated yet
								Toast.makeText(getApplicationContext(), "Beer is not rated yet", Toast.LENGTH_LONG).show();
								BeerDetailActivity.this.ratingBar.setRating(0f);
							}
						} catch (JSONException e) {
							e.printStackTrace(); // TODO
						}

					}
				});
			}
		});
		cr.get(MediaType.APPLICATION_JSON); // Async call
		cr.release();
	}

}