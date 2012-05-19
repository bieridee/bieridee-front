package ch.hsr.bieridee.android.test;

import android.test.ActivityInstrumentationTestCase2;
import ch.hsr.bieridee.android.activities.BreweryListActivity;
import ch.hsr.bieridee.android.config.Auth;
import com.jayway.android.robotium.solo.Solo;

/**
 * Test the brewery list activity.
 */
public class BreweryListActivityTest extends ActivityInstrumentationTestCase2<BreweryListActivity> {

	private Solo solo;

	public BreweryListActivityTest() {
		super(BreweryListActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Auth.setAuth("testuser", "$2$10$ae5deb822e0d719929004uD0KL0l5rHNCSFKcfBvoTzG5Og6O/Xxu");
		this.solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		this.solo.finishOpenedActivities();
		super.tearDown();
	}

	/**
	 * Test activity content.
	 */
	public void testActivityContent() {
		assertTrue(this.solo.searchText("Brauereien")); // title
		assertTrue(this.solo.searchText("Brauerei Locher AG")); // brewery name
		assertTrue(this.solo.searchText("Die Brauerei Locher ist ein traditionsreicher Familienbetrieb in Appenzell.")); // description
	}
}
