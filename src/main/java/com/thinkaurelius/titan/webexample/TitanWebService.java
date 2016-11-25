package com.thinkaurelius.titan.webexample;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * This class handles the HTTP requests.
 */
@Path("/")
@Component
public class TitanWebService {

    @Autowired
    GroovyGraphOp groovyOp;

    @Autowired
    JavaGraphOp operationHandeler;

    @PostConstruct
    private void init() {
        System.out.println("Initialized Titan Web Example Service");
    }

    @GET
    @Path("/getVerticesCount")
    @Produces(MediaType.TEXT_PLAIN)
    public String getVerticesCount(@Context UriInfo info) throws JSONException {
        String res = operationHandeler.getVerticesCount();
        return "\"" + res + "\"";
    }

    @GET
    @Path("/school/uf")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPersonBySchoolName(@Context UriInfo info) throws JSONException {
        String res = operationHandeler.getPersonByEdge("name", "UF", "studyIn");
        return "\"" + res + "\"";
    }

    @GET
    @Path("/interest/football")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPersonByInterestName(@Context UriInfo info) throws JSONException {
        String res = operationHandeler.getPersonByEdge("name", "football", "interestIn");
        return "\"" + res + "\"";
    }


    @GET
    @Path("/search/JoeToKobe")
    @Produces(MediaType.TEXT_PLAIN)
    public String shortestPath(@Context UriInfo info) throws JSONException {
        String res = operationHandeler.getDegree("Joe", "Kobe");
        return "\"" + res + "\"";
    }

    @GET
    @Path("/recommend/Joe")
    @Produces(MediaType.TEXT_PLAIN)
    public String getRecommend(@Context UriInfo info) throws JSONException {
        String res = operationHandeler.getRecommend("Joe");
        return "\"" + res + "\"";
    }

    /*
    @GET
    @Path("/rank/Joe")
    @Produces(MediaType.TEXT_PLAIN)
    public String listVertices(@Context UriInfo info) throws JSONException {
        String res = operationHandeler.listVertices();
        return "\"" + res + "\"";
    }
    */
    @GET
    @Path("/listVertices/name")
    @Produces(MediaType.TEXT_PLAIN)
    public String listVertices(@Context UriInfo info) throws JSONException {
        String res = operationHandeler.listVertices();
        return "\"" + res + "\"";
    }

    @GET
    @Path("/plutosBrothers")
    @Produces(MediaType.TEXT_PLAIN)
    public String pipeline(@Context UriInfo info) throws JSONException {
        String res = groovyOp.getPlutosBrothers();
        return "\"" + res + "\"";
    }
}
