package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.csanchez.rahnam.api.model.User;

public class UserCollection {
	
	private List<User> users;

	public UserCollection() {
		super();
		users = new ArrayList<>();
	}	
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public void addUser(User user) {
		users.add(user);
	}

	

}
