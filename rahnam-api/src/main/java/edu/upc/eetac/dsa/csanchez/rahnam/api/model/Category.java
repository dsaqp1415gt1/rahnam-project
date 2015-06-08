package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

import java.util.List;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.csanchez.rahnam.api.MediaType2;
import edu.upc.eetac.dsa.csanchez.rahnam.api.PhotoResource;
import edu.upc.eetac.dsa.csanchez.rahnam.api.UserResource;

public class Category {

	@InjectLinks({
		@InjectLink(resource = PhotoResource.class, style = Style.ABSOLUTE, rel = "categoryphotos", title = "photos", type = MediaType2.RAHNAM_API_PHOTO_COLLECTION, method = "getPhotosByCategory", bindings = @Binding(name = "category", value = "${instance.name}"))
 })
	
	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	
	
	
	private int categoryid;
	private String name;
	
	public int getCategoryid() {
		return categoryid;
	}
	public void setCategoryid(int categoryid) {
		this.categoryid = categoryid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
