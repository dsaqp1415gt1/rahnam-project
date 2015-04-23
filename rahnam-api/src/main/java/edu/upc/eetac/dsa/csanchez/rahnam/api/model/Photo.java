package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

public class Photo {
	
	private String photoid;
	private String username;
	private String title;
	private String description;
	private long last_modified;
	private long crationTimestamp;
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
	public long getCrationTimestamp() {
		return crationTimestamp;
	}
	public void setCrationTimestamp(long crationTimestamp) {
		this.crationTimestamp = crationTimestamp;
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

	
	
	
	
}
