package edu.upc.eetac.dsa.csanchez.rahnam.api;

public interface MediaType2 {
	public final static String RAHNAM_API_ERROR = "application/vnd.dsa.rahnam.error+json";

	public final static String RAHNAM_API_USER = "application/vnd.rahnam.api.user+json";
	public final static String RAHNAM_API_USER_COLLECTION = "application/vnd.rahnam.api.user.collection+json";
	
	public final static String RAHNAM_API_CATEGORY = "application/vnd.rahnam.api.category+json";
	public final static String RAHNAM_API_CATEGORY_COLLECTION = "application/vnd.rahnam.api.category.collection+json";
	
	public final static String RAHNAM_API_COMMENT = "application/vnd.rahnam.api.comment+json";
	public final static String RAHNAM_API_COMMENT_COLLECTION = "application/vnd.rahnam.api.comment.collection+json";
	
	public final static String RAHNAM_API_PHOTO = "application/vnd.rahnam.api.photo+json";
	public final static String RAHNAM_API_PHOTO_COLLECTION = "application/vnd.rahnam.api.photo.collection+json";
}