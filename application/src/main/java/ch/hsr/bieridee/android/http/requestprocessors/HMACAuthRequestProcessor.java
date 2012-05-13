package ch.hsr.bieridee.android.http.requestprocessors;

import android.util.Log;
import ch.hsr.bieridee.android.config.Auth;
import ch.hsr.bieridee.android.exceptions.BierIdeeException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMACAuthRequestProcessor implements IRequestProcessor {

	private final static String LOG_TAG = "HMACAuth";

	public HttpRequestBase processRequest(HttpRequestBase request) {
		final String uri = request.getURI().toString();
		final String method = request.getMethod();
		final String accept = request.getFirstHeader("Accept").getValue();

		String body = "";
		String contentLength = "";

		if (request instanceof HttpEntityEnclosingRequestBase) {
			final HttpEntity entity = ((HttpEntityEnclosingRequestBase) request).getEntity();
			if (entity != null) {
				contentLength = request.getFirstHeader("Content-Length").getValue();
				final byte[] content = new byte[(int)entity.getContentLength()];
				try {
					((HttpEntityEnclosingRequestBase) request).getEntity().getContent().read(content);
				} catch (IOException e) {
					throw new BierIdeeException("Could not get body from " + request.getMethod().toUpperCase() + " request.");
				}
				body = new String(content);
			}
		}

		Log.d(LOG_TAG, "URI: " + uri);
		Log.d(LOG_TAG, "Method: " + method);
		Log.d(LOG_TAG, "Accept: " + accept);
		Log.d(LOG_TAG, "Content-Length: " + contentLength);
		Log.d(LOG_TAG, "Body: " + body);
		Log.d(LOG_TAG, "Processing HMAC Processor");

		final String hmacInputData = method + uri + accept + contentLength + body;
		String macString = "";
		try {
			final String password = Auth.getPassword();
			final SecretKey signingKey = new SecretKeySpec(password.getBytes(), "HmacSHA256");

			final Mac m = Mac.getInstance("HmacSHA256");
			m.init(signingKey);
			m.update(hmacInputData.getBytes());
			byte[] macBytes = m.doFinal();

			// Convert byte array to hex string
			for (byte b : macBytes) {
				macString += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new BierIdeeException("HmacSHA256 algorithm missing", e);
		} catch (InvalidKeyException e) {
			throw new BierIdeeException("Invalid key for HmacSHA256 signing", e);
		}

		if (macString.isEmpty()) {
			throw new BierIdeeException("Empty Authentication-HMAC");
		}
		final String authenticationHeader = Auth.getUsername() + ":" + macString;
		request.setHeader("Authentication", authenticationHeader);
		Log.d(LOG_TAG, "Setting Authentication header to \"" + authenticationHeader + "\".");
		return request;
	}
}
