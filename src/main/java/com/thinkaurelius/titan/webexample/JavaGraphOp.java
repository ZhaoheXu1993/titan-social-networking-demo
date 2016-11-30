package com.thinkaurelius.titan.webexample;

import com.tinkerpop.blueprints.Direction;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.both;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.is;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;

@Component
public class JavaGraphOp {
    /**
     * TODO: convert all these types into different ENUM type class and reconstruct the code.
     *
     * Define different types to identify different tpyes of nodes we have in the g.
     */
    private static final String PERSON = "person";
    private static final String SCHOOL = "school";
    private static final String INTEREST = "interest";
    private static final String ASSOCIATION = "associations";
    private static final String MAJOR = "major";

    /**
     * TODO: convert all these types into different ENUM type class and reconstruct the code.
     *
     * Define possible edge labels we will need in our social network g.
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
    private Graph g;

    @Autowired
    // I like to autowire via setters because it makes it easy to write spring-free unit tests.
    public void setG(TitanGraphFactory gf) {
        this.g = gf.getGraph();
    }


    /**
     * Thrown when we did not find the vertex that we are looking for in our database.
     */
    private class RequiredVertexNotFoundException extends RuntimeException {
        public RequiredVertexNotFoundException(String cause) {
            super(cause);
        }
    }

    /**
     * When a new user input his data into the system, we should firstly insert
     * a new node representing this user, and connect to other node as necessay.
     */
//    public void addNewUserToGraph() {
//        // TODO: Discuss the input format with Joe and Thomas, and implement this method.
//    }
//
    /**
     * Query: Find all people who goes to the same school with the user.
     * @param school Name of the school the user goes to
     * @return List of people who also attend to the same school
     */
    public Map<String, String> getPeopleInTheSameSchool(String school) {
        if (school == null || school.length() == 0) {
            return new HashMap<>();
        }

//        Vertex schoolVertex = g.traversal().V().has("name", school).next();
//        System.out.println("For school: " + school + " we found vertex " + schoolVertex);
//        if (schoolVertex == null) {
//            throw new RequiredVertexNotFoundException("Looking for school " + school +
//                    ", but did not found existing information in our database :(");
//        }
        List<Vertex> studentsInThisSchool = g.traversal().V().has("name", school).outE(HAS_STUDENT).inV().toList();
        //System.out.println("For school " + school + " we found " + studentsInThisSchool.size() + " studys there");

        HashMap<String, String> result = new HashMap<>();
        for (Vertex vertex : studentsInThisSchool) {
            if (vertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, STUDIES).hasNext()) {
                Vertex majorVtx = vertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, STUDIES).next().inVertex();
                String name = (String) vertex.property("name").value();
                String majorName = (String) majorVtx.property("name").value();
                result.put(name, majorName);
            }
            //System.out.println("Adding student " + name);
        }

        return result;
    }

    /**
     * Query: Find all students who attends the same school with the user and
     *        the same major
     * @return List of student names.
     */
    public Map<String, String> getPeopleWithSameMajor(String major, String school) {
        if (major == null || major.length() == 0 || school == null || school.length() == 0) {
            return new HashMap<>();
        }

        //Vertex schoolVertex = g.traversal().V().has("name", school).next();
        //System.out.println("For school: " + school + " we found vertex " + schoolVertex);
        // Put all students in the same school into a set
        List<Vertex> studentsInThisSchool = g.traversal().V().has("name", school).outE(HAS_STUDENT).inV().toList();
        //System.out.println("For school " + school + " we found " + studentsInThisSchool.size() + " studys there");

        Map<String, String> result = new HashMap<>();
        for (Vertex vertex : studentsInThisSchool) {
            if (!vertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, STUDIES).hasNext()) {
                continue;
            }

            if (vertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, STUDIES)
                    .next().inVertex().property("name").value().equals(major)) {
                String name = (String) vertex.property("name").value();
                if (vertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, LIKES).hasNext()) {
                    String interestName = (String)vertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, LIKES)
                            .next().inVertex().property("name").value();
                    result.put(name, interestName);
                }
            }
            //System.out.println("Adding student " + name + " to school set");
        }

        // Check each of this person if they attends this school
