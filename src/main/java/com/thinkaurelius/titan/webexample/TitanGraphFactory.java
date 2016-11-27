package com.thinkaurelius.titan.webexample;

import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * This Component exists to provide other beans with access to the TitanGraph instance.
 */
@Component
public class TitanGraphFactory {
    private static final Logger logger = LoggerFactory.getLogger(TitanGraphFactory.class);
    public static final String PROPS_PATH = "titan-web-example/config/titan-cassandra-es-v0.properties";

    /**
     * Define different types to identify different tpyes of nodes we have in the graph.
     */
    private static final String PERSON = "person";
    private static final String SCHOOL = "school";
    private static final String INTEREST = "interest";
    private static final String ASSOCIATION = "associations";
    private static final String MAJOR = "major";

    /**
     * Primiry property key
     */
    private static final String SCHOOLNAME = "name";
    private static final String MAJORNAME = "name";
    private static final String ASSNAME = "name";
    private static final String INTRNAME = "name";

    /**
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

    /**
     * Common things that people like to do, and schools that students go to in FL.
     */
    private static final String[] INTEREST_COLLECTION = {"basketball", "football", "soccer", "swimming", "running", "tennis", "volleyball", "beach volleyball",
            "body building", "vedio games", "cooking", "baseball"};
    private static final String[] SCHOOL_COLLECTION = {"UF", "UCF", "SFU", "FIU"};
    private static final String[] MAJOR_COLLECTION = {"computer science", "computer engineering", "photographing", "chemical engineering", "mechanical engineer",
            "electronical engineering", "jornalism", "physics"};
    private static final String[] ASSOCIATION_COLLECTION = {"beach volleyball team", "swimming team", "fraternity", "football team", "tennis team"};

    // One graph to rule them all...
    private TitanGraph graph;

    private Map<String, Vertex> mapSchool = new HashMap<>();
    private Map<String, Vertex> mapMajor = new HashMap<>();
    private Map<String, Vertex> mapInterest = new HashMap<>();
    private Map<String, Vertex> mapAsso = new HashMap<>();

    private int _countVertex(TitanGraph graph) {
        Iterator<Vertex> it = graph.vertices();
        int count = 0;
        while (it.hasNext()) {
            Vertex v = it.next();
            count++;
        }
        return count;
    }

