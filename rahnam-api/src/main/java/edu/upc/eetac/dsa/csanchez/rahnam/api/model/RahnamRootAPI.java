package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

import java.util.List;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.csanchez.rahnam.api.MediaType2;
import edu.upc.eetac.dsa.csanchez.rahnam.api.PhotoResource;
import edu.upc.eetac.dsa.csanchez.rahnam.api.RahnamRootAPIResource;


public class RahnamRootAPI {
	@InjectLinks({
		@InjectLink(resource = RahnamRootAPIResource.class, style = Style.ABSOLUTE, rel = "self", title = "Beeter Root API", method = "getRootAPI"),
		@InjectLink(resource = PhotoResource.class, style = Style.ABSOLUTE, rel ="photos", title="lista de photos", type=MediaType.MULTIPART_FORM_DATA ),
		@InjectLink(resource = PhotoResource.class, style = Style.ABSOLUTE, rel = "collection", title = "Latest photos", type = MediaType2.RAHNAM_API_PHOTO_COLLECTION)})

	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
}