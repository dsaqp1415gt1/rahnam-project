package edu.upc.eetac.dsa.csanchez.rahnam.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
 
import edu.upc.eetac.dsa.csanchez.rahnam.api.model.RahnamRootAPI;
 
@Path("/")
public class RahnamRootAPIResource {
	@GET
	public RahnamRootAPI getRootAPI() {
		RahnamRootAPI api = new RahnamRootAPI();
		return api;
	}
}