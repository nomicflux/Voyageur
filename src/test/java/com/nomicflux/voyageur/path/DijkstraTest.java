package com.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueWeightedEdge;
import org.junit.Test;

import static com.jnape.palatable.lambda.monoid.Monoid.monoid;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static com.nomicflux.voyageur.impl.ValueWeightedEdge.weightedEdgeFromTo;
import static com.nomicflux.voyageur.path.Dijkstra.dijkstra;
import static java.util.Arrays.asList;

public class DijkstraTest {

    @Test
    public void takeShorterPath() {
        AdjListGraph<Integer, Node<Integer>, ValueWeightedEdge<Integer, Node<Integer>, Integer>> graph =
                AdjListGraph.fromEdges(asList(weightedEdgeFromTo(node(1), node(3), 10),
                        weightedEdgeFromTo(node(2), node(3), 2),
                        weightedEdgeFromTo(node(3), node(4), 1),
                        weightedEdgeFromTo(node(1), node(2), 1)));

        StrictQueue<Tuple2<Integer, Node<Integer>>> found = dijkstra(monoid(Integer::sum, 0), graph, node(1), 4);
        System.out.println(found);
    }
}