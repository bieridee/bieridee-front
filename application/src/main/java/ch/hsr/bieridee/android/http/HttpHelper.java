package ch.hsr.bieridee.android.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
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
		HttpRequestBase getRequest = applyRequestProcessors(new HttpGet(uri));
		final HttpClient client = new DefaultHttpClient();
		try {
			return client.execute(getRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Perform a POST request.
	 * @param uri Full URI of the server resource
	 * @return A HttpResponse instance
	 */
	public HttpResponse post(String uri) {
		HttpRequestBase postRequest = applyRequestProcessors(new HttpPost(uri));
		final HttpClient client = new DefaultHttpClient();
		try {
			return client.execute(postRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Perform a PUT request.
	 * @param uri Full URI of the server resource
	 * @return A HttpResponse instance
	 */
	public HttpResponse put(String uri) {
		throw new RuntimeException("PUT not yet implemented!");
	}

	/**
	 * Perform a DELETE request.
	 * @param uri Full URI of the server resource
	 * @return A HttpResponse instance
	 */
	public HttpResponse delete(String uri) {
		throw new RuntimeException("DELETE not yet implemented!");
	}
}