//        Vertex majorVertex = g.traversal().V().has("name", major).next();
//        List<String> result = new ArrayList<>();
//        for (Vertex vertex : majorVertex.outE().inV().toList()) {
//            String name = (String)vertex.property("name").value();
//            if (students.contains(name)) {
//                System.out.println("Student " + name + " is in school" + school + " and study " + major);
//                result.add(name);
//            }
//        }
//
//        System.out.println("We found " + result.size() + " Students that attends " + school + " and study " + major);
        return result;
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
    public Map<String, String> getPeopleWithSameInterestsInSchool(String interests, String school) {
        if (interests == null || interests.length() == 0 || school == null || school.length() == 0) {
            return new HashMap<>();
        }

        List<String> interestList = new ArrayList<>(Arrays.asList(interests.split("_")));
        Set<String> interestSet = new HashSet<>(interestList);
        int minReq = interestSet.size() / 2 + 1;
        /*
         * We firstly get back all students that attends the same school, then check one
         * by one whether they share same interests or not.
         */
        // TODO: we should be more specific about this, e.g.: A --> basketball, B --> football.
        List<Vertex> studentsInSameSchool = g.traversal().V().has("name", school).outE(HAS_STUDENT).inV().toList();
        Map<String, String> result = new HashMap<>();
        for (Vertex studentVertex : studentsInSameSchool) {
            int interestCount = 0;
            Iterator<Edge> interestIter = studentVertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, LIKES);
            while(interestIter.hasNext()) {
                Edge interestEdge = interestIter.next();
                Vertex interestVertex = interestEdge.inVertex();
                if (interestSet.contains((String)interestVertex.property("name").value())) {
                    interestCount++;
                    if (studentVertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, STUDIES).hasNext()
                            && interestCount >= minReq) {
                        String name = (String)studentVertex.property("name").value();
                        String majorName = (String)studentVertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, STUDIES)
                                .next().inVertex().property("name").value();
                        result.put(name, majorName);
                    }
                }

//                if (interestsSet.contains((String) interestVertex.property("name").value())) {
//                    String foundOne = (String)studentVertex.property("name").value();
//                    result.add(foundOne);
//                    System.out.println("Found " + foundOne + " who also likes playing " + (String) interestVertex.property("name").value());
//                    break; /* Go check for next possible person so that we will not have duplicates */
//                }

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
    public List<String> findCommonInterestsWithSomeone(String sourcePerson, String targetPerson) {
        List<String> result = new ArrayList<>();
        if (targetPerson == null || targetPerson.length() == 0) {
            return result;
        }


        /* Check if this people is in our system */
        Vertex targetVertex = g.traversal().V().has("name", targetPerson).next();
        Vertex sourceVertex = g.traversal().V().has("name", sourcePerson).next();
        if (targetVertex == null) {
            System.out.println("We cant find student " + targetPerson + "in database");
//            throw new RequiredVertexNotFoundException("Expect to find user:" + targetPerson + " in our system but does not exist.");
        }

        /* Use a set to find the common interests we share */
        Set<String> sourceInterestSet = new HashSet<>();
        Iterator<Edge> sourceInterestItr = sourceVertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, LIKES);
        while (sourceInterestItr.hasNext()) {
            sourceInterestSet.add((String)sourceInterestItr.next().inVertex().property("name").value());
        }

        Iterator<Edge> targetInterestItr = targetVertex.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, LIKES);
        while (targetInterestItr.hasNext()) {
            String interestName = (String)targetInterestItr.next().inVertex().property("name").value();
            if (sourceInterestSet.contains(interestName)) {
                result.add(interestName);
            }
        }

        return result;
    }

