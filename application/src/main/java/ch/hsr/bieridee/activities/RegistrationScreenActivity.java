package ch.hsr.bieridee.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import ch.hsr.bieridee.android.R;

public class RegistrationScreenActivity extends Activity {

	EditText inputPassword;
	EditText inputUsername;
	EditText inputEmail;
	Button buttonRegister;
	RelativeLayout usernameHint;
	RelativeLayout emailHint;
	RelativeLayout passwordHint;

	public void onCreate(Bundle savedInstanceState) {
		Log.d("info", "activity started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registrationscreen);

		this.inputEmail = (EditText) this.findViewById(R.id.registrationInputEmail);
		this.inputPassword = (EditText) this.findViewById(R.id.registrationInputPassword);
		this.inputUsername = (EditText) this.findViewById(R.id.registrationInputUsername);
		this.buttonRegister = (Button) this.findViewById(R.id.registrationButtonRegister);
		this.usernameHint = (RelativeLayout) this.findViewById(R.id.registrationRelativeLayoutUsernameHint);
		this.emailHint = (RelativeLayout) this.findViewById(R.id.registrationRelativeLayoutEmailHint);

		this.addRegisterOnClickListener();
	}

	public void addRegisterOnClickListener() {
		this.buttonRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				RegistrationScreenActivity.this.resetHints();
				boolean usernameIsValid = RegistrationScreenActivity.this.checkUsername(inputUsername.getText().toString());
				boolean emailIsValid = RegistrationScreenActivity.this.checkEmail(inputEmail.getText().toString());
				// boolean passwordIsValid =
				// RegistrationScreenActivity.this.checkPassword(inputPassword.getText().toString());

				boolean allValid = true;

				if (!usernameIsValid) {
					RegistrationScreenActivity.this.usernameHint.setVisibility(View.VISIBLE);
					Log.d("info", "username failure");
					allValid = false;
				}
				if (!emailIsValid) {
					RegistrationScreenActivity.this.emailHint.setVisibility(View.VISIBLE);
					allValid = false;
					Log.d("info", "email failure");
				}

				if (!allValid) {
					Log.d("info", "Somethings wrong in the form");
				}

			}
		});
	}

	private void resetHints() {
		Log.d("info", "Resetting Hints");
		this.emailHint.setVisibility(View.GONE);
		this.usernameHint.setVisibility(View.GONE);
	}

	private boolean checkUsername(String username) {
		return false;
	}

	private boolean checkEmail(String email) {
		return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	}

	private boolean checkPassword(String password) {
		return true;
	}
}
