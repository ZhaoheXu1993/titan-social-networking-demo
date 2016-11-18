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
    // Autowired via setter. I leave this as a blueprints.Graph unless I have to do Titan specific stuff.
    private Graph g;

    @Autowired
    // I like to autowire via setters because it makes it easy to write spring-free unit tests.
    public void setGraph(TitanGraphFactory gf) {
        this.g = gf.getGraph();
    }

    public String getDegree(String personA, String personB) {
        List<String> path = new ArrayList<>();

        final Vertex vA = g.traversal().V().has("name", personA).next();
        final Vertex vB = g.traversal().V().has("name", personB).next();

        final GremlinPipeline pipe = new GremlinPipeline(vA).as("person")
                .both("iHas","studyIn","hHas","interestIn").loop("person", new PipeFunction<LoopPipe.LoopBundle, Boolean>() {
                    @Override
                    public Boolean compute(LoopPipe.LoopBundle loopBundle) {
                        return loopBundle.getLoops() < 5 && loopBundle.getObject() != vB;
                    }
                }).path();

        if (pipe.hasNext()) {
            final List<CacheVertex> p = (ArrayList<CacheVertex>) pipe.next();
            for (final CacheVertex v : p) {
                path.add((String) ((Vertex)v).property("name").value());
            }
        }
        return path.size() == 0 ? "Can't connect " + personA + " to " + personB : path.toString();
    }

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
}
