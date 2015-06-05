package edu.upc.eetac.dsa.dsaqp1415gt1.rahnam.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cristina on 29/05/2015.
 */
public class Category {

    private int categoryid;
    private String name;
    private String ETag;
    private Map<String, Link> links = new HashMap<String, Link>();


    public int getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(int categoryid) {
        this.categoryid = categoryid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }

    public Map<String, Link> getLinks() {
        return links;

   }

}



