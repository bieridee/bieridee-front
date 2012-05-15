package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.config.Auth;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "activity started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);

		this.inputUsername = (EditText) this.findViewById(R.id_loginscreen.inputUsername);
		this.inputPassword = (EditText) this.findViewById(R.id_loginscreen.inputPassword);
		this.buttonLogin = (Button) this.findViewById(R.id_loginscreen.buttonLogin);
		this.wrongCredentailsHintLayout = (TextView) this.findViewById(R.id_loginscreen.wrongLoginHint);
		this.registrationLink = (TextView) this.findViewById(R.id_loginscreen.registrationLink);

		this.readSettings();

		this.addLoginListener();
		this.addRegistrationListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.wrongCredentailsHintLayout.setVisibility(View.GONE);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(LOG_TAG, "Application stopped, Settings saved");
	}

	/**
	 * Sets the registration click listener.
	 */
	private void addRegistrationListener() {
		this.registrationLink.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d("info", "Register Link was pressed");
				final Intent intent = new Intent(v.getContext(), RegistrationScreenActivity.class);
				startActivityForResult(intent, 0);
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
				/*final boolean validCredentials = false; // TODO

				if (validCredentials) {
					LoginScreenActivity.this.wrongCredentailsHintLayout.setVisibility(View.GONE);
				} else {
					LoginScreenActivity.this.wrongCredentailsHintLayout.setVisibility(View.VISIBLE);
				}*/
				final String username = LoginScreenActivity.this.inputUsername.getText().toString();
				final String password = LoginScreenActivity.this.inputPassword.getText().toString();
				if (!(username.isEmpty() || password.isEmpty())) {
					this.saveSettings();

					final Intent intent = new Intent(v.getContext(), HomeScreenActivity.class);
					startActivity(intent);
				} else {
					LoginScreenActivity.this.wrongCredentailsHintLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	/**
	 * Read user data from the shared settings and update the UI elements accordingly.
	 */
	private void readSettings() {
		this.inputUsername.setText(Auth.getUsername());
		this.inputPassword.setText(Auth.getPassword());
	}
}
