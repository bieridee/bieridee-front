package ch.hsr.bieridee.android.activities;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.Validators;
import ch.hsr.bieridee.android.adapters.CreateBeerSpinnerAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;

/**
 * Activity to create a new beer.
 * 
 */
public class BeerCreateActivity extends Activity {

	private static final String LOG_TAG = BreweryListActivity.class.getName();
	private CreateBeerSpinnerAdapter breweryAdapter;
	private CreateBeerSpinnerAdapter beertypeAdapter;
	private ArrayAdapter<String> autoCompleteAdapter;
	private HttpHelper httpHelper;
	private MultithreadProgressDialog progressDialog;
	private EditText beername;
	private AutoCompleteTextView brand;
	private Spinner brewerySpinner;
	private Spinner beertypeSpinner;
	private Button createButton;

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
		setButtonAction();
	}


	@Override
	public void onStart() {
		super.onStart();
		new GetBrandData().execute();
		new GetBeertypeData().execute();
		new GetBreweryData().execute();
	}
	
	private void setButtonAction() {
		this.createButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) {
				final String beername = BeerCreateActivity.this.beername.getText().toString().trim();	
				final String brand = BeerCreateActivity.this.brand.getText().toString().trim();
				final long beertypeId = BeerCreateActivity.this.beertypeSpinner.getSelectedItemId();
				final long breweryId = BeerCreateActivity.this.brewerySpinner.getSelectedItemId();
				
				if(Validators.validateNonEmpty(beername) && Validators.validateNonEmpty(brand)) {
					final JSONObject newBeer = new JSONObject();
					try {
						newBeer.put("name", beername);
						newBeer.put("brand", brand);
						newBeer.put("beertype", beertypeId);
						newBeer.put("brewery", breweryId);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.d(LOG_TAG, newBeer.toString());
					new BeerCreateActivity.AddNewBeer().execute(newBeer);
				} else {
					Toast.makeText(BeerCreateActivity.this, "Bitte alles ausfüllen", Toast.LENGTH_SHORT).show();
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

			final HttpResponse response = BeerCreateActivity.this.httpHelper.post(Res.getURI(Res.BEER_COLLECTION), params[0]);

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONObject(responseText);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			if(result != null) {
				Toast.makeText(BeerCreateActivity.this, "Bier wurde erfolgreich erstellt", Toast.LENGTH_SHORT).show();
				BeerCreateActivity.this.beername.setText("");	
				BeerCreateActivity.this.brand.setText("");
			} else {
				Toast.makeText(BeerCreateActivity.this, "whoa, fehler!", Toast.LENGTH_SHORT).show();
			}
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

			final HttpResponse response = BeerCreateActivity.this.httpHelper.get(Res.getURI(Res.BEERTYPE_COLLECTION));

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Log.d(LOG_TAG, "onPostExecute()");
			if (result != null) {
				BeerCreateActivity.this.beertypeAdapter.updateData(result);
				BeerCreateActivity.this.beertypeAdapter.notifyDataSetChanged();
			}
			BeerCreateActivity.this.progressDialog.hide();
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

			final HttpResponse response = BeerCreateActivity.this.httpHelper.get(Res.getURI(Res.BREWERY_COLLECTION));

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Log.d(LOG_TAG, "onPostExecute()");
			if (result != null) {
				BeerCreateActivity.this.breweryAdapter.updateData(result);
				BeerCreateActivity.this.breweryAdapter.notifyDataSetChanged();
			}
			BeerCreateActivity.this.progressDialog.hide();
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

			final HttpResponse response = BeerCreateActivity.this.httpHelper.get(Res.getURI(Res.BRAND_COLLECTION));

			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					try {
						final String responseText = new BasicResponseHandler().handleResponse(response);
						return new JSONArray(responseText);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			Log.d(LOG_TAG, "onPostExecute()");
			if (result != null) {
				final String[] brands = new String[result.length()];
				for(int i = 0; i < result.length(); i++) {
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
		}
	}

}
