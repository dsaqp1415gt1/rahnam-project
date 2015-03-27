package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

import java.util.ArrayList;
import java.util.List;

public class PhotoCollection {
	
	private List<Photo> photos;

	public PhotoCollection() {
		super();
		photos = new ArrayList<>();
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}	
	
	public void addPhoto(Photo photo) {
		photos.add(photo);
	}
	

}
