package ch.hsr.bieridee.android.config;

/**
 * Resources.
 */
public final class Res {

	private Res() {
		// do not instantiate.
	}

	public static final String API_URL = "http://brauhaus.nusszipfel.com:8080";
	//public static final String API_URL = "http://152.96.233.221:8080";

	// beer
	public static final String BEER_COLLECTION = "/beers";
	public static final String BEER_REQ_ATTR = "beer-id";
	public static final String BEER_DOCUMENT = "/beers/{" + BEER_REQ_ATTR + "}";
	public static final String BEER_FILTER_PARAMETER_TAG = "tag";

	// beertype
	public static final String BEERTYPE_COLLECTION = "/beertypes";
	public static final String BEERTYPE_REQ_ATTR = "beertype-id";
	public static final String BEERTYPE_DOCUMENT = "/beertypes/{" + BEERTYPE_REQ_ATTR + "}";

	// brewery
	public static final String BREWERY_COLLECTION = "/breweries";
	public static final String BREWERY_REQ_ATTR = "brewery-id";
	public static final String BREWERY_DOCUMENT = "/breweries/{" + BREWERY_REQ_ATTR + "}";
	public static final String BREWERY_FILTER_PARAMETER_SIZE = "size";

	// tag
	public static final String TAG_COLLECTION = "/tags";
	public static final String TAG_REQ_ATTR = "tag-name";
	public static final String TAG_DOCUMENT = "/tags/{" + TAG_REQ_ATTR + "}";

	// user
	public static final String USER_COLLECTION = "/users";
	public static final String USER_REQ_ATTR = "user-name";
	public static final String USER_DOCUMENT = "/users/{" + USER_REQ_ATTR + "}";

	// usercredentials
	public static final String USERCREDENTIALS_CONTROLLER = "/usercredentials";

	// rating
	public static final String RATING_COLLECTION = "/ratings";
	public static final String RATING_DOCUMENT = BEER_DOCUMENT + RATING_COLLECTION + "/{" + USER_REQ_ATTR + "}";

	// consumption
	public static final String CONSUMPTION_COLLECTION = "/consumptions";
	public static final String CONSUMPTION_DOCUMENT = BEER_DOCUMENT + CONSUMPTION_COLLECTION + "/{" + USER_REQ_ATTR + "}";

	/**
	 * Returns the specified URI.
	 * 
	 * This is done by appending the resource URI to the base URI. If there are parameters in the URI, those are
	 * replaced using the provided attrs.
	 * 
	 * @param resource
	 *            Resource string (<code>Res</code> constants should be used).
	 * @param attrs
	 *            Arbitrary number of URI attribute replacements (should match the number of attributes marked with
	 *            curly braces).
	 * @return Processed URI string
	 */
	public static String getURI(final String resource, final String... attrs) {
		String parsedResource = resource;
		for (String attr : attrs) {
			parsedResource = parsedResource.replaceFirst("\\{[^\\}]*\\}", attr);
		}
		return API_URL + parsedResource;
	}
}