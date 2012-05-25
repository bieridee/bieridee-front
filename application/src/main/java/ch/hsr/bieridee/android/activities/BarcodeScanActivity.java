package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import ch.hsr.bieridee.android.adapters.BeerListAdapter;
import ch.hsr.bieridee.android.config.Res;
import ch.hsr.bieridee.android.http.AuthJsonHttp;
import ch.hsr.bieridee.android.http.HttpHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Activity that initiates a barcode scan.
 */
public class BarcodeScanActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcodescan);

		findViewById(R.id_barcodescan.button).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
			// TODO read javadoc of IntentIntegrator and implement methods like onPause...
			IntentIntegrator integrator = new IntentIntegrator(BarcodeScanActivity.this);
			integrator.initiateScan();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			Toast.makeText(this.getBaseContext(),
					"Scan result: " + scanResult.getContents() + "(" + scanResult.getFormatName() + ")",
					Toast.LENGTH_LONG).show();
		}
	}
}
