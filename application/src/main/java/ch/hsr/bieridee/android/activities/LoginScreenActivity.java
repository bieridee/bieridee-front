package ch.hsr.bieridee.android.activities;

import ch.hsr.bieridee.android.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Login Screen Activity.
 * 
 */
public class LoginScreenActivity extends Activity {

	// [section] Properties 
	private static final String LOGGINGTAG = "INFO";
	Button button;
	SharedPreferences settings;
	EditText tvUser;
	EditText tvPassword;
	CheckBox autologin;
	RelativeLayout wrongCredentailsHintLayout;
	TextView tvRegister;
	// [endSection]
	
	// [section] Lifecycle 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOGGINGTAG, "activity started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);

		this.autologin = (CheckBox) this.findViewById(R.id_loginscreen.checkboxAutologin);
		this.tvUser = (EditText) this.findViewById(R.id_loginscreen.inputUsername);
		this.tvPassword = (EditText) this.findViewById(R.id_loginscreen.inputPassword);
		this.button = (Button) this.findViewById(R.id_loginscreen.buttonLogin);
		this.wrongCredentailsHintLayout = (RelativeLayout) this.findViewById(R.id_loginscreen.relativeLayoutWrongLogin);
		this.tvRegister = (TextView) this.findViewById(R.id_loginscreen.registrationLink);

		this.settings = getSharedPreferences("settings", MODE_PRIVATE);
		this.readSettings();
		this.addLoginListener();
		this.addRegistrationListener();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		this.saveSettings();
		Log.d(LOGGINGTAG, "Application stopped, Settings saved");
	}
	
	// [endSection] 

	// [section] Listeners 
	
	/**
	 * Sets the registration click listener.
	 */
	private void addRegistrationListener() {
		this.tvRegister.setOnClickListener(new OnClickListener() {
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
		this.button.setOnClickListener(new OnClickListener() {
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
	
	// [endSection]

	// [section] Settings 
	
	/**
	 * Read user data from the shared settings.
	 */
	private void readSettings() {
		final String username = this.settings.getString("username", "");
		final String password = this.settings.getString("password", "");
		final boolean autologin = this.settings.getBoolean("autologin", false);

		this.autologin.setChecked(autologin);
		this.tvUser.setText(username);
		this.tvPassword.setText(password);
	}

	/**
	 * Save user data to the shared settings.
	 */
	private void saveSettings() {
		final SharedPreferences.Editor editor = this.settings.edit();
		
		final String newUsername = this.tvUser.getText().toString();
		final String newPassword = this.tvPassword.getText().toString();
		final boolean newAutologin = this.autologin.isChecked();
		
		editor.putString("username", newUsername);
		editor.putString("password", newPassword);
		editor.putBoolean("autologin", newAutologin);
		editor.commit();
	}

	/**
	 * Remove all saved settings from the shared settings editor.
	 */
	private void clearSettings() {
		final SharedPreferences.Editor editor = this.settings.edit();
		editor.clear();
		editor.commit();
	}
	
	// [endSection]
}