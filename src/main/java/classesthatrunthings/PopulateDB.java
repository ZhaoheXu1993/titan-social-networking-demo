package classesthatrunthings;

import com.thinkaurelius.titan.core.*;
import com.thinkaurelius.titan.core.schema.EdgeLabelMaker;
import com.thinkaurelius.titan.core.schema.PropertyKeyMaker;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.core.schema.VertexLabelMaker;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import com.thinkaurelius.titan.example.GraphOfTheGodsFactory;
import com.thinkaurelius.titan.webexample.TitanGraphFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.Collection;
import java.util.Iterator;

/**
 * Loads the graph of the gods example data set.
 */
public class PopulateDB {
    public static void main(String[] args) throws Exception {
        Configuration conf = new PropertiesConfiguration(TitanGraphFactory.PROPS_PATH);

        TitanGraph g = TitanFactory.open(conf);

        // Uncomment the following if your graph is already populated and you want to clear it out first.
        // g.close();
        // TitanCleanup.clear(g);
        // g = TitanFactory.open(conf);

        // Interested in the source?
        // https://github.com/thinkaurelius/titan/blob/titan05/titan-core/src/main/java/com/thinkaurelius/titan/example/GraphOfTheGodsFactory.java
        // GraphOfTheGodsFactory.load(g);
        g.close();
        System.out.println("Success.");
    }
}
