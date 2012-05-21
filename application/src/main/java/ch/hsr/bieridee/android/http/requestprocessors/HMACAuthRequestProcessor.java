package ch.hsr.bieridee.android.http.requestprocessors;

import ch.hsr.bieridee.android.config.Auth;
import ch.hsr.bieridee.android.exceptions.BierIdeeException;
import ch.hsr.bieridee.android.http.IRequestProcessor;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpRequestBase;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Request processor to calculate a HMAC and add it to the Authorization header.
 */
public class HMACAuthRequestProcessor implements IRequestProcessor {

	private final static String LOG_TAG = "HMACAuth";
	private final String username;
	private final String password;

	/**
	 * Constructor that retrieves the username and password from the auth config file.
	 */
	public HMACAuthRequestProcessor()  {
		this.username = Auth.getUsername();
		this.password = Auth.getPassword();
		if (this.username.isEmpty() || this.password.isEmpty()) {
			throw new BierIdeeException("Could not retrieve username / password for signing the request.");
		}
	}

	/**
	 * Constructor that takes username and password as parameters.
	 * @param username Username string
	 * @param password Password string
	 */
	public HMACAuthRequestProcessor(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Process the request, add the HMAC to the Authorization header.
	 *
	 * @param request HTTP request object
	 * @return Processed HTTP request object
	 */
	public HttpRequestBase processRequest(HttpRequestBase request) {
		final String uri = request.getURI().toString();
		final String method = request.getMethod();
		final String accept = request.getFirstHeader("Accept").getValue();

		String body = "";
		String contentLength = "0";

		if (request instanceof HttpEntityEnclosingRequest) {
			final HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			if (entity != null) {
				contentLength = String.valueOf(entity.getContentLength());
//				final byte[] content = new byte[(int)entity.getContentLength()];
//				try {
//					((HttpEntityEnclosingRequestBase) request).getEntity().getContent().read(content);
//				} catch (IOException e) {
//					throw new BierIdeeException("Could not get body from " + request.getMethod().toUpperCase() + " request.");
//				}
//				body = new String(content);
			}
		}

		final String hmacInputData = method + uri + accept + contentLength + body;
		String macString;
		try {
			final Key signingKey = new SecretKeySpec(this.password.getBytes(), "HmacSHA256");

			final Mac m = Mac.getInstance("HmacSHA256");
			m.init(signingKey);
			m.update(hmacInputData.getBytes());
			final byte[] macBytes = m.doFinal();

			macString = new String(Hex.encodeHex(macBytes));
		} catch (NoSuchAlgorithmException e) {
			throw new BierIdeeException("HmacSHA256 algorithm missing", e);
		} catch (InvalidKeyException e) {
			throw new BierIdeeException("Invalid key for HmacSHA256 signing", e);
		}

		if (macString.isEmpty()) {
			throw new BierIdeeException("Empty Authorization-HMAC");
		}
		final String authorizationHeader = this.username + ":" + macString;
		request.setHeader("Authorization", authorizationHeader);

		return request;
	}
}
