package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

import java.util.ArrayList;
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


public class Photo {
	@InjectLinks({
		@InjectLink(resource=PhotoResource.class, style = Style.ABSOLUTE, rel="photos", title="photo collection", type = MediaType.MULTIPART_FORM_DATA),
		@InjectLink(resource = PhotoResource.class, style = Style.ABSOLUTE, rel = "photoscol", title = "Latest photos", type = MediaType2.RAHNAM_API_PHOTO_COLLECTION),
		@InjectLink(resource = PhotoResource.class, style = Style.ABSOLUTE, rel = "coments", title = "Coments", type = MediaType2.RAHNAM_API_COMMENT_COLLECTION, method = "getComments", bindings = @Binding(name = "photoid", value = "${instance.photoid}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel = "user", title = "PhotoAuthor", type = MediaType2.RAHNAM_API_USER, method = "getUser", bindings = @Binding(name = "username", value = "${instance.username}")),
		@InjectLink(resource = PhotoResource.class, style = Style.ABSOLUTE, rel = "self", title = "Photo", type = MediaType2.RAHNAM_API_PHOTO, method = "getPhoto", bindings = @Binding(name = "photoid", value = "${instance.photoid}")) })
	
	
	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	private String photoid;
	private String username;
	private String title;
	private String description;
	private long last_modified;
	private long creationTimestamp;
	private String filename;
	private String photoURL;
	

	public String getPhotoid() {
		return photoid;
	}
	public void setPhotoid(String photoid) {
		this.photoid = photoid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getLast_modified() {
		return last_modified;
	}
	public void setLast_modified(long last_modified) {
		this.last_modified = last_modified;
	}
	public long getCreationTimestamp() {
		return creationTimestamp;
	}
	public void setCreationTimestamp(long creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getPhotoURL() {
		return photoURL;
	}
	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	
	
	private List<Category> categories;
	
	public Photo(){
		super();
		categories = new ArrayList<>();
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	public void addCategory(Category category){
		categories.add(category);
	}
	
	
}
