package com.nomicflux.voyageur.search;

import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueEdge;
import org.junit.Test;

import static com.nomicflux.voyageur.impl.AdjListGraph.*;
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
        assertTrue(dfSearch(fromChain(asList(1,2,3,4,5,6,7,8,9,10)),
                node(1), 10));
    }

    @Test
    public void evenWithCycles() {
        assertTrue(dfSearch(fromChains(asList(asList(1,2,3,5), asList(2,4,1), asList(3,1))),
                node(1), 5));
    }


    @Test
    public void neverReachesDisconnections() {
        assertFalse(dfSearch(fromChains(asList(asList(1,2,3,1), asList(2,4,1), asList(5,5))),
                node(1), 5));
    }
}