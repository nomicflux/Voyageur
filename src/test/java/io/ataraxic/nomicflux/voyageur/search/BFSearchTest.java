package io.ataraxic.nomicflux.voyageur.search;

import com.jnape.palatable.lambda.adt.Unit;
import io.ataraxic.nomicflux.voyageur.impl.AdjListGraph;
import org.junit.jupiter.api.Test;

import static io.ataraxic.nomicflux.voyageur.impl.AdjListGraph.*;
import static io.ataraxic.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static io.ataraxic.nomicflux.voyageur.impl.ValueNode.node;
import static io.ataraxic.nomicflux.voyageur.search.BFSearch.bfSearch;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BFSearchTest {
    @Test
    public void nothingFoundInEmptyGraph() {
        assertFalse(bfSearch(AdjListGraph.<Integer, Unit, Unit>emptyGraph(), node(1), 1));
    }

    @Test
    public void singletonItemFoundInSingletonGraph() {
        assertTrue(bfSearch(AdjListGraph.<Integer, Unit, Unit>singletonGraph(node(1)), node(1), 1));
    }

    @Test
    public void singletonItemNotFoundFromOtherNode() {
        assertFalse(bfSearch(AdjListGraph.<Integer, Unit, Unit>singletonGraph(node(1)), node(2), 1));
    }

    @Test
    public void otherItemFoundInSingleEdgeGraph() {
        assertTrue(bfSearch(fromEdge(edgeFromTo(node(1), node(2))), node(1), 2));
    }

    @Test
    public void onlyFollowsEdgeOneWay() {
        assertFalse(bfSearch(fromEdge(edgeFromTo(node(1), node(2))), node(2), 1));
    }

    @Test
    public void followsSeveralSteps() {
        assertTrue(bfSearch(fromChain(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                node(1), 10));
    }

    @Test
    public void evenWithCycles() {
        assertTrue(bfSearch(fromChains(asList(asList(1, 2, 3, 5), asList(2, 4, 1), asList(3, 1))),
                node(1), 5));
    }


    @Test
    public void neverReachesDisconnections() {
        assertFalse(bfSearch(fromChains(asList(asList(1, 2, 3, 1), asList(2, 4, 1), asList(5, 5))),
                node(1), 5));
    }
}