package com.nomicflux.voyageur.search;

import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueEdge;
import org.junit.Test;

import static com.nomicflux.voyageur.impl.AdjListGraph.fromEdge;
import static com.nomicflux.voyageur.impl.AdjListGraph.fromEdges;
import static com.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static com.nomicflux.voyageur.search.DFSearch.dfSearch;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DFSearchTest {
    @Test
    public void nothingFoundInEmptyGraph() {
        assertFalse(dfSearch(AdjListGraph.<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>emptyGraph(), node(1), 1));
    }

    @Test
    public void singletonItemFoundInSingletonGraph() {
        assertTrue(dfSearch(AdjListGraph.<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>singletonGraph(node(1)), node(1), 1));
    }

    @Test
    public void singletonItemNotFoundFromOtherNode() {
        assertFalse(dfSearch(AdjListGraph.<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>singletonGraph(node(1)), node(2), 1));
    }

    @Test
    public void otherItemFoundInSingleEdgeGraph() {
        assertTrue(dfSearch(fromEdge(edgeFromTo(node(1), node(2))), node(1), 2));
    }

    @Test
    public void onlyFollowsEdgeOneWay() {
        assertFalse(dfSearch(fromEdge(edgeFromTo(node(1), node(2))), node(2), 1));
    }

    @Test
    public void followsSeveralSteps() {
        assertTrue(dfSearch(fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(7), node(8)),
                edgeFromTo(node(8), node(9)),
                edgeFromTo(node(9), node(10)))),
                node(1), 10));
    }

    @Test
    public void evenWithCycles() {
        assertTrue(dfSearch(fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(2), node(4)),
                edgeFromTo(node(4), node(1)),
                edgeFromTo(node(3), node(1)),
                edgeFromTo(node(3), node(5)))),
                node(1), 5));
    }


    @Test
    public void neverReachesDisconnections() {
        assertFalse(dfSearch(fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(2), node(4)),
                edgeFromTo(node(4), node(1)),
                edgeFromTo(node(3), node(1)),
                edgeFromTo(node(5), node(5)))),
                node(1), 5));
    }
}