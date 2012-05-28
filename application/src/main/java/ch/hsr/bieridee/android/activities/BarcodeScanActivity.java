package ch.hsr.bieridee.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import ch.hsr.bieridee.android.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
