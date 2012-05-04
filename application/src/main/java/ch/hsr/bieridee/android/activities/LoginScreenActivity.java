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
	private SharedPreferences settings;
	private EditText inputUsername;
	private EditText inputPassword;
	private CheckBox checkboxAutologin;
	private RelativeLayout wrongCredentailsHintLayout;
	private TextView registrationLink;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "activity started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);

		this.checkboxAutologin = (CheckBox) this.findViewById(R.id_loginscreen.checkboxAutologin);
		this.inputUsername = (EditText) this.findViewById(R.id_loginscreen.inputUsername);
		this.inputPassword = (EditText) this.findViewById(R.id_loginscreen.inputPassword);
		this.buttonLogin = (Button) this.findViewById(R.id_loginscreen.buttonLogin);
		this.wrongCredentailsHintLayout = (RelativeLayout) this.findViewById(R.id_loginscreen.relativeLayoutWrongLogin);
		this.registrationLink = (TextView) this.findViewById(R.id_loginscreen.registrationLink);

		this.readSettings();

		// If data has been passed on to the activity, load it
		final Bundle extras = this.getIntent().getExtras();
		if (extras != null && extras.containsKey("username") && extras.containsKey("password")) {
			this.inputUsername.setText(extras.getString("username"));
			this.inputPassword.setText(extras.getString("password"));
		}

		this.addLoginListener();
		this.addRegistrationListener();
		this.addAutologinListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.wrongCredentailsHintLayout.setVisibility(View.GONE);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (this.checkboxAutologin.isChecked()) {
			this.saveSettings();
		}
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
			public void onClick(View arg0) {
				Log.d("info", "Login Button was pressed");
				final boolean validCredentials = false; // TODO

				if (validCredentials) {
					LoginScreenActivity.this.wrongCredentailsHintLayout.setVisibility(View.GONE);
				} else {
					LoginScreenActivity.this.wrongCredentailsHintLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	/**
	 * Sets the autologin checkbox change listener.
	 * 
	 * If autologin checkbox is disabled, settings are cleared.
	 */
	private void addAutologinListener() {
		this.checkboxAutologin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					Auth.clearAuth();
				}
			}
		});
	}

	/**
	 * Read user data from the shared settings and update the UI elements accordingly.
	 */
	private void readSettings() {
		this.checkboxAutologin.setChecked(Auth.getAutologin());
		this.inputUsername.setText(Auth.getUsername());
		this.inputPassword.setText(Auth.getPassword());
	}

	/**
	 * Save user data to the shared settings.
	 */
	private void saveSettings() {
		final String newUsername = this.inputUsername.getText().toString();
		final String newPassword = this.inputPassword.getText().toString();
		final boolean newAutologin = this.checkboxAutologin.isChecked();
		Auth.setAuth(newUsername, newPassword, newAutologin);
	}
}
