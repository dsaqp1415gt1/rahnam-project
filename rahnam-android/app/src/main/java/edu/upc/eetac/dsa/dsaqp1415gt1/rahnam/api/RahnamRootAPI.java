package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api;

import java.util.HashMap;
import java.util.Map;

public class RahnamRootAPI {


    private Map<String, Link> links;

    public RahnamRootAPI() {
        links = new HashMap<String, Link>();
    }

    public Map<String, Link> getLinks() {
        return links;
    }

}