package ch.hsr.bieridee.android.activities;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.Validators;
import ch.hsr.bieridee.android.config.Conf;

/**
 * Activity with Registration Form.
 * 
 */
public class RegistrationScreenActivity extends Activity {

	private static final String LOG_TAG = "INFO";
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
	
	// [section] Lifecycle

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "activity started");
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

		this.fillTestValues();
		this.addRegisterOnClickListener();

	}
	
	// [endSection]

	private void addRegisterOnClickListener() {
		this.buttonRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				RegistrationScreenActivity.this.resetHints();
				final String username = RegistrationScreenActivity.this.inputUsername.getText().toString();
				final String surname = RegistrationScreenActivity.this.inputSurname.getText().toString();
				final String prename = RegistrationScreenActivity.this.inputPrename.getText().toString();
				final String email = RegistrationScreenActivity.this.inputEmail.getText().toString();
				final String password = RegistrationScreenActivity.this.inputPassword.getText().toString();

				boolean allValid = true;

				if (!Validators.validateUsername(username)) {
					RegistrationScreenActivity.this.usernameHint.setVisibility(View.VISIBLE);
					Log.d(LOG_TAG, "username invalid");
					allValid = false;
				}
				if (!Validators.validateEmail(email)) {
					RegistrationScreenActivity.this.emailHint.setVisibility(View.VISIBLE);
					allValid = false;
					Log.d(LOG_TAG, "email invalid");
				}

				if (!Validators.validateName(prename)) {
					RegistrationScreenActivity.this.prenameHint.setVisibility(View.VISIBLE);
					allValid = false;
					Log.d(LOG_TAG, "prename invalid");
				}
				if (!Validators.validateName(surname)) {
					RegistrationScreenActivity.this.surnameHint.setVisibility(View.VISIBLE);
					allValid = false;
					Log.d(LOG_TAG, "surname invalid");
				}

				if (!allValid) {
					Log.d(LOG_TAG, "Something went wrong in the form");
				} else {
					final ClientResource clientResource = new ClientResource(Conf.API_URL + "/users/" + username);
					clientResource.setRetryOnError(false);
					
					final JSONObject user = new JSONObject();
					try {
						user.put("username", username);
						user.put("prename", prename);
						user.put("surname", surname);
						user.put("email", email);
						user.put("password", password);
					} catch (JSONException e) {
						throw new RuntimeException("Creating the user JSON object failed.");
					}

					final Representation rep = new StringRepresentation(user.toString(), MediaType.APPLICATION_JSON);
					final String dialogTitle = getString(R.string.savingData);
					final String dialogMessage = getString(R.string.pleaseWait);
					final ProgressDialog dialog = ProgressDialog.show(RegistrationScreenActivity.this, dialogTitle, dialogMessage, true);

					createConnectionThread(clientResource, rep, dialog);
				}
			}

			/*
			 * Unfortunately the clientResource.setOnResponse Method doesnt not work! Therefore the nested solution.
			 */
			private void createConnectionThread(final ClientResource clientResource, final Representation rep, final ProgressDialog dialog) {
				new Thread(new Runnable() {
					public void run() {
						try {
							clientResource.put(rep);
						} catch (ResourceException e) {
							Log.d("info", "Connection timeout occured!");
						}
							runOnUiThread(new Runnable() {
								public void run() {
									dialog.dismiss();
									Log.d("status", clientResource.getStatus().toString());
									saveSettings();
								}

								private void saveSettings() {
									final SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
									final String username = RegistrationScreenActivity.this.inputUsername.getText().toString();
									final String password = RegistrationScreenActivity.this.inputPassword.getText().toString();
									Log.d("info", "username is: " + username);
									editor.putString("username", username);
									editor.putString("password", password);
									editor.commit();

									final Intent intent = new Intent(RegistrationScreenActivity.this.getBaseContext(), LoginScreenActivity.class);
									startActivityForResult(intent, 0);
								}
							});
						}
				}).start();
			}
		});
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
	
	/**
	 * Adds test data.
	 * @deprecated Can be removed later.
	 */
	@java.lang.Deprecated
	private void fillTestValues() {
		this.inputUsername.setText("ruedi");
		this.inputPrename.setText("Hans");
		this.inputSurname.setText("Rudin");
		this.inputEmail.setText("ruedi@hsr.ch");
		this.inputPassword.setText("naja");
	}
}
