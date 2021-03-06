package ch.hsr.bieridee.android.http;

import ch.hsr.bieridee.android.http.requestprocessors.AcceptRequestProcessor;
import ch.hsr.bieridee.android.http.requestprocessors.HMACAuthRequestProcessor;

/**
 * A factory that returns a default HttpHelper with support
 * for HMAC authentication and JSON accept header.
 */
public final class AuthJsonHttp {

	private AuthJsonHttp() {
		// Do not instantiate
	}

	/**
	 * Create a new HttpHelper instance, add the AcceptRequestProcessor
	 * and the HMACAuthRequestProcessor to it and return it.
	 * @return HttpHelper instance
	 */
	public static HttpHelper create() {
		final HttpHelper httpHelper = new HttpHelper();
		httpHelper.addRequestProcessor(new AcceptRequestProcessor(AcceptRequestProcessor.ContentType.JSON));
		httpHelper.addRequestProcessor(new HMACAuthRequestProcessor());
		return httpHelper;
	}

	/**
	 * Create a new HttpHelper instance, add the AcceptRequestProcessor
	 * and the HMACAuthRequestProcessor with cusotm login data to it
	 * and return it.
	 * @param username Username string
	 * @param password Password string
	 * @return HttpHelper instance
	 */
	public static HttpHelper create(String username, String password) {
		final HttpHelper httpHelper = new HttpHelper();
		httpHelper.addRequestProcessor(new AcceptRequestProcessor(AcceptRequestProcessor.ContentType.JSON));
		httpHelper.addRequestProcessor(new HMACAuthRequestProcessor(username, password));
		return httpHelper;
	}
}
