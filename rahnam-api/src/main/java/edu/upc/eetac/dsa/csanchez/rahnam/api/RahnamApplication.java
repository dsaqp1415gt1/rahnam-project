package edu.upc.eetac.dsa.csanchez.rahnam.api;



import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
 
public class RahnamApplication extends ResourceConfig {
	public RahnamApplication() {
		super();
		register(DeclarativeLinkingFeature.class);
	}
}