package ch.hsr.bieridee.android;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ch.hsr.bieridee.android.config.Conf;

/**
 * Test class to retrive the beer list.
 * 
 * @author jfurrer
 *
 */
public class BeerList {
	/**
	 * Test method to get the beer list.
	 * @return Test list of beer-name strings
	 */
	public List<String> getBeerList() {
		final List<String> beerList= new LinkedList<String>();
		try {
			final ClientResource cr = new ClientResource(Conf.API_URL + "beers");
			final Representation rep = cr.get(MediaType.APPLICATION_JSON);
			
			final String json = rep.getText();
			final JSONArray jsonarray = new JSONArray(json);
			
			for(int i=0; i<jsonarray.length(); i++) {
				final JSONObject jo = (JSONObject) jsonarray.get(i);
				final String name = jo.getString("name");
				beerList.add(name);
			}
		} catch (ResourceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return beerList;
	}
	
}
