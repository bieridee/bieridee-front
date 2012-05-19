package ch.hsr.bieridee.android.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import ch.hsr.bieridee.android.activities.AboutScreenActivity;
import ch.hsr.bieridee.android.activities.HomeScreenActivity;
import ch.hsr.bieridee.android.activities.LoginScreenActivity;
import ch.hsr.bieridee.android.config.Auth;
import com.jayway.android.robotium.solo.Solo;

/**
 * Test the home screen activity.
 */
public class HomeScreenActivityTest extends ActivityInstrumentationTestCase2<HomeScreenActivity> {

	private Solo solo;

	public HomeScreenActivityTest() {
		super(HomeScreenActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		this.solo.finishOpenedActivities();
		super.tearDown();
	}

	/**
	 * Destroy and restart current activity.
	 */
	private void restartActivity() {
		Intent testIntent = new Intent(getActivity().getBaseContext(), HomeScreenActivity.class);
		getActivity().finish();
		getActivity().startActivity(testIntent);
	}

	/**
	 * Clear authentication information and restart activity.
	 */
	private void testWithoutLogin() {
		Auth.clearAuth();
		this.restartActivity();
	}

	/**
	 * Set dummy authentication information and restart activity.
	 */
	private void testWithLogin() {
		Auth.setAuth("testuser", "$2$10$ae5deb822e0d719929004uD0KL0l5rHNCSFKcfBvoTzG5Og6O/Xxu");
		this.restartActivity();
	}

	/**
	 * Test basic parts of the activity.
	 */
	public void testDashboardItems() {
		this.testWithLogin();
		assertTrue(this.solo.searchText("Bierliste"));
		assertTrue(this.solo.searchText("Timeline"));
		assertTrue(this.solo.searchText("Brauereien"));
		assertTrue(this.solo.searchText("Profil"));
		assertTrue(this.solo.searchText("Bier erfassen"));
		assertTrue(this.solo.searchText("Empfehlungen"));
	}

	/**
	 * Should display the home screen activity.
	 */
	public void testLoggedIn() {
		this.testWithLogin();
		getInstrumentation().callActivityOnRestart(getActivity());
		assertTrue(this.solo.searchText("Bieridee Dashboard"));
		this.solo.assertCurrentActivity("Expected home screen activity to stay open.", HomeScreenActivity.class);
	}

	/**
	 * Should redirect to login view.
	 */
	public void testLoggedOut() {
		this.testWithoutLogin();
		getInstrumentation().callActivityOnRestart(getActivity());
		this.solo.assertCurrentActivity("Expected login activity to launch.", LoginScreenActivity.class);
	}

	/**
	 * Test whether the logout button works and launches the login activity.
	 */
	public void testLogoutButton() {
		this.testWithLogin();
		assertTrue("Expected auth data to be available.", Auth.dataAvailable());
		this.solo.assertCurrentActivity("Expected home screen activity to be active.", HomeScreenActivity.class);
		this.solo.sendKey(Solo.MENU);
		this.solo.clickOnText("Logout");
		assertFalse("Expected auth data to be gone.", Auth.dataAvailable());
		this.solo.assertCurrentActivity("Expected login activity to launch.", LoginScreenActivity.class);
	}

	/**
	 * Test whether the about button launches the about screen.
	 */
	public void testAboutButton() {
		this.testWithLogin();
		this.solo.assertCurrentActivity("Expected home screen activity to be active.", HomeScreenActivity.class);
		this.solo.sendKey(Solo.MENU);
		this.solo.clickOnText("Über");
		this.solo.assertCurrentActivity("Expected about screen activity to launch.", AboutScreenActivity.class);
	}
}
