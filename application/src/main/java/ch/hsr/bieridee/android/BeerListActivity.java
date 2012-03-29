package ch.hsr.bieridee.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Activity that shows a list of all beers in our database.
 */
public class BeerListActivity extends Activity {

    private static final String TAG = "bieridee-front";

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.beerlist);
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	final TextView beerListDescription = (TextView)findViewById(R.id.beerListDescription);
    	beerListDescription.setText(beerListDescription.getText() + " Programming Motherfucker!");
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	final TextView beerListDescription = (TextView)findViewById(R.id.beerListDescription);
    	beerListDescription.setText(beerListDescription.getText() + " Pausing.");
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	final TextView beerListDescription = (TextView)findViewById(R.id.beerListDescription);
    	beerListDescription.setText(beerListDescription.getText() + " Resuming.");
    }
    
    /**
     * View lifecycle:
     * - onCreate(Bundle savedInstanceState)
     * - onStart()
     * - onRestart()
     * - onResume()
     * - onPause()
     * - onStop()
     * - onDestroy()
     */

}