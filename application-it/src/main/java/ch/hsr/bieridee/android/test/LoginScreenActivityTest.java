package ch.hsr.bieridee.android.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.CheckBox;
import android.widget.EditText;
import ch.hsr.bieridee.android.activities.LoginScreenActivity;

/**
 * Test the login screen activity.
 */
public class LoginScreenActivityTest extends ActivityInstrumentationTestCase2<LoginScreenActivity> {

	private LoginScreenActivity activity;

	@Override
	public void setUp() {
		this.activity = getActivity();
	}

	public LoginScreenActivityTest() {
		super(LoginScreenActivity.class);
	}

	public void testActivity() {
		assertNotNull(this.activity);
	}

	public void testSavingLoginInformation() {
		// Set test data
		final String testUsername = "testuser";
		final String testPassword = "testpass";

		// Initialize GUI elements
		final EditText usernameInputBefore;
		final EditText passwordInputBefore;

		final EditText usernameInputAfter;
		final EditText passwordInputAfter;

		usernameInputBefore = (EditText) this.activity.findViewById(ch.hsr.bieridee.android.R.id_loginscreen.inputUsername);
		passwordInputBefore = (EditText) this.activity.findViewById(ch.hsr.bieridee.android.R.id_loginscreen.inputPassword);

		// Set test data
		this.activity.runOnUiThread(new Runnable() {

			public void run() {
				usernameInputBefore.setText(testUsername);
				passwordInputBefore.setText(testPassword);
			}
		});

		// Restart activity
		this.activity.finish();
		this.activity = getActivity();
		getInstrumentation().waitForIdleSync();

		// Verify data (should have been stored)
		usernameInputAfter = (EditText) this.activity.findViewById(ch.hsr.bieridee.android.R.id_loginscreen.inputUsername);
		passwordInputAfter = (EditText) this.activity.findViewById(ch.hsr.bieridee.android.R.id_loginscreen.inputPassword);

		assertEquals(testUsername, usernameInputAfter.getText().toString());
		assertEquals(testPassword, passwordInputAfter.getText().toString());
	}
}