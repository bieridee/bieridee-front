package ch.hsr.bieridee.android.test;

import android.test.ActivityInstrumentationTestCase2;
import ch.hsr.bieridee.android.activities.BeerListActivity;
import ch.hsr.bieridee.android.config.Auth;
import com.jayway.android.robotium.solo.Solo;

/**
 * Test the BeerList activity.
 */
public class BeerListActivityTest extends ActivityInstrumentationTestCase2<BeerListActivity> {

	private Solo solo;

	public BeerListActivityTest() {
		super(BeerListActivity.class);
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
		assertTrue(this.solo.searchText("Bierliste")); // title
		assertTrue(this.solo.searchText("Holzfass Bier")); // beer name
		assertTrue(this.solo.searchText("Appenzeller Bier")); // beer brand
	}
}
