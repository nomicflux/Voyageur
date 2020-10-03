package io.ataraxic.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.shoki.impl.StrictQueue;
import io.ataraxic.nomicflux.voyageur.impl.AdjListGraph;
import io.ataraxic.nomicflux.voyageur.impl.ValueNode;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.monoid.Monoid.monoid;
import static io.ataraxic.nomicflux.voyageur.impl.ValueEdge.weightedEdgeFromTo;
import static io.ataraxic.nomicflux.voyageur.impl.ValueNode.node;
import static io.ataraxic.nomicflux.voyageur.path.Dijkstra.dijkstra;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static testsupport.matchers.IterableMatcher.iterates;

public class DijkstraTest {

    @Test
    public void takeShorterPath() {
        AdjListGraph<Integer, Unit, Integer> graph = AdjListGraph.fromEdges(asList(weightedEdgeFromTo(node(1), node(3), 10),
                weightedEdgeFromTo(node(2), node(3), 2),
                weightedEdgeFromTo(node(3), node(4), 1),
                weightedEdgeFromTo(node(1), node(2), 1)));

        StrictQueue<Tuple2<Integer, ValueNode<Integer, Unit>>> found = dijkstra(monoid(Integer::sum, 0), graph, node(1), 4);
        assertThat(found, iterates(tuple(0, node(1)), tuple(1, node(2)), tuple(3, node(3)), tuple(4, node(4))));
    }
}