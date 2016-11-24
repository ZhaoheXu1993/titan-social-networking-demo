package com.thinkaurelius.titan.webexample;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.core.util.TitanCleanup;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This Component exists to provide other beans with access to the TitanGraph instance.
 */
@Component
public class TitanGraphFactory {
    private static final Logger logger = LoggerFactory.getLogger(TitanGraphFactory.class);
    public static final String PROPS_PATH = "titan-web-example/config/titan-cassandra-es.properties";

    /**
     * Define different types to identify different tpyes of nodes we have in the graph.
     */
    private static final String PERSON = "person";
    private static final String SCHOOL = "school";
    private static final String INTEREST = "interest";
    private static final String ASSOCIATION = "associations";
    private static final String MAJOR = "major";

    /**
     * Define possible edge labels we will need in our social network graph.
     */
    private static final String KNOWS = "knows";
    private static final String ATTENDS = "attends";
    private static final String STUDIES = "studies"; /* The major for each student */
    private static final String LIKES = "likesPlaying";
    private static final String JOINED = "joined";
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
    private static final String[] MAJOR_COLLECTION = {"computer science", "computer engineering", "photographing", "chemical engineering", "mechanical engineer"
                                                      "electronical engineering", "jornalism", "physics"};
    private static final String[] ASSOCIATION_COLLECTION = {"beach volleyball team", "swimming team", "fraternity", "football team", "tennis team"};

    // One graph to rule them all...
    private TitanGraph graph;

    @PostConstruct
    public void init() {
        try {
            logger.info("Titan Properties Path: {}", PROPS_PATH);
            Configuration conf = new PropertiesConfiguration(PROPS_PATH);
            graph = TitanFactory.open(conf);

            Iterator<Vertex> itty = graph.vertices();
            Vertex v;

            // remove existing nodes
            if (!graph.tx().isOpen()) {
                graph.tx().open();
            }

            // cleanup graph
            graph.traversal().V().drop().iterate();
            TitanManagement mgnt = graph.openManagement();

            /*
             * First of all, let's create all schools and hobbies in our system.
             */
            for (int i = 0; i < SCHOOL_COLLECTION.length; i++) {
                graph.addVertex(T.label, SCHOOL, "name", SCHOOL_COLLECTION[i]);
            }
            for (int i = 0; i < MAJOR_COLLECTION.length; i++) {
                graph.addVertex(T.label, MAJOR, "name", MAJOR_COLLECTION[i]);
            }
            for (int i = 0; i < INTEREST_COLLECTION.length; i++) {
                graph.addVertex(T.label, INTEREST, "name", INTEREST_COLLECTION[i]);
            }
            for (int i = 0; i < ASSOCIATION_COLLECTION.length; i++) {
                graph.addVertex(T.label, ASSOCIATION, "name", ASSOCIATION_COLLECTION[i]);
            }

            /*
             * Secondly, let's add some people in to the graph.
             *
             * Here we will craete 3000 people each to join the 4 schools we have here,
             * and randomly assign them hobbies and majors, and a association to go to.
             */
            Random randomNumGernerator = new Random();
            List<Vertex> ufStudents = new ArrayList<>();
            List<Vertex> ucfStudents = new ArrayList<>();
            List<Vertex> sfuStudents = new ArrayList<>();
            List<Vertex> fiuStudents = new ArrayList<>();
            List<Vertex>[] studentLists = {ucfStudents, ucfStudents, sfuStudents, fiuStudents};

            for (int i = 0; i < SCHOOL_COLLECTION.length; i++) {
                String school = SCHOOL_COLLECTION[i];
                Vertex schoolVertex = graph.getVertices(SCHOOL, school).next();

                for (int j = 1; j <= 3000; j++) {
                    String studentName = school + "-student-" + j;
                    Vertex studentVertex = graph.addVertex(T.label, PERSON, "name", studentName);
                    studentLists[i].add(student);

                    /* Connect this student to the school. */
                    studentVertex.addEdge(ATTENDS, schoolVertex);
                    schoolVertex.addEdge(HAS_STUDENT, studentVertex);

                    /* Assign a major to this student */
                    Vertex majorVertex = graph.getVertices(MAJOR, MAJOR_COLLECTION[randomNumGernerator.nextInt(MAJOR_COLLECTION.length)]);
                    majorVertex.addEdge(HAS_STUDENT_STUDY, studentVertex);
                    studentVertex.addEdge(STUDIES, majorVertex);

                    /* Pick some of the students to attend some random associations */
                    if (j % 6 == 0) {
                        // TODO: add student to associations
                    }

                    /* Randomly assign 2 interests for this student */
                    for (int z = 0; z < 2; z++) {
                        Vertex interestVertex = graph.getVertices(INTEREST, INTEREST_COLLECTION[randomNumGernerator.nextInt(INTEREST_COLLECTION.length)]);
                        studentVertex.addEdge(LIKES, interestVertex);
                        interestVertex.addEdge(HAS_PEOPLE_INTERESTEDIN, studentVertex);
                    }
                }
            }
















            // build a social graph
            Vertex uf = graph.addVertex(T.label, "school", "name", "UF");
            Vertex fsu = graph.addVertex(T.label, "school", "name", "FSU");
            Vertex fBall = graph.addVertex(T.label, "interest", "name", "football");
            Vertex bBall = graph.addVertex(T.label, "interest", "name", "basketball");
            Vertex joe = graph.addVertex(T.label, "person", "name", "Joe");
            Vertex alan = graph.addVertex(T.label, "person", "name", "Alan");
            Vertex thomas = graph.addVertex(T.label, "person", "name", "Thomas");
            Vertex jessci = graph.addVertex(T.label, "person", "name", "Jessci");
            Vertex james = graph.addVertex(T.label, "person", "name", "James");
            Vertex kobe = graph.addVertex(T.label, "person", "name", "Kobe");
            Vertex love = graph.addVertex(T.label, "person", "name", "Love");

            List<Vertex> ufStudents = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                Vertex tmp = graph.addVertex(T.label, "person", "name", "Robot" + Integer.toString(i));
                ufStudents.add(tmp);
                tmp.addEdge("studyIn", uf);
                uf.addEdge("sHas", tmp);
            }

            joe.addEdge("studyIn", uf);
            uf.addEdge("uHas", joe);
            alan.addEdge("studyIn", uf);
            uf.addEdge("uHas", alan);
            thomas.addEdge("studyIn", uf);
            uf.addEdge("uHas", thomas);
            jessci.addEdge("studyIn", uf);
            uf.addEdge("uHas", jessci);
            james.addEdge("studyIn", fsu);
            fsu.addEdge("uHas", james);
            kobe.addEdge("studyIn", fsu);
            fsu.addEdge("uHas", kobe);
            love.addEdge("studyIn", fsu);
            fsu.addEdge("uHas", love);

            joe.addEdge("interestIn", fBall);
            fBall.addEdge("iHas", joe);
            joe.addEdge("interestIn", bBall);
            bBall.addEdge("iHas", joe);
            alan.addEdge("interestIn", bBall);
            bBall.addEdge("iHas", alan);
            james.addEdge("interestIn", bBall);
            bBall.addEdge("iHas", james);
            kobe.addEdge("interestIn", bBall);
            bBall.addEdge("iHas", kobe);
            kobe.addEdge("interestIn", fBall);
            fBall.addEdge("iHas", kobe);
            love.addEdge("interestIn", bBall);
            bBall.addEdge("iHas", love);

            graph.tx().commit();
            logger.info("Titan graph loaded successfully.");
        } catch (ConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public TitanGraph getGraph() {
        return graph;
    }
}