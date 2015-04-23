package edu.upc.eetac.dsa.csanchez.rahnam.api.model;

public class RahnamError {
	private int status;
	private String message;
 
	public RahnamError() {
		super();
	}
 
	public RahnamError(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
 
	public int getStatus() {
		return status;
	}
 
	public void setStatus(int status) {
		this.status = status;
	}
 
	public String getMessage() {
		return message;
	}
 
	public void setMessage(String message) {
		this.message = message;
	}
}