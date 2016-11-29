package com.thinkaurelius.titan.webexample;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.*;

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
    @Path("/school/{schoolName}")
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody String getStudentBySchool(@PathParam("schoolName") String schoolName) throws JSONException{
        Map<String, String> nameMajorMap = operationHandeler.getPeopleInTheSameSchool(schoolName);
        JSONArray res = new JSONArray();
        for (Map.Entry entry : nameMajorMap.entrySet()) {
            JSONObject person = new JSONObject();
            person.put("name", entry.getKey());
            person.put("major", entry.getValue());
            res.put(person);
        }
        return res.toString();
    }

    @GET
    @Path("/major/{schoolName}/{majorName}")
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody String getStudentByMajorAndSchool(@PathParam("schoolName") String schoolName,
                                                           @PathParam("majorName") String majorName) throws JSONException {
        Map<String, String> nameInterestMap = operationHandeler.getPeopleWithSameMajor(majorName, schoolName);
        JSONArray res = new JSONArray();
        for (Map.Entry entry : nameInterestMap.entrySet()) {
            JSONObject person = new JSONObject();
            person.put("name", entry.getKey());
            person.put("interest", entry.getValue());
            res.put(person);
        }
        return res.toString();
    }

    @GET
    @Path("/interest/{schoolName}/{interestsNames}")
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody String getStudentByInterestAndSchool(@PathParam("schoolName") String schoolName,
                                                              @PathParam("interestsNames") String interestsNames) throws JSONException {
        Map<String, String> nameMajorMap = operationHandeler.getPeopleWithSameInterestsInSchool(interestsNames, schoolName);
        JSONArray res = new JSONArray();
        for (Map.Entry entry : nameMajorMap.entrySet()) {
            JSONObject person = new JSONObject();
            person.put("name", entry.getKey());
            person.put("major", entry.getValue());
            res.put(person);
        }
        return res.toString();
    }

    @GET
    @Path("/commonInterest/{sourceStudentName}/{targetStudentName}")
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody String getCommonInterestWith(@PathParam("sourceStudentName") String sourceName,
                                                      @PathParam("targetStudentName") String targetName) throws JSONException {
        List<String> commonInterestList = operationHandeler.findCommonInterestsWithSomeone(sourceName, targetName);

        JSONObject res = new JSONObject();
        res.put("name", new ArrayList<String>(commonInterestList));

        return res.toString();
    }

    @GET
    @Path("/findDegree/{FromStudentName}/{ToStudentName}")
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody String getDegree(@PathParam("FromStudentName") String fromName,
                                          @PathParam("ToStudentName") String toName) {
        List<String> res = operationHandeler.getShortestPathVersion2(fromName, toName);

        return res.toString();
    }

    @GET
    @Path("/getVerticesCount")
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody String getVerticesCount(@Context UriInfo info) throws JSONException {
        JSONObject res = new JSONObject();
        String num = operationHandeler.getVerticesCount();
        res.put("count", num);
        return res.toString();
    }

    @GET
    @Path("/friendRecommend/{name}/{friendNames}")
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody
    String getRecommand(@Context UriInfo info,
                                  @PathParam("name") String studentName,
                                  @PathParam("friendNames") String friendNames) throws JSONException {
        String friendsNames = friendNames.replace("_",", ");
        Map<String, Integer> mapRes = operationHandeler.getFriendsRecommend(studentName, friendsNames);

        JSONArray res = new JSONArray();

        for (Map.Entry entry : mapRes.entrySet()) {
            JSONObject tmp = new JSONObject();
            tmp.put("name", entry.getKey());
            tmp.put("common_num", entry.getValue());
            res.put(tmp);
        }

        return res.toString();
    }

    @POST
    @Path("/addStudent/{studentName}/{schoolName}/{interestNames}/{majorName}/{friendNames}")
    @Produces(MediaType.APPLICATION_JSON)
    public @ResponseBody String addStudent(@PathParam("studentName") String studentName,
                                           @PathParam("schoolName") String schoolName,
                                           @PathParam("interestNames") String interestName,
                                           @PathParam("majorName") String majorName,
                                           @PathParam("friendNames") String friendNames) throws JSONException {
//        List<String> notFoundName = operationHandeler.addStudent(
//                studentName,
//                schoolName,
//                interestName,
//                majorName,
//                friendNames
//                );
//        JSONArray results = new JSONArray();
//
//        for (String name : notFoundName) {
//            JSONObject person = new JSONObject();
//            person.put("not_found", name);
//            results.put(person);
//        }

        return "";
    }

//    @GET
//    @Path("/getVerticesCount")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getVerticesCount(@Context UriInfo info) throws JSONException {
//        String res = operationHandeler.getVerticesCount();
//        return "\"" + res + "\"";
//    }
//
//    @GET
//    @Path("/school/uf")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getPersonBySchoolName(@Context UriInfo info) throws JSONException {
//        String res = operationHandeler.getPersonByEdge("name", "UF", "studyIn");
//        return "\"" + res + "\"";
//    }
//
//    @GET
//    @Path("/interest/football")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getPersonByInterestName(@Context UriInfo info) throws JSONException {
//        String res = operationHandeler.getPersonByEdge("name", "football", "interestIn");
//        return "\"" + res + "\"";
//    }
//
//
////    @GET
////    @Path("/search/JoeToKobe")
////    @Produces(MediaType.TEXT_PLAIN)
////    public String shortestPath(@Context UriInfo info) throws JSONException {
////        String res = operationHandeler.getDegree("Joe", "Kobe");
////        return "\"" + res + "\"";
////    }
//
//    @GET
//    @Path("/recommend/Joe")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getRecommend(@Context UriInfo info) throws JSONException {
//        String res = operationHandeler.getRecommend("Joe");
//        return "\"" + res + "\"";
//    }
//
//    /*
//    @GET
//    @Path("/rank/Joe")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String listVertices(@Context UriInfo info) throws JSONException {
//        String res = operationHandeler.listVertices();
//        return "\"" + res + "\"";
//    }
//    */
//    @GET
//    @Path("/listVertices/name")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String listVertices(@Context UriInfo info) throws JSONException {
//        String res = operationHandeler.listVertices();
//        return "\"" + res + "\"";
//    }
//
//    @GET
//    @Path("/plutosBrothers")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String pipeline(@Context UriInfo info) throws JSONException {
//        String res = groovyOp.getPlutosBrothers();
//        return "\"" + res + "\"";
//    }
}