//    /**
//     * Query: How many people am I away from the person I wish I knew? (The shortest path between two nodes)
//     * @return The list of name of people that connects me to the person, if there is any
//     * TODO : make sure which of the version of finding the shortest path will work
//     */
//    public String getDegreeFromTargetPerson(String startPeople, String targetPeople) {
//        List<String> path = new ArrayList<>();
//
//        final Vertex vA = g.traversal().V().has("name", startPeople).next();
//        final Vertex vB = g.traversal().V().has("name", targetPeople).next();
//
//        final GremlinPipeline pipe = new GremlinPipeline(vA)
//                .as("person")
//                .both(/* HAS_PEOPLE_INTERESTEDIN, ATTENDS, LIKES*/ KNOWS) /* TODO : Understand what's happening here */
//                .loop("person", new PipeFunction<LoopPipe.LoopBundle, Boolean>() {
//                    @Override
//                    public Boolean compute(LoopPipe.LoopBundle loopBundle) {
//                        return loopBundle.getLoops() < MAX_LOOP_STEPS && loopBundle.getObject() != vB;
//                    }
//                }).path();
//
//        if (pipe.hasNext()) {
//            final List<CacheVertex> p = (ArrayList<CacheVertex>) pipe.next();
//            for (final CacheVertex v : p) {
//                path.add((String) ((Vertex)v).property("name").value());
//            }
//        }
//        return path.size() == 0 ? "Can't connect " + startPeople + " to " + targetPeople : path.toString();
//    }

    /**
     * TODO : is this version working?
     * Another version of finding the shortest path between two user in the system.
     */
    public List<String> getShortestPathVersion2(String startPerson, String endPerson) {
        if (startPerson == null || startPerson.length() == 0 || endPerson == null || endPerson.length() == 0) {
            return new ArrayList<String>();
        }

        GraphTraversalSource traversal = g.traversal();
        Vertex fromNode = traversal.V().has("name", startPerson).next();
        Vertex toNode = traversal.V().has("name", endPerson).next();
        if (fromNode == null || toNode == null) {
            throw new RequiredVertexNotFoundException("Expect both users in our system but somehow at least one of them is missing.");
        }

        List<String> result = new ArrayList();
        GraphTraversal path = traversal.V(fromNode).repeat(out().simplePath()).until(is(toNode)).limit(1).path();
        result = path.toList();
//        traversal.V(fromNode).repeat(both().simplePath())
//                .until(is(toNode))
//                .limit(1) /* Only output 1 path for all possible results we have here */
//                .path()
//                .fill(result); /* TODO: Simple path???? */

        return result;
    }

    public String addFriend(String sourceName, String targetName) {
        if (sourceName == null || sourceName.length() == 0
                || targetName == null || targetName.length() == 0) {
            return "404";
        }

        Iterator<Vertex> sourceItr = g.traversal().V().has("name", sourceName);
        Iterator<Vertex> targetItr = g.traversal().V().has("name", targetName);

        if (!sourceItr.hasNext() || !targetItr.hasNext()) {
            return "404";
        }

        Vertex sourceVertex = sourceItr.next();
        Vertex targetVertex = targetItr.next();

        sourceVertex.addEdge(KNOWS, targetVertex);
        targetVertex.addEdge(KNOWS, sourceVertex);

        g.tx().commit();

        return "200";
    }

    public List<String> addStudent(String name,
                                   String school,
                                   String interests,
                                   String major,
                                   String friends) {
        List<String> notFoundList = new ArrayList<>();
        if (school == null || school.length() == 0 || name == null || name.length() == 0
                || major == null || major.length() == 0) {
            return notFoundList;
        }

        List<String> interestList = new ArrayList<>(Arrays.asList(interests.split("_")));
        List<String> friendList = new ArrayList<>(Arrays.asList(friends.split("_")));

        Vertex newStudent = g.addVertex(T.label, PERSON, "name", name);

        Vertex schoolVertex = g.traversal().V().has("name", school).next();
        newStudent.addEdge(SCHOOL, schoolVertex);
        schoolVertex.addEdge(HAS_STUDENT, newStudent);

        Vertex majorVertex = g.traversal().V().has("name", major).next();
        newStudent.addEdge(STUDIES, majorVertex);
        majorVertex.addEdge(HAS_STUDENT_STUDY, newStudent);

        for (String interest : interestList) {
            Iterator<Vertex> interestItr = g.traversal().V().has("name", interest);
            if (!interestItr.hasNext()) {
                continue;
            }
            Vertex interestVertex = interestItr.next();

            newStudent.addEdge(LIKES, interestVertex);
            interestVertex.addEdge(HAS_PEOPLE_INTERESTEDIN, newStudent);
        }

        for (String friend : friendList) {
            Iterator<Vertex> friendItr = g.traversal().V().has("name", friend);
            if (!friendItr.hasNext()) {
                notFoundList.add(friend);
                continue;
            }
            Vertex friendVertex = friendItr.next();
            newStudent.addEdge(KNOWS, friendVertex);
            friendVertex.addEdge(KNOWS, newStudent);
        }

        g.tx().commit();

        return notFoundList;
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
            return null;
        }

        /* Find all friends for my friends, and find the number of people we know in common */
        String[] friends = friendsNames.split(", ");
        Set<String> myFriends = new HashSet<>();
        for (String person : friends) {
            myFriends.add(person);
        }

        List<Pair> peopleRecommend = new ArrayList<>();
        for (String curPerson : friends) {
            Vertex curVertex = g.traversal().V().has("name", curPerson).next();

            if (curVertex == null) {
                continue;
            }
            // All friends for my friend
            List<Vertex> friendsOfMyFriend = g.traversal().V().has("name", curPerson).outE(KNOWS).inV().toList();
            System.out.println("The friends of " + curPerson + ": size is " + friendsOfMyFriend.size());

            // Let's see if I know any of his friends.
            int numOfCommonFriends = 0;
            for (Vertex person : friendsOfMyFriend) {
                Iterator<Edge> edgeIterator = person.edges(org.apache.tinkerpop.gremlin.structure.Direction.OUT, KNOWS);
                while (edgeIterator.hasNext()) {
                    Edge currE = edgeIterator.next();
                    Vertex inFFF = currE.inVertex();
                    if (myFriends.contains((String)inFFF.property("name").value())) {
                        numOfCommonFriends++;
                    }
                }

                if (numOfCommonFriends != 0) {
                    peopleRecommend.add(new Pair((String)person.property("name").value(), numOfCommonFriends - 500)); /* Make result more reliable */
                }
            }
        }

        // Sort them in the decending order
        Collections.sort(peopleRecommend, new Comparator<Pair>(){
            @Override
            public int compare(Pair p1, Pair p2) {
                return p2.number - p1.number;
            }
        });

        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < peopleRecommend.size(); i++) {
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
        List<Vertex> l = g.traversal().V().has(propertyKey, value).inE(edgeLabel).outV().toList();
        System.out.println(l.size());
        for (Vertex v : l) {
            students.add((String) v.property("name").value());
        }

        return students.toString();
    }

    public String listVertices() {
        List<String> gods = new ArrayList<String>();
        Iterator<Vertex> itty = g.vertices();
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
        Iterator<Vertex> itty = g.vertices();
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
}
