package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.glassfish.jersey.linking.InjectLink.Style;

import edu.upc.eetac.dsa.csanchez.rahnam.api.MediaType2;
import edu.upc.eetac.dsa.csanchez.rahnam.api.PhotoResource;

public class User {
	
	@InjectLinks({
		@InjectLink(resource=PhotoResource.class, style = Style.ABSOLUTE, rel="photos", title="photo collection", type = MediaType.MULTIPART_FORM_DATA),
		@InjectLink(resource = PhotoResource.class, style = Style.ABSOLUTE, rel = "photoscol", title = "user's photos", type = MediaType2.RAHNAM_API_PHOTO_COLLECTION, method = "getPhotosByUser", bindings = @Binding(name = "username", value = "${instance.username}")),
	})
	private List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	private String username;
	private String userpass;
	private String name;
	private int avatar;
	private String email;
	private Date birth;
	private String gender;
	private boolean loginSuccessful;
	

	public boolean isLoginSuccessful() {
		return loginSuccessful;
	}
	public void setLoginSuccessful(boolean loginSuccessful) {
		this.loginSuccessful = loginSuccessful;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserpass() {
		return userpass;
	}
	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAvatar() {
		return avatar;
	}
	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

}
