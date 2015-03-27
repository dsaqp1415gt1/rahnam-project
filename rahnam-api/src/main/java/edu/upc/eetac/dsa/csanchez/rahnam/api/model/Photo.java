package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

public class Photo {
	
	private String photoid;
	private String username;
	private String file;
	private String name;
	private String description;
	private long last_modified;
	private long crationTimestamp;
	
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
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	
	
	
}
