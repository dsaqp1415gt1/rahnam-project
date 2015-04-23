package edu.upc.eetac.dsa.csanchez.rahnam.api;



import java.util.Enumeration;
import java.util.ResourceBundle;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
 
public class RahnamApplication extends ResourceConfig {
	public RahnamApplication() {
		super();
		register(DeclarativeLinkingFeature.class);
		register(MultiPartFeature.class);
		
		ResourceBundle bundle = ResourceBundle.getBundle("application");
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			property(key, bundle.getObject(key));
		}
	}
}