package ch.hsr.bieridee.android.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import ch.hsr.bieridee.android.BierideeApplication;
import ch.hsr.bieridee.android.exceptions.BierIdeeException;

/**
 * A helper class that simplifies HTTP requests. It supports request processors that can be added with
 * addRequestProcessor.
 */
public final class HttpHelper {
	private final Collection<IRequestProcessor> requestProcessors = new LinkedList<IRequestProcessor>();
	private final static String LOG_TAG = "HttpHelper";

	private final static int CONNECTION_TIMEOUT = 3000;
	private final static int SOCKET_TIMEOUT = 3000;
	private final static String USER_AGENT = "BierIdee Android v" + BierideeApplication.VERSION;

	/**
	 * Add a request processor.
	 * 
	 * @param requestProcessor
	 *            A subclass of IRequestProcessor
	 */
	public void addRequestProcessor(IRequestProcessor requestProcessor) {
		this.requestProcessors.add(requestProcessor);
	}

	/**
	 * Apply request processors.
	 * 
	 * @param getRequest
	 *            The HTTP request object
	 * @return The processed HTTP request object
	 */
	private HttpRequestBase applyRequestProcessors(HttpRequestBase getRequest) {
		for (IRequestProcessor requestProcessor : this.requestProcessors) {
			getRequest = requestProcessor.processRequest(getRequest);
		}
		return getRequest;
	}

	/**
	 * Perform a GET request.
	 * 
	 * @param uri
	 *            Full URI of the server resource
	 * @return A HttpResponse instance
	 * @throws IOException IOException
	 */
	public HttpResponse get(String uri) throws IOException {
		final HttpRequestBase request = new HttpGet(uri);
		return this.execute(this.applyRequestProcessors(request));
	}

	/**
	 * Perform a POST request without body data.
	 * 
	 * @param uri
	 *            Full URI of the server resource
	 * @return A HttpResponse instance
	 * @throws IOException IOException
	 */
	public HttpResponse post(String uri) throws IOException {
		return this.post(uri, null, null);
	}

	/**
	 * Perform a POST request with attached JSONObject entity.
	 * 
	 * @param uri
	 *            Full URI of the server resource
	 * @param data
	 *            A NameValuePair array containing
	 * @return A HttpResponse instance
	 * @throws IOException IOException
	 */
	public HttpResponse post(String uri, JSONObject data) throws IOException {
		try {
			final StringEntity stringEntity = new StringEntity(data.toString(2));
			return this.post(uri, stringEntity, "application/json");
		} catch (UnsupportedEncodingException e) {
			throw new BierIdeeException("Unsupported encoding for POST body data.", e);
		} catch (JSONException e) {
			throw new BierIdeeException("Could not properly decode JSON body data.", e);
		}
	}

	/**
	 * Perform a POST request with attached entity.
	 * 
	 * @param uri
	 *            Full URI of the server resource
	 * @param data
	 *            A NameValuePair array containing
	 * @param contentType
	 *            The Content-type string
	 * @return A HttpResponse instance
	 * @throws IOException IOException
	 */
	public HttpResponse post(String uri, AbstractHttpEntity data, String contentType) throws IOException {
		final HttpPost request = new HttpPost(uri);
		if (data != null) {
			request.setEntity(data);
		}
		if (contentType != null) {
			request.setHeader("Content-type", contentType);
		}
		return this.execute(this.applyRequestProcessors(request));
	}

	/**
	 * Perform a PUT request without body data.
	 * 
	 * @param uri
	 *            Full URI of the server resource
	 * @return A HttpResponse instance
	 * @throws IOException IOException
	 */
	public HttpResponse put(String uri) throws IOException {
		return this.put(uri, null, null);
	}

	/**
	 * Perform a PUT request with attached JSONObject entity.
	 * 
	 * @param uri
	 *            Full URI of the server resource
	 * @param data
	 *            A NameValuePair array containing
	 * @return A HttpResponse instance
	 * @throws IOException IOException
	 */
	public HttpResponse put(String uri, JSONObject data) throws IOException {
		try {
			final StringEntity stringEntity = new StringEntity(data.toString(2));
			return this.put(uri, stringEntity, "application/json");
		} catch (UnsupportedEncodingException e) {
			throw new BierIdeeException("Unsupported encoding for PUT body data.", e);
		} catch (JSONException e) {
			throw new BierIdeeException("Could not properly decode JSON body data.", e);
		}
	}

	/**
	 * Perform a PUT request with attached entity.
	 * 
	 * @param uri
	 *            Full URI of the server resource
	 * @param data
	 *            A NameValuePair array containing
	 * @param contentType
	 *            The Content-type string
	 * @return A HttpResponse instance
	 * @throws IOException IOException
	 */
	public HttpResponse put(String uri, AbstractHttpEntity data, String contentType) throws IOException {
		final HttpPut request = new HttpPut(uri);
		if (data != null) {
			request.setEntity(data);
		}
		if (contentType != null) {
			request.setHeader("Content-type", contentType);
		}
		return this.execute(this.applyRequestProcessors(request));
	}

	/**
	 * Perform a DELETE request.
	 * 
	 * @param uri
	 *            Full URI of the server resource
	 * @return A HttpResponse instance
	 * @throws IOException IOException
	 */
	public HttpResponse delete(String uri) throws IOException {
		final HttpRequestBase request = new HttpDelete(uri);
		return this.execute(this.applyRequestProcessors(request));
	}

	/**
	 * Actually perform a request.
	 * 
	 * @param request
	 *            HttpRequest to execute
	 * @return A HttpResponse instance
	 * @throws IOException IOException
	 */
	private HttpResponse execute(HttpRequestBase request) throws IOException {
		Log.d(LOG_TAG, request.getMethod() + " " + request.getURI());

		// Initialize HTTP parameters
		final HttpParams httpParameters = new BasicHttpParams();

		// Set timeout values
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);

		// Set useragent
		httpParameters.setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);

		// Initialize HttpClient with previously defined parameters
		final HttpClient client = new DefaultHttpClient(httpParameters);

		return client.execute(request);
	}
}
