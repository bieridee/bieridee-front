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

/**
 * Activity with Registration Form.
 * 
 */
public class RegistrationScreenActivity extends Activity {
	private static final String LOGGINGTAG = "INFO";

	EditText inputPassword;
	EditText inputUsername;
	EditText inputEmail;
	EditText inputPrename;
	EditText inputSurname;
	Button buttonRegister;
	RelativeLayout usernameHint;
	RelativeLayout emailHint;
	RelativeLayout passwordHint;
	RelativeLayout prenameHint;
	RelativeLayout surnameHint;

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * @param savedInstanceState @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOGGINGTAG, "activity started");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registrationscreen);

		this.inputEmail = (EditText) this.findViewById(R.id.registrationInputEmail);
		this.inputPassword = (EditText) this.findViewById(R.id.registrationInputPassword);
		this.inputUsername = (EditText) this.findViewById(R.id.registrationInputUsername);
		this.inputPrename = (EditText) this.findViewById(R.id.registrationInputPrename);
		this.inputSurname = (EditText) this.findViewById(R.id.registrationInputSurname);

		this.buttonRegister = (Button) this.findViewById(R.id.registrationButtonRegister);

		this.usernameHint = (RelativeLayout) this.findViewById(R.id.registrationRelativeLayoutUsernameHint);
		this.emailHint = (RelativeLayout) this.findViewById(R.id.registrationRelativeLayoutEmailHint);
		this.prenameHint = (RelativeLayout) this.findViewById(R.id.registrationRelativeLayoutPrenameHint);
		this.surnameHint = (RelativeLayout) this.findViewById(R.id.registrationRelativeLayoutSurnameHint);

		this.addRegisterOnClickListener();
	}

	/**
	 * 
	 */
	private void addRegisterOnClickListener() {
		this.buttonRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				RegistrationScreenActivity.this.resetHints();
				final boolean usernameIsValid = RegistrationScreenActivity.this.checkUsername(RegistrationScreenActivity.this.inputUsername.getText().toString());
				final boolean emailIsValid = RegistrationScreenActivity.this.checkEmail(RegistrationScreenActivity.this.inputEmail.getText().toString());
				final boolean prenameIsValid = RegistrationScreenActivity.this.checkName(RegistrationScreenActivity.this.inputPrename.getText().toString());
				final boolean surnameIsValid = RegistrationScreenActivity.this.checkName(RegistrationScreenActivity.this.inputSurname.getText().toString());

				boolean allValid = true;

				if (!usernameIsValid) {
					RegistrationScreenActivity.this.usernameHint.setVisibility(View.VISIBLE);
					Log.d(LOGGINGTAG, "username invalid");
					allValid = false;
				}
				if (!emailIsValid) {
					RegistrationScreenActivity.this.emailHint.setVisibility(View.VISIBLE);
					allValid = false;
					Log.d(LOGGINGTAG, "email invalid");
				}

				if (!prenameIsValid) {
					RegistrationScreenActivity.this.prenameHint.setVisibility(View.VISIBLE);
					allValid = false;
					Log.d(LOGGINGTAG, "prename invalid");
				}
				if (!surnameIsValid) {
					RegistrationScreenActivity.this.surnameHint.setVisibility(View.VISIBLE);
					allValid = false;
					Log.d(LOGGINGTAG, "surname invalid");
				}

				if (!allValid) {
					Log.d(LOGGINGTAG, "Something went wrong in the form");
				}

			}
		});
	}

	private void resetHints() {
		Log.d(LOGGINGTAG, "Resetting Hints");
		this.emailHint.setVisibility(View.GONE);
		this.usernameHint.setVisibility(View.GONE);
		this.prenameHint.setVisibility(View.GONE);
		this.surnameHint.setVisibility(View.GONE);
	}

	private boolean checkName(String name) {
		return name.matches("\\w{3,}");
	}

	private boolean checkUsername(String username) {
		return username.matches("\\w{3,}");
	}

	private boolean checkEmail(String email) {
		return email.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	}
}
