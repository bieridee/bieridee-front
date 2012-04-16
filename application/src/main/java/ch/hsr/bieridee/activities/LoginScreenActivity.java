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
import ch.hsr.bieridee.util.Hashhelper;

public class LoginScreenActivity extends Activity {
	/** Called when the activity is first created. */
	Button button;
	SharedPreferences settings;
	EditText tvUser;
	EditText tvPassword;
	CheckBox autologin;
	RelativeLayout wrongCredentails;
	TextView tvRegister;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("info", "activity started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);

		autologin = (CheckBox) this.findViewById(R.id.checkboxAutologin);
		tvUser = (EditText) this.findViewById(R.id.inputUsername);
		tvPassword = (EditText) this.findViewById(R.id.inputPassword);
		button = (Button) this.findViewById(R.id.buttonLogin);
		wrongCredentails = (RelativeLayout) this.findViewById(R.id.relativeLayoutWrongLogin);
		tvRegister = (TextView) this.findViewById(R.id.registrationLink);

		// Load Login information (if already stored)
		settings = getPreferences(MODE_PRIVATE);
		this.readSettings();
		this.addListenerOnButton();
		this.addListenerToRegisterLink();

	}

	public void addListenerToRegisterLink() {
		this.tvRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.d("info", "Register Link was pressed");
				Intent intent = new Intent(v.getContext(), RegistrationScreenActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	public void addListenerOnButton() {

		button.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Log.d("info", "Login Button was pressed");
				boolean validCredentails = false;
				if (validCredentails) {
					wrongCredentails.setVisibility(View.GONE);
				} else {
					wrongCredentails.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	// @Override
	// public void onStop() {
	// super.onStop();
	// this.saveSettings();
	// Log.d("info", "Application stopped");
	//
	// }

	public void readSettings() {
		String username = settings.getString("username", "");
		String password = settings.getString("password", "");
		boolean autologin = settings.getBoolean("autologin", false);

		this.autologin.setChecked(autologin);
		tvUser.setText(username);
		tvPassword.setText(password);
	}

	//
	public void saveSettings() {
		SharedPreferences.Editor editor = settings.edit();
		final String newUsername = tvUser.getText().toString();
		final String newPassword = tvPassword.getText().toString();
		final boolean newAutologin = autologin.isChecked();
		editor.putString("username", newUsername);
		editor.putString("password", Hashhelper.encryptPassword(newPassword));
		editor.putBoolean("autologin", newAutologin);
		editor.commit();
	}
}