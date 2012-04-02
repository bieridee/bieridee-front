package ch.hsr.bieridee.android;

import java.util.LinkedList;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ch.hsr.bieridee.android.config.Conf;
import ch.hsr.bieridee.domain.Beer;
import ch.hsr.bieridee.resourcehandler.interfaces.IBeerListResource;

public class BeerList {
	
	public BeerList() {
		
	}
	
	public static void main(String[] args) {
		new BeerList().getJsonList();
	}
	
	public List<Beer> getJsonList() {
		List<Beer> beerList= new LinkedList<Beer>();
		try {
			// Initialize the resource proxy.
			ClientResource cr = new ClientResource(Conf.API_URL + "beers");
			
			IBeerListResource resource = cr.wrap(IBeerListResource.class);

			// Get the remote contact
			 beerList = resource.retrieve();
			
			
		} catch (ResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return beerList;
	}
	
}
