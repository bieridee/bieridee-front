package ch.hsr.bieridee.android.activities;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.Validators;
import ch.hsr.bieridee.android.adapters.CreateBeerSpinnerAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;
import ch.hsr.bieridee.android.utils.ErrorHelper;

/**
 * Activity to create a new beer.
 * 
 */
public class BeerCreateActivity extends Activity {

	private static final String LOG_TAG = BreweryCreateActivity.class.getName();
	private CreateBeerSpinnerAdapter breweryAdapter;
	private CreateBeerSpinnerAdapter beertypeAdapter;
	private ArrayAdapter<String> autoCompleteAdapter;
	private HttpHelper httpHelper;
	private MultithreadProgressDialog progressDialog;
	private EditText beername;
	private AutoCompleteTextView brand;
	private Spinner brewerySpinner;
	private Spinner beertypeSpinner;
	private ImageButton beerNameInfoButton;
	private ImageButton brandInfoButton;
	private ImageButton beertypeInfoButton;
	private ImageButton breweryInfoButton;
	private Button createButton;

	private long beerId;
	private boolean newBeer;

	private GetBrandData getBrandDataTask;
	private GetBreweryData getBreweryDataTask;
	private GetBeertypeData getBeertypeDataTask;
	private GetBeerDetail getBeerDetailTask;
	private final CountDownLatch allLoadingTasksFinished = new CountDownLatch(3);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beercreate);

		this.httpHelper = AuthJsonHttp.create();
		this.progressDialog = new MultithreadProgressDialog(getString(R.string.pleaseWait), getString(R.string.loadingData));

		this.beername = (EditText) findViewById(R.id_beercreate.beername);
		this.brand = (AutoCompleteTextView) findViewById(R.id_beercreate.brandAutocomplete);

		this.beertypeSpinner = (Spinner) findViewById(R.id_beercreate.beertypeSpinner);
		this.beertypeAdapter = new CreateBeerSpinnerAdapter(this);
		this.beertypeSpinner.setAdapter(this.beertypeAdapter);

		this.brewerySpinner = (Spinner) findViewById(R.id_beercreate.brewerySpinner);
		this.breweryAdapter = new CreateBeerSpinnerAdapter(this);
		this.brewerySpinner.setAdapter(this.breweryAdapter);

		this.createButton = (Button) findViewById(R.id_beercreate.createButton);

		this.newBeer = true;
		this.setCreateButtonAction();

		this.beerNameInfoButton = (ImageButton) findViewById(R.id_beercreate.nameInfoButton);
		this.setBeerNameInfoButtonAction();
		this.brandInfoButton = (ImageButton) findViewById(R.id_beercreate.brandInfoButton);
		this.setBrandNameInfoButtonAction();
		this.beertypeInfoButton = (ImageButton) findViewById(R.id_beercreate.beertypeInfoButton);
		setBeertypeInfoButtonAction();
		this.breweryInfoButton = (ImageButton) findViewById(R.id_beercreate.breweryInfoButton);
		setBreweryInfoButtonAction();
		final Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getLong("beerToUpdate") > 0) {
			BeerCreateActivity.this.beerId = extras.getLong("beerToUpdate");
			this.newBeer = false;
			this.createButton.setText("Save");
			this.getBeerDetailTask = new GetBeerDetail();
			this.getBeerDetailTask.execute();
		}

	}

	private void setBeerNameInfoButtonAction() {
		this.beerNameInfoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(BeerCreateActivity.this);
				builder.setMessage(BeerCreateActivity.this.getString(R.string.beercreate_nameInfo)).setCancelable(true).setPositiveButton(BeerCreateActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				final AlertDialog info = builder.create();
				info.show();
			}
		});
	}

	private void setBrandNameInfoButtonAction() {
		this.brandInfoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(BeerCreateActivity.this);
				builder.setMessage(BeerCreateActivity.this.getString(R.string.beercreate_brandInfo)).setCancelable(true).setPositiveButton(BeerCreateActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				final AlertDialog info = builder.create();
				info.show();
			}
		});
	}

	private void setBeertypeInfoButtonAction() {
		this.beertypeInfoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(BeerCreateActivity.this);
				builder.setMessage(BeerCreateActivity.this.getString(R.string.beercreate_beertypeInfo)).setCancelable(true).setPositiveButton(BeerCreateActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).setNeutralButton(BeerCreateActivity.this.getString(R.string.more), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						final Intent intent = new Intent(BeerCreateActivity.this, BeertypeListActivity.class);
						BeerCreateActivity.this.startActivity(intent);
					}
				});
				final AlertDialog info = builder.create();
				info.show();
			}
		});
	}

	private void setBreweryInfoButtonAction() {
		this.breweryInfoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(BeerCreateActivity.this);
				builder.setMessage(BeerCreateActivity.this.getString(R.string.beercreate_breweryInfo)).setCancelable(true).setPositiveButton(BeerCreateActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).setNeutralButton(BeerCreateActivity.this.getString(R.string.add), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						final Intent intent = new Intent(BeerCreateActivity.this, BreweryCreateActivity.class);
						startActivity(intent);
					}
				});
				final AlertDialog info = builder.create();
				info.show();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		this.getBrandDataTask = new GetBrandData();
		this.getBeertypeDataTask = new GetBeertypeData();
		this.getBreweryDataTask = new GetBreweryData();

		this.getBrandDataTask.execute();
		this.getBeertypeDataTask.execute();
		this.getBreweryDataTask.execute();
	}

	private void setCreateButtonAction() {
		this.createButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				final String beername = BeerCreateActivity.this.beername.getText().toString().trim();
				final String brand = BeerCreateActivity.this.brand.getText().toString().trim();
				final long beertypeId = BeerCreateActivity.this.beertypeSpinner.getSelectedItemId();
				final long breweryId = BeerCreateActivity.this.brewerySpinner.getSelectedItemId();

				if (Validators.validateNonEmpty(beername) && Validators.validateNonEmpty(brand)) {
					final JSONObject newBeer = new JSONObject();
					try {
						newBeer.put("name", beername);
						newBeer.put("brand", brand);
						if (beertypeId < 0) { // id -1 means beertype is unknown
							newBeer.put("unknownbeertype", true);
						}
						newBeer.put("beertype", beertypeId);
						if (breweryId < 0) { // id -1 means brewery is unknown
							newBeer.put("unknownbrewery", true);
						}
						newBeer.put("brewery", breweryId);
					} catch (JSONException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						ErrorHelper.onError(BeerCreateActivity.this.getString(R.string.malformedData), BeerCreateActivity.this);
					}

					new BeerCreateActivity.AddNewBeer().execute(newBeer);
				} else {
					Toast.makeText(BeerCreateActivity.this, BeerCreateActivity.this.getString(R.string.pleaseProvideAllData), Toast.LENGTH_SHORT).show();
				}

			}

		});
	}

	/**
	 * Adds a new beer!
	 */
	private class AddNewBeer extends AsyncTask<JSONObject, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(JSONObject... params) {
			Log.d(LOG_TAG, "doInBackground()");

			HttpResponse response = null;
			try {
				if (BeerCreateActivity.this.newBeer) {
					response = BeerCreateActivity.this.httpHelper.post(Res.getURI(Res.BEER_COLLECTION), params[0]);
				} else {
					final String uri = Res.getURI(Res.BEER_DOCUMENT, BeerCreateActivity.this.beerId + "");
					response = BeerCreateActivity.this.httpHelper.put(uri, params[0]);
				}
			} catch (IOException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.connectionError));
			}

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONObject(responseText);
					} catch (IOException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
					} catch (JSONException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			Log.d(LOG_TAG, "onPostExecute");
			if (isCancelled()) {
				Log.d(LOG_TAG, "cancel onPostExecute");
				return;
			}
			if (result != null) {
				if (BeerCreateActivity.this.newBeer) {
					onPostNewBeer(result);
				} else {
					onPostChangeBeer(result);
				}
			} else {
				Toast.makeText(BeerCreateActivity.this, BeerCreateActivity.this.getString(R.string.handsomeError), Toast.LENGTH_SHORT).show();
			}
		}

		private void onPostChangeBeer(JSONObject result) {
			Toast.makeText(BeerCreateActivity.this, BeerCreateActivity.this.getString(R.string.changesSaved), Toast.LENGTH_SHORT).show();
			BeerCreateActivity.this.finish();
		}

		private void onPostNewBeer(JSONObject result) {
			Toast.makeText(BeerCreateActivity.this, BeerCreateActivity.this.getString(R.string.beercreate_success), Toast.LENGTH_SHORT).show();
			BeerCreateActivity.this.beername.setText("");
			BeerCreateActivity.this.brand.setText("");
		}

	}

	/**
	 * Async task to get beertypes from server and update UI.
	 */
	private class GetBeertypeData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			Log.d(LOG_TAG, "onPreExecute()");
			BeerCreateActivity.this.progressDialog.display(BeerCreateActivity.this, true);
		}

		@Override
		protected JSONArray doInBackground(Void... voids) {
			Log.d(LOG_TAG, "doInBackground()");

			HttpResponse response = null;
			try {
				response = BeerCreateActivity.this.httpHelper.get(Res.getURI(Res.BEERTYPE_COLLECTION));
			} catch (IOException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.connectionError));
			}

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
					} catch (JSONException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Log.d(LOG_TAG, "onPostExecute()");
			if (isCancelled()) {
				Log.d(LOG_TAG, "cancel onPostExecute()");
				return;
			}
			if (result != null) {
				BeerCreateActivity.this.beertypeAdapter.updateData(result);
				BeerCreateActivity.this.beertypeAdapter.notifyDataSetChanged();
			}
			BeerCreateActivity.this.progressDialog.hide();
			BeerCreateActivity.this.allLoadingTasksFinished.countDown();
		}
	}

	/**
	 * Async task to get breweries from server and update UI.
	 */
	private class GetBreweryData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			Log.d(LOG_TAG, "onPreExecute()");
			BeerCreateActivity.this.progressDialog.display(BeerCreateActivity.this, true);
		}

		@Override
		protected JSONArray doInBackground(Void... voids) {
			Log.d(LOG_TAG, "doInBackground()");

			HttpResponse response = null;
			try {
				response = BeerCreateActivity.this.httpHelper.get(Res.getURI(Res.BREWERY_COLLECTION));
			} catch (IOException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.connectionError));
			}

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
					} catch (JSONException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Log.d(LOG_TAG, "onPostExecute()");
			if (isCancelled()) {
				Log.d(LOG_TAG, "cancel onPostExecute()");
				return;
			}
			if (result != null) {
				BeerCreateActivity.this.breweryAdapter.updateData(result);
				BeerCreateActivity.this.breweryAdapter.notifyDataSetChanged();
			}
			BeerCreateActivity.this.progressDialog.hide();
			BeerCreateActivity.this.allLoadingTasksFinished.countDown();
		}
	}

	/**
	 * Async task to get brands from server and add to the autocomplete list.
	 */
	private class GetBrandData extends AsyncTask<Void, Void, JSONArray> {
		@Override
		protected void onPreExecute() {
			Log.d(LOG_TAG, "onPreExecute()");
			BeerCreateActivity.this.progressDialog.display(BeerCreateActivity.this, true);
		}

		@Override
		protected JSONArray doInBackground(Void... voids) {
			Log.d(LOG_TAG, "doInBackground()");

			HttpResponse response = null;
			try {
				response = BeerCreateActivity.this.httpHelper.get(Res.getURI(Res.BRAND_COLLECTION));
			} catch (IOException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.connectionError));
			}

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
					} catch (JSONException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Log.d(LOG_TAG, "onPostExecute()");
			if (isCancelled()) {
				Log.d(LOG_TAG, "cancel onPostExecute()");
				return;
			}
			if (result != null) {
				final String[] brands = new String[result.length()];
				for (int i = 0; i < result.length(); i++) {
					try {
						brands[i] = result.getString(i);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				BeerCreateActivity.this.autoCompleteAdapter = new ArrayAdapter<String>(BeerCreateActivity.this, android.R.layout.simple_dropdown_item_1line, brands);
				BeerCreateActivity.this.brand.setAdapter(BeerCreateActivity.this.autoCompleteAdapter);
			}
			BeerCreateActivity.this.progressDialog.hide();
			BeerCreateActivity.this.allLoadingTasksFinished.countDown();
		}
	}

	/**
	 * Async task to get beer detail from server and update UI.
	 */
	private class GetBeerDetail extends AsyncTask<Void, Void, JSONObject> {

		private boolean showDialog = false;

		public GetBeerDetail() {
			super();
		}

		@Override
		protected void onPreExecute() {
			Log.d(LOG_TAG, "onPreExecute()");
			if (this.showDialog) {
				BeerCreateActivity.this.progressDialog.display(BeerCreateActivity.this, true);
			}
		}

		@Override
		protected JSONObject doInBackground(Void... voids) {
			final String uri = Res.getURI(Res.BEER_DOCUMENT, Long.toString(BeerCreateActivity.this.beerId));
			HttpResponse response = null;
			try {
				response = BeerCreateActivity.this.httpHelper.get(uri);
			} catch (IOException e) {
				Log.d(LOG_TAG, e.getMessage(), e);
				BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.connectionError));
			}

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);

						// wait for all other data loading tasks to be finished.
						BeerCreateActivity.this.allLoadingTasksFinished.await();
						return new JSONObject(responseText);
					} catch (IOException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.connectionError));
					} catch (JSONException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
					} catch (InterruptedException e) {
						Log.d(LOG_TAG, e.getMessage(), e);
						BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.connectionError));
					}
				} else {
					Log.e(LOG_TAG, "HTTP Response " + statusCode + " in GetBeerDetail::doInBackground");
				}
			}
			Log.e(LOG_TAG, "HTTP Response was null in GetBeerDetail::doInBackground");

			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			Log.d(LOG_TAG, "onPostExecute()");
			if (isCancelled()) {
				Log.d(LOG_TAG, "cancel onPostExecute()");
				return;
			}
			if (result != null) {
				try {

					// Set Name and brand
					BeerCreateActivity.this.beername.setText(result.getString("name"));
					BeerCreateActivity.this.brand.setText(result.getString("brand"));

					// Set Brewery in spinner
					final JSONObject resultBrewery = result.getJSONObject("brewery");
					final long breweryId = resultBrewery.optLong("id");
					final CreateBeerSpinnerAdapter breweryListAdapter = (CreateBeerSpinnerAdapter) BeerCreateActivity.this.brewerySpinner.getAdapter();
					final int breweryPosition = breweryListAdapter.getPositionOf(breweryId);
					BeerCreateActivity.this.brewerySpinner.setSelection(breweryPosition);

					// Set Beertype in spinner
					final JSONObject resultBeertype = result.getJSONObject("beertype");
					final CreateBeerSpinnerAdapter beertypeListAdapter = (CreateBeerSpinnerAdapter) BeerCreateActivity.this.beertypeSpinner.getAdapter();
					final long beertypeId = resultBeertype.optLong("id");
					final int beerTypePosition = beertypeListAdapter.getPositionOf(beertypeId);
					BeerCreateActivity.this.beertypeSpinner.setSelection(beerTypePosition);

				} catch (JSONException e) {
					Log.d(LOG_TAG, e.getMessage(), e);
					BeerCreateActivity.this.onError(BeerCreateActivity.this.getString(R.string.malformedData));
				}
			} else {
				Log.w(LOG_TAG, "Result was null in GetBeerDetail::onPostExecute");
			}
			if (this.showDialog) {
				BeerCreateActivity.this.progressDialog.hide();
			}
		}
	}

	private void onError(String message) {
		if (this.getBeerDetailTask != null) {
			this.getBeerDetailTask.cancel(true);
		}
		if (this.getBeertypeDataTask != null) {
			this.getBeertypeDataTask.cancel(true);
		}
		if (this.getBrandDataTask != null) {
			this.getBrandDataTask.cancel(true);
		}
		if (this.getBreweryDataTask != null) {
			this.getBreweryDataTask.cancel(true);
		}
		ErrorHelper.onError(message, this);
	}

}
