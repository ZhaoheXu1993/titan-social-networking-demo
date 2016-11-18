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

    // One graph to rule them all...
    private TitanGraph g;

    @PostConstruct
    public void init() {
        try {
            logger.info("Titan Properties Path: {}", PROPS_PATH);
            Configuration conf = new PropertiesConfiguration(PROPS_PATH);
            g = TitanFactory.open(conf);

            Iterator<Vertex> itty = g.vertices();
            Vertex v;

            // remove existing nodes
            if (!g.tx().isOpen()) {
                g.tx().open();
            }

            // cleanup graph
            g.traversal().V().drop().iterate();
            TitanManagement mgnt = g.openManagement();

            // build a social graph
            Vertex uf = g.addVertex(T.label, "school", "name", "UF");
            Vertex fsu = g.addVertex(T.label, "school", "name", "FSU");
            Vertex fBall = g.addVertex(T.label, "interest", "name", "football");
            Vertex bBall = g.addVertex(T.label, "interest", "name", "basketball");
            Vertex joe = g.addVertex(T.label, "person", "name", "Joe");
            Vertex alan = g.addVertex(T.label, "person", "name", "Alan");
            Vertex thomas = g.addVertex(T.label, "person", "name", "Thomas");
            Vertex jessci = g.addVertex(T.label, "person", "name", "Jessci");
            Vertex james = g.addVertex(T.label, "person", "name", "James");
            Vertex kobe = g.addVertex(T.label, "person", "name", "Kobe");
            Vertex love = g.addVertex(T.label, "person", "name", "Love");

            List<Vertex> ufStudents = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                Vertex tmp = g.addVertex(T.label, "person", "name", "Robot" + Integer.toString(i));
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

            g.tx().commit();
            logger.info("Titan graph loaded successfully.");
        } catch (ConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public TitanGraph getGraph() {
        return g;
    }
}
