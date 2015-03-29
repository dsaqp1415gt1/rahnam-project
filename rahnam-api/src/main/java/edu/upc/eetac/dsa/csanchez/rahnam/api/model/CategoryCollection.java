package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryCollection {
	
	
	private List<Category> categories;

	public CategoryCollection() {
		super();
		categories = new ArrayList<>();
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}	
	
	public void addCategory(Category category) {
		categories.add(category);
	}


}
