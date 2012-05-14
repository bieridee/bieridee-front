package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.Validators;
import ch.hsr.bieridee.android.config.Auth;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.exceptions.BierIdeeException;
import ch.hsr.bieridee.android.http.HttpHelper;
import ch.hsr.bieridee.android.http.requestprocessors.AcceptRequestProcessor;
import ch.hsr.bieridee.android.http.requestprocessors.HMACAuthRequestProcessor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity with Registration Form.
 */
public class RegistrationScreenActivity extends Activity {

	private static final String LOG_TAG = RegistrationScreenActivity.class.getName();
	private EditText inputPassword;
	private EditText inputUsername;
	private EditText inputEmail;
	private EditText inputPrename;
	private EditText inputSurname;
	private Button buttonRegister;
	private TextView usernameHint;
	private TextView emailHint;
	private TextView passwordHint;
	private TextView prenameHint;
	private TextView surnameHint;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registrationscreen);

		this.inputEmail = (EditText) this.findViewById(R.id.registrationInputEmail);
		this.inputPassword = (EditText) this.findViewById(R.id.registrationInputPassword);
		this.inputUsername = (EditText) this.findViewById(R.id.registrationInputUsername);
		this.inputPrename = (EditText) this.findViewById(R.id.registrationInputPrename);
		this.inputSurname = (EditText) this.findViewById(R.id.registrationInputSurname);

		this.buttonRegister = (Button) this.findViewById(R.id.registrationButtonRegister);

		this.usernameHint = (TextView) this.findViewById(R.id.registrationUsernameHint);
		this.emailHint = (TextView) this.findViewById(R.id.registrationEmailHint);
		this.prenameHint = (TextView) this.findViewById(R.id.registrationPrenameHint);
		this.surnameHint = (TextView) this.findViewById(R.id.registrationSurnameHint);

		this.addRegisterOnClickListener();
	}

	private void addRegisterOnClickListener() {
		this.buttonRegister.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				RegistrationScreenActivity.this.resetHints();
				final String username = RegistrationScreenActivity.this.inputUsername.getText().toString().trim();
				final String surname = RegistrationScreenActivity.this.inputSurname.getText().toString().trim();
				final String prename = RegistrationScreenActivity.this.inputPrename.getText().toString().trim();
				final String email = RegistrationScreenActivity.this.inputEmail.getText().toString().trim();
				final String password = RegistrationScreenActivity.this.inputPassword.getText().toString().trim();

				boolean allValid = true;

				if (!Validators.validateUsername(username)) {
					RegistrationScreenActivity.this.usernameHint.setVisibility(View.VISIBLE);
					allValid = false;
				}
				if (!Validators.validateEmail(email)) {
					RegistrationScreenActivity.this.emailHint.setVisibility(View.VISIBLE);
					allValid = false;
				}

				if (!Validators.validateName(prename)) {
					RegistrationScreenActivity.this.prenameHint.setVisibility(View.VISIBLE);
					allValid = false;
				}
				if (!Validators.validateName(surname)) {
					RegistrationScreenActivity.this.surnameHint.setVisibility(View.VISIBLE);
					allValid = false;
				}

				if (allValid) {
					new Register().execute(username, prename, surname, email, password);
				}
			}
		});
	}

	/**
	 * Async task to send registration HTTP request.
	 * <code>execute()</code> expects the following parameters (order relevant!):
	 * username, prename, surname, email, password
	 */
	private class Register extends AsyncTask<String, Void, HttpResponse> {
		private String username;
		private String password;

		@Override
		protected void onPreExecute() {
			RegistrationScreenActivity.this.progressDialog = ProgressDialog.show(
					RegistrationScreenActivity.this, getString(R.string.pleaseWait), getString(R.string.loadingData), true);
		}
		@Override
		protected HttpResponse doInBackground(String... params) {
			if (params.length != 5) {
				throw new BierIdeeException("Invalid number of parameters to Register AsyncTask (5 expected, " + params.length + " given).");
			}
			this.username = params[0];
			this.password = params[4];

			final JSONObject user = new JSONObject();
			try {
				user.put("username", this.username);
				user.put("prename", params[1]);
				user.put("surname", params[2]);
				user.put("email", params[3]);
				user.put("password", this.username);
			} catch (JSONException e) {
				throw new BierIdeeException("Creating the user JSON object failed.", e);
			}

			// We need to pass login data directly to the HMACAuthRequestProcessor
			final HttpHelper httpHelper = new HttpHelper();
			httpHelper.addRequestProcessor(new AcceptRequestProcessor(AcceptRequestProcessor.ContentType.JSON));
			httpHelper.addRequestProcessor(new HMACAuthRequestProcessor(this.username, this.password));

			// Send HTTP request
			return httpHelper.put(Res.getURI(Res.USER_DOCUMENT, this.username), user);
		}
		@Override
		protected void onPostExecute(HttpResponse response) {
			RegistrationScreenActivity.this.progressDialog.dismiss();
			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_NO_CONTENT) {
					// Store auth data
					Auth.setAuth(this.username, this.password, true);

					// Show success message
					Toast.makeText(RegistrationScreenActivity.this.getApplicationContext(), getString(R.string.registrationscreen_success_registration), Toast.LENGTH_SHORT).show();

					// Return to login activity
					final Intent intent = new Intent(RegistrationScreenActivity.this.getBaseContext(), LoginScreenActivity.class);
					startActivity(intent);
					return;
				}
				Log.e(LOG_TAG, "Registration failed with HTTP status code " + statusCode);
			}

			// Show error message
			Toast.makeText(RegistrationScreenActivity.this.getApplicationContext(), getString(R.string.registrationscreen_fail_registration), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Hide all hints.
	 */
	private void resetHints() {
		Log.d(LOG_TAG, "Resetting Hints");
		this.emailHint.setVisibility(View.GONE);
		this.usernameHint.setVisibility(View.GONE);
		this.prenameHint.setVisibility(View.GONE);
		this.surnameHint.setVisibility(View.GONE);
	}
}
