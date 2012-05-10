package ch.hsr.bieridee.android.http;

import android.util.Log;
import ch.hsr.bieridee.android.exceptions.BierIdeeException;
import ch.hsr.bieridee.android.http.requestprocessors.IRequestProcessor;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * A helper class that simplifies HTTP requests.
 * It supports request processors that can be added with addRequestProcessor.
 */
public final class HttpHelper {
	private List<IRequestProcessor> requestProcessors = new LinkedList<IRequestProcessor>();
	private final static String LOG_TAG = "HttpHelper";

	/**
	 * Add a request processor.
	 * @param requestProcessor A subclass of IRequestProcessor
	 */
	public void addRequestProcessor(IRequestProcessor requestProcessor) {
		this.requestProcessors.add(requestProcessor);
	}

	/**
	 * Apply request processors.
	 * @param getRequest The HTTP request object
	 * @return The processed HTTP request object
	 */
	private HttpRequestBase applyRequestProcessors(HttpRequestBase getRequest) {
		for (IRequestProcessor requestProcessor : requestProcessors) {
			getRequest = requestProcessor.processRequest(getRequest);
		}
		return getRequest;
	}

	/**
	 * Perform a GET request.
	 * @param uri Full URI of the server resource
	 * @return A HttpResponse instance
	 */
	public HttpResponse get(String uri) {
		HttpRequestBase request = applyRequestProcessors(new HttpGet(uri));
		return this.execute(request);
	}

	/**
	 * Perform a POST request.
	 * @param uri Full URI of the server resource
	 * @return A HttpResponse instance
	 */
	public HttpResponse post(String uri) {
		HttpRequestBase request = applyRequestProcessors(new HttpPost(uri));
		return this.execute(request);
	}

	/**
	 * Perform a PUT request.
	 * @param uri Full URI of the server resource
	 * @return A HttpResponse instance
	 */
	public HttpResponse put(String uri) {
		HttpRequestBase request = applyRequestProcessors(new HttpPut(uri));
		return this.execute(request);
	}

	/**
	 * Perform a DELETE request.
	 * @param uri Full URI of the server resource
	 * @return A HttpResponse instance
	 */
	public HttpResponse delete(String uri) {
		HttpRequestBase request = applyRequestProcessors(new HttpDelete(uri));
		return this.execute(request);
	}

	/**
	 * Actually perform a request.
	 * @param request HttpRequest to execute
	 * @return A HttpResponse instance
	 */
	private HttpResponse execute(HttpRequestBase request) {
		Log.d(LOG_TAG, request.getMethod() + " " + request.getURI());

		final HttpClient client = new DefaultHttpClient();

		try {
			return client.execute(request);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BierIdeeException("IOException in " + request.getMethod().toUpperCase() + " request.", e);
		}
	}
}