package ch.hsr.bieridee.android.activities;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Uniform;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.Validators;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.ClientResourceFactory;

/**
 * Activity with Registration Form.
 * 
 */
public class RegistrationScreenActivity extends Activity {

	private static final String LOG_TAG = "RegistrationScreenActivity";
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

		this.addRegisterOnClickListener();
	}

	// [endSection]

	private void addRegisterOnClickListener() {
		this.buttonRegister.setOnClickListener(new View.OnClickListener() {

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

					// Do PUT request
					final ClientResource cr = ClientResourceFactory.getClientResource(Res.getURI(Res.USER_DOCUMENT, username));
					cr.setOnResponse(new Uniform() {
						public void handle(Request request, Response response) {
							Log.d(LOG_TAG, "PUT onResponse");
							dialog.dismiss();
							
							final int statusCode = response.getStatus().getCode();
							
							// If request was successful, show toast and pass data on to login activity.
							if (statusCode == HttpStatus.SC_CREATED) {
								Log.d(LOG_TAG, "Success! (HTTP " + response.getStatus().getCode() + ")");
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(RegistrationScreenActivity.this.getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
									}
								});
								
								final Intent intent = new Intent(RegistrationScreenActivity.this.getBaseContext(), LoginScreenActivity.class);
								intent.putExtra("username", username);
								intent.putExtra("password", password);
								startActivity(intent);
								
							// If request was unsuccessful, show error message.
							} else {
								final String fullHttpStatus = statusCode + " " + response.getStatus().getDescription();
								Log.e(LOG_TAG, "Registration failed. (HTTP " + fullHttpStatus + ")");
								runOnUiThread(new Runnable() {
									public void run() {
										new AlertDialog.Builder(RegistrationScreenActivity.this)
										.setTitle("Error")
										.setMessage("The registration has failed (HTTP " + statusCode + ").")
										.setNeutralButton("OK", new DialogInterface.OnClickListener() {
										    public void onClick(DialogInterface alertDialog, int which) {
										        alertDialog.dismiss();
										    }
										}).show();
									}
								});
							}
						}
					});
					cr.put(rep, MediaType.APPLICATION_JSON);
				}
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
}
