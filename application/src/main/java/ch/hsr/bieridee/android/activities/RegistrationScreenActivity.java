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
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

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
	private TextView prenameHint;
	private TextView surnameHint;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registrationscreen);

		// Inputfields
		this.inputEmail = (EditText) this.findViewById(R.id_registrationscreen.emailInput);
		this.inputPassword = (EditText) this.findViewById(R.id_registrationscreen.passwordInput);
		this.inputUsername = (EditText) this.findViewById(R.id_registrationscreen.usernameInput);
		this.inputPrename = (EditText) this.findViewById(R.id_registrationscreen.prenameInput);
		this.inputSurname = (EditText) this.findViewById(R.id_registrationscreen.surnameInput);

		// Buttons
		this.buttonRegister = (Button) this.findViewById(R.id_registrationscreen.registrationButton);

		// Hints
		this.usernameHint = (TextView) this.findViewById(R.id_registrationscreen.usernameHint);
		this.emailHint = (TextView) this.findViewById(R.id_registrationscreen.emailHint);
		this.prenameHint = (TextView) this.findViewById(R.id_registrationscreen.prenameHint);
		this.surnameHint = (TextView) this.findViewById(R.id_registrationscreen.surnameHint);

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
		private String cleartextPassword;
		private String hashedPassword;

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
			this.cleartextPassword = params[4];
			this.hashedPassword = BCrypt.hashpw(this.cleartextPassword, BCrypt.gensalt());

			final JSONObject user = new JSONObject();
			try {
				user.put("username", this.username);
				user.put("prename", params[1]);
				user.put("surname", params[2]);
				user.put("email", params[3]);
				user.put("password", this.hashedPassword);
			} catch (JSONException e) {
				throw new BierIdeeException("Creating the user JSON object failed.", e);
			}

			// Send HTTP request
			final HttpHelper httpHelper = new HttpHelper();
			httpHelper.addRequestProcessor(new AcceptRequestProcessor(AcceptRequestProcessor.ContentType.JSON));
			return httpHelper.put(Res.getURI(Res.USER_DOCUMENT, this.username), user);
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			RegistrationScreenActivity.this.progressDialog.dismiss();
			if (response != null) {
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_CREATED) {
					// Store auth data
					Auth.setAuth(this.username, this.hashedPassword);

					// Show success message
					Toast.makeText(
							RegistrationScreenActivity.this.getApplicationContext(),
							getString(R.string.registrationscreen_success_registration),
							Toast.LENGTH_SHORT
					).show();

					// Return to login activity
					final Intent intent = new Intent(RegistrationScreenActivity.this.getBaseContext(), LoginScreenActivity.class);
					intent.putExtra("username", this.username);
					intent.putExtra("password", this.cleartextPassword);
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
