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


public class Comment {
	
	@InjectLinks({
		@InjectLink(resource = PhotoResource.class, style = Style.ABSOLUTE, rel ="comments", title="comments list", type=MediaType2.RAHNAM_API_COMMENT ),
		@InjectLink(resource = PhotoResource.class, style = Style.ABSOLUTE, rel ="photo", title="coment's photo", type=MediaType2.RAHNAM_API_PHOTO, method = "getPhoto", bindings=@Binding(name ="photoid", value="${instance.photoid}")),
		@InjectLink(resource = UserResource.class, style = Style.ABSOLUTE, rel ="user", title="coment's user", type=MediaType2.RAHNAM_API_USER, method="getUser", bindings=@Binding(name ="username", value="${instance.username}"))
		})
	
	private List<Link> links; //lista de atributos
	 
	public List<Link> getLinks() {
		return links;
	}
 
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	private int commentid;
	private String username;
	private String photoid;
	private long last_modified;
	private long creationTimestamp;
	private String content;
	
	public int getCommentid() {
		return commentid;
	}
	public void setCommentid(int commentid) {
		this.commentid = commentid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPhotoid() {
		return photoid;
	}
	public void setPhotoid(String photoid) {
		this.photoid = photoid;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	

}
