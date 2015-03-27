package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

import java.util.ArrayList;
import java.util.List;

public class CommentCollection {
	
	private List<Comment> comments;

	public CommentCollection() {
		super();
		comments = new ArrayList<>();
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}


}
