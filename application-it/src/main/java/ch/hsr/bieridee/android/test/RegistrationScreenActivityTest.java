package ch.hsr.bieridee.android.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import ch.hsr.bieridee.android.activities.RegistrationScreenActivity;

/**
 * Test the registration screen.
 */
public class RegistrationScreenActivityTest extends ActivityInstrumentationTestCase2<RegistrationScreenActivity> {

	public RegistrationScreenActivityTest() {
		super(RegistrationScreenActivity.class);
	}

	/**
	 * Test whether the activity launches.
	 */
	public void testActivity() {
		Activity activity = getActivity();
		assertNotNull(activity);
	}
}