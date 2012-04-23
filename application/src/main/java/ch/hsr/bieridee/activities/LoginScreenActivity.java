package ch.hsr.bieridee.activities;

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
//import ch.hsr.bieridee.R;
import ch.hsr.bieridee.android.R;

/**
 * Login Screen Activity.
 * 
 */
public class LoginScreenActivity extends Activity {

	private static final String LOGGINGTAG = "INFO";
	Button button;
	SharedPreferences settings;
	EditText tvUser;
	EditText tvPassword;
	CheckBox autologin;
	RelativeLayout wrongCredentailsHintLayout;
	TextView tvRegister;

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
		this.addListenerOnButton();
		this.addListenerToRegisterLink();

	}

	private void addListenerToRegisterLink() {
		this.tvRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.d("info", "Register Link was pressed");
				final Intent intent = new Intent(v.getContext(), RegistrationScreenActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	private void addListenerOnButton() {

		this.button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Log.d("info", "Login Button was pressed");
				final boolean validCredentails = false;
				if (validCredentails) {
					LoginScreenActivity.this.wrongCredentailsHintLayout.setVisibility(View.GONE);
				} else {
					LoginScreenActivity.this.wrongCredentailsHintLayout.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
		this.saveSettings();
		Log.d(LOGGINGTAG, "Application stopped, Settings saved");

	}

	private void readSettings() {
		final String username = this.settings.getString("username", "");
		final String password = this.settings.getString("password", "");
		final boolean autologin = this.settings.getBoolean("autologin", false);

		this.autologin.setChecked(autologin);
		this.tvUser.setText(username);
		this.tvPassword.setText(password);
	}

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
}