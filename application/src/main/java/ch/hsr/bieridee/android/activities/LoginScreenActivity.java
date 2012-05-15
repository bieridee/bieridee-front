package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.config.Auth;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.exceptions.BierIdeeException;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Login Screen Activity.
 */
public class LoginScreenActivity extends Activity {

	private static final String LOG_TAG = LoginScreenActivity.class.getName();
	private Button buttonLogin;
	private EditText inputUsername;
	private EditText inputPassword;
	private TextView wrongCredentailsHintLayout;
	private TextView registrationLink;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);

		this.inputUsername = (EditText) this.findViewById(R.id_loginscreen.inputUsername);
		this.inputPassword = (EditText) this.findViewById(R.id_loginscreen.inputPassword);
		this.buttonLogin = (Button) this.findViewById(R.id_loginscreen.buttonLogin);
		this.wrongCredentailsHintLayout = (TextView) this.findViewById(R.id_loginscreen.wrongLoginHint);
		this.registrationLink = (TextView) this.findViewById(R.id_loginscreen.registrationLink);

		this.addLoginListener();
		this.addRegistrationListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.wrongCredentailsHintLayout.setVisibility(View.GONE);
	}

	/**
	 * Sets the registration click listener.
	 */
	private void addRegistrationListener() {
		this.registrationLink.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Intent intent = new Intent(v.getContext(), RegistrationScreenActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * Sets the login click listener.
	 */
	private void addLoginListener() {
		this.buttonLogin.setOnClickListener(new OnClickListener() {
			/**
			 * Save user data to the shared settings.
			 */
			private void saveSettings() {
				final String newUsername = LoginScreenActivity.this.inputUsername.getText().toString();
				final String newPassword = LoginScreenActivity.this.inputPassword.getText().toString();
				Auth.setAuth(newUsername, newPassword);
			}

			public void onClick(View v) {
				Log.d("info", "Login Button was pressed");

				final String username = LoginScreenActivity.this.inputUsername.getText().toString();
				final String password = LoginScreenActivity.this.inputPassword.getText().toString();

				if (!(username.isEmpty() || password.isEmpty())) {
					LoginScreenActivity.this.wrongCredentailsHintLayout.setVisibility(View.GONE);
					new VerifyLoginData().execute(username, password);
				} else {
					LoginScreenActivity.this.wrongCredentailsHintLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * Async task to verify login information.
	 * Expects username and hashed password strings as parameters.
	 */
	private class VerifyLoginData extends AsyncTask<String, Void, HttpResponse> {
		private String username;
		private String hashedPassword;

		@Override
		protected void onPreExecute() {
			LoginScreenActivity.this.progressDialog = ProgressDialog.show(
					LoginScreenActivity.this, getString(R.string.pleaseWait), getString(R.string.loginscreen_verifyingData), true);
		}

		@Override
		protected HttpResponse doInBackground(String... params) {
			// Process arguments
			if (params.length != 2) {
				throw new BierIdeeException("Invalid number of parameters to VerifyLoginData AsyncTask (2 expected, " + params.length + " given).");
			}

			// Set username, hash password
			this.username = params[0];
			this.hashedPassword = BCrypt.hashpw(params[1], BCrypt.gensalt());

			// Send HTTP request
			final HttpHelper httpHelper = AuthJsonHttp.create(this.username, this.hashedPassword);
			return httpHelper.post(Res.getURI(Res.USERCREDENTIALS_CONTROLLER));
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			LoginScreenActivity.this.progressDialog.dismiss();

			if (response == null) {
				throw new BierIdeeException("Response was null in VerifyLoginData");
			}

			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
				Toast.makeText(
						LoginScreenActivity.this,
						getString(R.string.loginscreen_loginFailed),
						Toast.LENGTH_LONG
				).show();
				Log.d(LOG_TAG, "Login failed, invalid credentials.");
			} else if (statusCode == HttpStatus.SC_NO_CONTENT) {
				// Show success message
				Toast.makeText(
						LoginScreenActivity.this.getApplicationContext(),
						getString(R.string.loginscreen_loginSuccess),
						Toast.LENGTH_SHORT
				).show();
				Log.d(LOG_TAG, "Login successful.");

				// Save data
				Auth.setAuth(this.username, this.hashedPassword);

				// Open homescreen activity
			   final Intent intent = new Intent(LoginScreenActivity.this.getBaseContext(), HomeScreenActivity.class);
			   startActivity(intent);
			} else {
				Toast.makeText(
						LoginScreenActivity.this.getApplicationContext(),
						getString(R.string.loginscreen_loginError),
						Toast.LENGTH_LONG
				).show();
				Log.e(LOG_TAG, "Unexpected return status (HTTP " + statusCode + ") in VerifyLoginData.");
			}
		}
	}
}
