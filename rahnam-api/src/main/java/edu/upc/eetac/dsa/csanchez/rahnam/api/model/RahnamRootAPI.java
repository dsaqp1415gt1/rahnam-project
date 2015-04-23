package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

import java.util.List;
import javax.ws.rs.core.Link;


public class RahnamRootAPI {
	
	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
}