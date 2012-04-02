package ch.hsr.bieridee.android;

import java.util.List;

import ch.hsr.bieridee.domain.Beer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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
    	Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beerlist);
    }
    
    @Override
    public void onStart() {
    	Log.i(TAG, "onStart");
    	super.onStart();
    }
    
    @Override
    public void onPause() {
    	Log.i(TAG, "onPause");
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	Log.i(TAG, "onResume");
    	super.onResume();
    }
    
    private void populateList() {
    	BeerList bl = new BeerList();
    	List<Beer> list = bl.getJsonList();
    	Beer[] ba = (Beer[]) list.toArray();
    	ListView listview = (ListView) findViewById(R.id.beerListView);
    	listview.setAdapter(new ArrayAdapter<Beer>(this, R.layout.beerlist, ba));
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