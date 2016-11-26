package com.thinkaurelius.titan.webexample;

import com.thinkaurelius.titan.graphdb.vertices.CacheVertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class JavaGraphOp {
    /**
     * TODO: convert all these types into different ENUM type class and reconstruct the code.
     *
     * Define different types to identify different tpyes of nodes we have in the graph.
     */
    private static final String PERSON = "person";
    private static final String SCHOOL = "school";
    private static final String INTEREST = "interest";
    private static final String ASSOCIATION = "associations";
    private static final String MAJOR = "major";

    /**
     * TODO: convert all these types into different ENUM type class and reconstruct the code.
     *
     * Define possible edge labels we will need in our social network graph.
     */
    private static final String KNOWS = "knows"; /* Indicates friends */
    private static final String ATTENDS = "attends"; /* The school student attends */
    private static final String STUDIES = "studies"; /* The major for each student */
    private static final String LIKES = "likesPlaying"; /* Some hobbies they interested in */
    private static final String JOINED = "joined"; /* Associations or clubs */
    private static final String HAS_STUDENT = "hasStudent";
    private static final String HAS_MEMBER = "hasMember";
    private static final String HAS_PEOPLE_INTERESTEDIN = "has";
    private static final String HAS_STUDENT_STUDY = "has";

    private static final int MAX_LOOP_STEPS = 10; /* What is the maximum steps we allowed in a loop? */
    // Autowired via setter. I leave this as a blueprints.Graph unless I have to do Titan specific stuff.
    private Graph graph;

    @Autowired
    // I like to autowire via setters because it makes it easy to write spring-free unit tests.
    public void setGraph(TitanGraphFactory gf) {
        this.graph = gf.getGraph();
    }

    /**
     * When a new user input his data into the system, we should firstly insert
     * a new node representing this user, and connect to other node as necessay.
     */
    public void addNewUserToGraph() {
        // TODO: Discuss the input format with Joe and Thomas, and implement this method.
    }

    /**
     * Query: Find all people who goes to the same school with the user.
     * @param school Name of the school the user goes to
     * @return List of people who also attend to the same school
     */
    public List<String> getPeopleInTheSameSchool(String school) {
        if (school == null || school.length() == 0) {
            return "";
        }

        Vertex schoolVertex = graph.getVertex(SCHOOL, school).next();
        if (schoolVertex == null) {
            throw new RequiredInfoNotFoundException("Looking for school " + school +
                    ", but did not found existing information in our database :(");
        }
        return schoolVertex.outE(HAS_STUDENT).outV().toList();
    }

    /**
     * Query: Find all students who attends the same school with the user and
     *        the same major
     * @return List of student names.
     */
    public List<String> getPeopleWithSameMajor(String major, String school) {
        if (major == null || major.length() == 0 || school == null || school.length() == 0) {
            return "";
        }

        Vertex schoolVertex = graph.getVertex(SCHOOL, school).next();
        if (schoolVertex == null) {
            return "";
        }

        Vertex majorVertex = graph.getVertex(MAJOR, major).next();
        return schoolVertex.outV().as('x').has("name", major).back('x').toList();
    }

    /**
     * Query: Find all students in the same school who share common interests with me.
     * Similar to #getPeopleWithSameMajor,
     *
     * TODO: when people input multiple interests, what is the data format we get back
     *       from the front end part?
     *       What is the proper return type for this funtion? (I think a map would be better?)
     *       Here I assume the interests would a string separated by ','.
     */
    public List<String> getPeopleWithSameInterestsInSchool(String interests, String school) {
        if (interests == null || interests.length() == 0 || school == null || school.length() == 0) {
            return "";
        }

        Vertex schoolVertex = graph.getVertex(SCHOOL, school).next();
        if (schoolVertex == null) {
            return "";
        }

        String[] allInterests = interests.splite(", ");
        Set<String> interestsSet = new HashSet<>();
        for (String element : allInterests) {
            interestsSet.add(element);
        }

        /*
         * We firstly get back all students that attends the same school, then check one
         * by one whether they share same interests or not.
         */
        // TODO: we should be more specific about this, e.g.: A --> basketball, B --> football.
        List<String> result = new ArrayList<>();
        List<Vertex> studentsInSameSchool = graph.getVertex(SCHOOL, school).outE(HAS_STUDENT).inV().toList();
        for (Vertex studentVertex : studentsInSameSchool) {
            for (Vertex interestVertex : studentVertex.outE(LIKES).inV().toList()) {
                if (interestsSet.contains(interestVertex.property("name").value())) {
                    result.add(studentVertex.property("name").value());
                    break; /* Go check for next possible person so that we will not have duplicates */
                }
            }
        }

        return result;
    }

    /**
     * Query: Do I share any common interests with someone I wish to know?
     * @param targetPerson Name of the person I wish to know
     * @return List of interests that we share together
     * TODO: what is the format of the input for the interests? List of Strings? Or just strings
     */
    public List<String> findCommonInterestsWithSomeone(String myName, String allMyInterests, String targetPerson) {
        if (targetPerson == null || targetPerson.length() == 0) {
            return "";
        }

        /* Check if this people is in our system */
        Vertex targetVertex = graph.getVertex("name", targetPerson).next();
        if (targetVertex == null) {
            throw new RequiredVertexNotFoundException("Expect to find user:" + targetPerson + " in our system but does not exist.");
        }

        /* Use a set to find the common interests we share */
        String[] myInterests = allMyInterests.split(", ");
        Set<String> myInterestSet = new HashSet<>();
        for (string item : myInterests) {
            myInterestSet.add(item);
        }

        List<String> result = new ArrayList<>();
        for (Vertex currentVertex : targetVertex.outE(LIKES).inV().toList()) {
            String thisInterest = currentVertex.property("name").value();
            if (myInterestSet.contains(thisInterest)) {
                result.add(thisInterest);
            }
        }
        return result;
    }

    /**
     * Query: How many people am I away from the person I wish I knew? (The shortest path between two nodes)
     * @return The list of name of people that connects me to the person, if there is any
     * TODO : make sure which of the version of finding the shortest path will work
     */
    public String getDegreeFromTargetPerson(String startPeople, String targetPeople) {
        List<String> path = new ArrayList<>();

        final Vertex vA = graph.traversal().V().has("name", startPeople).next();
        final Vertex vB = graph.traversal().V().has("name", targetPeople).next();

        final GremlinPipeline pipe = new GremlinPipeline(vA)
                .as("person")
                .both(HAS_PEOPLE_INTERESTEDIN, ATTENDS, LIKES) /* TODO : Understand what's happening here */
                .loop("person", new PipeFunction<LoopPipe.LoopBundle, Boolean>() {
                    @Override
                    public Boolean compute(LoopPipe.LoopBundle loopBundle) {
                        return loopBundle.getLoops() < MAX_LOOP_STEPS && loopBundle.getObject() != vB;
                    }
                }).path();

        if (pipe.hasNext()) {
            final List<CacheVertex> p = (ArrayList<CacheVertex>) pipe.next();
            for (final CacheVertex v : p) {
                path.add((String) ((Vertex)v).property("name").value());
            }
        }
        return path.size() == 0 ? "Can't connect " + startPeople + " to " + targetPeople : path.toString();
    }

    /**
     * TODO : is this version working?
     * Another version of finding the shortest path between two user in the system.
     */
    public List<String> getShortestPathVersion2(String startPerson, String endPerson) {
        if (startPerson == null || startPerson.length() == 0 || endPerson == null || endPerson.length() == 0) {
            return "";
        }

        GraphTraversalSource traversal = graph.traversal();
        Vertex fromNode = g.V().has("name", startPerson).next();
        Vertex toNode = g.V().has("name", endPerson).next();
        if (fromNode == null || toNode == null) {
            throw new RequiredVertexNotFoundException("Expect both users in our system but somehow at least one of them is missing.");
        }

        List<String> result = new ArrayList();
        g.V(fromNode).repeat(both().simplePath())
                .until(is(toNode))
                .limit(1) /* Only output 1 path for all possible results we have here */
                .path().fill(result); /* TODO: Simple path???? */

        return result;
    }


    /**
     * Query: Recommend people you may want to meet (recommend friends' friend)
     * @return List of people that recommend me to know. They should be in decending order
     *         by the number of people we both know.
     *         We at most return 5 people to the user.
     * TODO: the input format for the friends.
     * TODO: Decide the format for the output. (List? String? Map?)
     */
    public Map<String, Integer> getFriendsRecommend(String myName, String friendsNames) {
        if (myName == null || myName.length() == 0 || friendsNames == null || friendsNames.length() == 0) {
            return "";
        }

        /* Find all friends for my friends, and find the number of people we know in common */
        String[] friends = friendsNames.split(", ");
        Set<String> myFriends = new HashSet<>();
        for (String person : friends) {
            myFriends.add(person);
        }

        List<Pair> peopleRecommend = new ArrayList<>();
        for (String curPerson : friends) {
            Vertex curVertex = graph.getVertex("name", curPerson).next();
            if (curVertex == null) {
                continue;
            }
            // All friends for my friend
            List<Vertex> friendsOfMyFriend = curVertex.outE(KNOWS).inV().toList();

            // Let's see if I know any of his friends.
            int numOfCommonFriends = 0;
            for (Vertex person : friendsOfMyFriend) {
                for (Vertex current : person.outE(KNOWS).inV().toList()) {
                    if (myFriends.contains(current.property("name").value())) {
                        numOfCommonFriends++;
                    }
                }

                if (numOfCommonFriends != 0) {
                    peopleRecommend.add(new Pair(person.property("name").value(), numOfCommonFriends));
                }
            }
        }

        // Sort them in the decending order
        Arrays.sort(peopleRecommend, (p1, p2) -> p2.number - p1.number);
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < peopleRecommend.size; i++) {
            if (i == 5) {// Limit the number of recommendation to be 5 at max
                break;
            }

            result.put(peopleRecommend.get(i).name, peopleRecommend.get(i).number);
        }

        return result;
    }




    /********** Old code TBD what to do *************/

    public String getRecommend(String person) {
        String res = getPersonByEdge("name", "UF", "studyIn");
        res += getPersonByEdge("name", "football", "interestIn");
        res += getPersonByEdge("name", "basketball", "interestIn");
        return res;
    }

    public String getPersonByEdge(String propertyKey, String value, String edgeLabel) {
        List<String> students = new ArrayList<>();
        List<Vertex> l = graph.traversal().V().has(propertyKey, value).inE(edgeLabel).outV().toList();
        System.out.println(l.size());
        for (Vertex v : l) {
            students.add((String) v.property("name").value());
        }

        return students.toString();
    }

    public String listVertices() {
        List<String> gods = new ArrayList<String>();
        Iterator<Vertex> itty = graph.vertices();
        Vertex v;
        while (itty.hasNext()) {
            v = itty.next();
            gods.add((String) v.property("name").value());
        }
        return gods.toString();
    }

    /**
     * Inform the user the total number of vertices we have in our system so far
     * @return The total number of nodes in String format.
     */
    public String getVerticesCount() {
        long res = 0;
        Iterator<Vertex> itty = graph.vertices();
        Vertex v;
        while (itty.hasNext()) {
            v = itty.next();
            res++;
        }
        return Long.toString(res);
    }

    /**
     * A wrapper class created for the friends recommendation query
     */
    private class Pair {
        String name;
        int number;

        public Pair(String name, int num) {
            this.name = name;
            this.number = num;
        }
    }

    /**
     * Thrown when we did not find the vertex that we are looking for in our database.
     */
    private class RequiredVertexNotFoundException extends RuntimeException {
        public RequiredVertexNotFoundException(String cause) {
            super(cause);
        }
    }
}