    @PostConstruct
    public void init() {
        System.out.print("-----------------Starting initializing -----------------  ");
        try {
            logger.info("Titan Properties Path: {}", PROPS_PATH);
            Configuration conf = new PropertiesConfiguration(PROPS_PATH);
            graph = TitanFactory.open(conf);
//
////            Iterator<Vertex> itty = graph.vertices();
////            Vertex v;
//
//            // remove existing nodes
//            if (!graph.tx().isOpen()) {
//                graph.tx().open();
//            }
//
//            // cleanup graph
//
////            Iterator<Vertex> it = graph.vertices();
////            int count = 0;
////            while (it.hasNext()) {
////                Vertex v = it.next();
////                count++;
////                v.remove();
////                graph.tx().commit();
////            }
//            //System.out.println("Before drop: " + count);
//
////            graph.traversal().V().drop().iterate();
////            graph.tx().commit();
////
////            count = 0;
////            it = graph.vertices();
////            while (it.hasNext()) {
////                it.next();
////                count++;
////            }
//            System.out.println("After drop: " + _countVertex(graph));
//            System.out.println("STOP");
////            TitanManagement mgnt = graph.openManagement();
//            //PropertyKey nameKey = mgnt.makePropertyKey("name").dataType(String.class).make();
//            //mgnt.buildIndex("name", Vertex.class).addKey(nameKey).buildCompositeIndex();
//            //mgnt.commit();
//
//            /*
//             * First of all, let's create vertex for school/hobbies/association/major in our system.
//             */
//            for (int i = 0; i < SCHOOL_COLLECTION.length; i++) {
//                System.out.println("----------- Inserting new node :) ----!!!!!--------------- ");
//                Vertex v = graph.addVertex(T.label, SCHOOL, "name", SCHOOL_COLLECTION[i]);
//                mapSchool.put(SCHOOL_COLLECTION[i], v);
//            }
//            for (int i = 0; i < MAJOR_COLLECTION.length; i++) {
//                System.out.println("------------Inserting new node :) -----------!!!!!!!!-- ");
//                mapMajor.put(MAJOR_COLLECTION[i],
//                        graph.addVertex(T.label, MAJOR, "name", MAJOR_COLLECTION[i]));
//            }
//            for (int i = 0; i < INTEREST_COLLECTION.length; i++) {
//                System.out.println("------!!!@!@!@-------Inserting new node :) ------------------- ");
//                mapInterest.put(INTEREST_COLLECTION[i],
//                        graph.addVertex(T.label, INTEREST, "name", INTEREST_COLLECTION[i]));
//            }
//            for (int i = 0; i < ASSOCIATION_COLLECTION.length; i++) {
//                System.out.println("-!@!#@#%#$^@-----------------Inserting new node :) ----------------- ");
//                mapAsso.put(ASSOCIATION_COLLECTION[i],
//                        graph.addVertex(T.label, ASSOCIATION, "name", ASSOCIATION_COLLECTION[i]));
//            }
//
//            System.out.println("After insert special vertices: " + _countVertex(graph));
//            /*
//             * Secondly, let's add some people in to the graph.
//             *
//             * Here we will craete 3000 people for each school we have here,
//             * and randomly assign them hobbies and majors, and a association to go to.
//             */
//            Random randomNumGernerator = new Random();
//            List<Vertex> ufStudents = new ArrayList<>();
//            List<Vertex> ucfStudents = new ArrayList<>();
//            List<Vertex> sfuStudents = new ArrayList<>();
//            List<Vertex> fiuStudents = new ArrayList<>();
//            List<List<Vertex>> studentLists = new ArrayList<>();
//            studentLists.add(ufStudents);
//            studentLists.add(ucfStudents);
//            studentLists.add(sfuStudents);
//            studentLists.add(fiuStudents);
//
//            for (int i = 0; i < SCHOOL_COLLECTION.length; i++) {
//                String school = SCHOOL_COLLECTION[i];
//                //Vertex schoolVertex = graph.traversal().V().has(SCHOOLNAME, school).next();
//                Vertex schoolVertex = mapSchool.get(school);
//
//                for (int j = 1; j <= 3000 /*3000*/; j++) {
//                    System.out.println("------ Inserting new node :) --------  ");
//
//                    String studentName = school + "--student--" + j;
//                    Vertex studentVertex = graph.addVertex(T.label, PERSON, "name", studentName);
//                    studentLists.get(i).add(studentVertex);
//
//                    /* Connect this student to the school. */
//                    studentVertex.addEdge(ATTENDS, schoolVertex);
//                    schoolVertex.addEdge(HAS_STUDENT, studentVertex);
//
//                    /* Assign a major to this student */
//                    String major = MAJOR_COLLECTION[randomNumGernerator.nextInt(MAJOR_COLLECTION.length)];
//                    Vertex majorVertex = mapMajor.get(major);
//                    //Vertex majorVertex = graph.traversal().V().has(MAJORNAME, MAJOR_COLLECTION[randomNumGernerator.nextInt(MAJOR_COLLECTION.length)]).next();
//                    majorVertex.addEdge(HAS_STUDENT_STUDY, studentVertex);
//                    studentVertex.addEdge(STUDIES, majorVertex);
//
//                    /* Pick some of the students to attend some random associations. */
//                    if (j % 6 == 0) {
//                        String asso = ASSOCIATION_COLLECTION[randomNumGernerator.nextInt(ASSOCIATION_COLLECTION.length)];
//                        Vertex associationVertex = mapAsso.get(asso);
//                        //Vertex associationVertex = graph.traversal().V().has(ASSNAME, ASSOCIATION_COLLECTION[randomNumGernerator.nextInt(ASSOCIATION_COLLECTION.length)]).next();
//                        studentVertex.addEdge(JOINED, associationVertex);
//                        associationVertex.addEdge(HAS_MEMBER, studentVertex);
//                    }
//
//                    /* Randomly assign 3 interests for this student */
//                    for (int z = 0; z < 3; z++) {
//                        //Vertex interestVertex = graph.traversal().V().has(INTRNAME, INTEREST_COLLECTION[randomNumGernerator.nextInt(INTEREST_COLLECTION.length)]).next();
//                        String intr = INTEREST_COLLECTION[randomNumGernerator.nextInt(INTEREST_COLLECTION.length)];
//                        Vertex interestVertex = mapInterest.get(intr);
//                        studentVertex.addEdge(LIKES, interestVertex);
//                        interestVertex.addEdge(HAS_PEOPLE_INTERESTEDIN, studentVertex);
//                    }
//                }
//            }
//
//            System.out.println("After insert student vertices: " + _countVertex(graph));
//            /*
//             * Thirdly, let's help them kown each other :)
//             * Here, we will select 100 people for each person from the same campus as friends,
//             * and randomly select 20 students from students at other universities.
//             */
//            for (List<Vertex> studentList : studentLists) {
//                System.out.println("------ Trying to add friends here -------");
//
//                for (Vertex currentStudentVertex : studentList) {
//                    System.out.println(" ----  Taking care of 1 student list -------  ");
//
//                    Set<String> peopleAlreadyKnown = new HashSet<>();
//                    /* Select 100 people from the same school */
//                    peopleAlreadyKnown.add((String) currentStudentVertex.property("name").value());
//                    for (int i = 0; i < 100; i++) {
//                        Vertex friend = studentList.get(randomNumGernerator.nextInt(studentList.size()));
//                        if (peopleAlreadyKnown.contains((String) friend.property("name").value())) {
//                            i--;
//                            continue;
//                        }
//
//                        System.out.println("-----" + i + "-------");
//
//                        currentStudentVertex.addEdge(KNOWS, friend);
//                        friend.addEdge(KNOWS, currentStudentVertex);
//                        peopleAlreadyKnown.add((String) friend.property("name").value());
//                    }
//
//                    /* select 20 people from each of other schools */
//                    for (int j = 0; j < 20 /*20*/; j++) {
//                        List<Vertex> randomStudentList = studentLists.get(randomNumGernerator.nextInt(studentLists.size()));
//                        Vertex randomFriend = randomStudentList.get(randomNumGernerator.nextInt(randomStudentList.size()));
//                        if (peopleAlreadyKnown.contains((String) randomFriend.property("name").value())) {
//                            j--;
//                            continue;
//                        }
//
//                        currentStudentVertex.addEdge(KNOWS, randomFriend);
//                        randomFriend.addEdge(KNOWS, currentStudentVertex);
//                        peopleAlreadyKnown.add((String) randomFriend.property("name").value());
//                    }
//                }
//            }
//
//
//            // build a social graph
////            Vertex uf = graph.addVertex(T.label, "school", "name", "UF");
////            Vertex fsu = graph.addVertex(T.label, "school", "name", "FSU");
////            Vertex fBall = graph.addVertex(T.label, "interest", "name", "football");
////            Vertex bBall = graph.addVertex(T.label, "interest", "name", "basketball");
////            Vertex joe = graph.addVertex(T.label, "person", "name", "Joe");
////            Vertex alan = graph.addVertex(T.label, "person", "name", "Alan");
////            Vertex thomas = graph.addVertex(T.label, "person", "name", "Thomas");
////            Vertex jessci = graph.addVertex(T.label, "person", "name", "Jessci");
////            Vertex james = graph.addVertex(T.label, "person", "name", "James");
////            Vertex kobe = graph.addVertex(T.label, "person", "name", "Kobe");
////            Vertex love = graph.addVertex(T.label, "person", "name", "Love");
////
////            List<Vertex> ufStudents = new ArrayList<>();
////            for (int i = 0; i < 10000; i++) {
////                Vertex tmp = graph.addVertex(T.label, "person", "name", "Robot" + Integer.toString(i));
////                ufStudents.add(tmp);
////                tmp.addEdge("studyIn", uf);
////                uf.addEdge("sHas", tmp);
////            }
////
////            joe.addEdge("studyIn", uf);
////            uf.addEdge("uHas", joe);
////            alan.addEdge("studyIn", uf);
////            uf.addEdge("uHas", alan);
////            thomas.addEdge("studyIn", uf);
////            uf.addEdge("uHas", thomas);
////            jessci.addEdge("studyIn", uf);
////            uf.addEdge("uHas", jessci);
////            james.addEdge("studyIn", fsu);
////            fsu.addEdge("uHas", james);
////            kobe.addEdge("studyIn", fsu);
////            fsu.addEdge("uHas", kobe);
////            love.addEdge("studyIn", fsu);
////            fsu.addEdge("uHas", love);
////
////            joe.addEdge("interestIn", fBall);
////            fBall.addEdge("iHas", joe);
////            joe.addEdge("interestIn", bBall);
////            bBall.addEdge("iHas", joe);
////            alan.addEdge("interestIn", bBall);
////            bBall.addEdge("iHas", alan);
////            james.addEdge("interestIn", bBall);
////            bBall.addEdge("iHas", james);
////            kobe.addEdge("interestIn", bBall);
////            bBall.addEdge("iHas", kobe);
////            kobe.addEdge("interestIn", fBall);
////            fBall.addEdge("iHas", kobe);
////            love.addEdge("interestIn", bBall);
////            bBall.addEdge("iHas", love);
//            System.out.println("------------- -----Ready to commit ---------------------  ");
//
//            graph.tx().commit();
            logger.info("Titan graph loaded successfully.");
        } catch (ConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public TitanGraph getGraph() {
        return graph;
    }
}
