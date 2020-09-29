package com.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueEdge;
import org.junit.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.nomicflux.voyageur.impl.AdjListGraph.fromEdge;
import static com.nomicflux.voyageur.impl.AdjListGraph.fromEdges;
import static com.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static com.nomicflux.voyageur.path.BFPath.bfPath;
import static com.nomicflux.voyageur.search.BFSearch.bfSearch;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class BFPathTest {
    @Test
    public void nothingFoundInEmptyGraph() {
        StrictQueue<Node<Integer>> res = bfPath(AdjListGraph.<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>emptyGraph(), node(1), 1);
        assertTrue(res.isEmpty());
    }

    @Test
    public void singletonItemFoundInSingletonGraph() {
        StrictQueue<Node<Integer>> res = bfPath(AdjListGraph.<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>singletonGraph(node(1)), node(1), 1);
        assertEquals(res.head(), just(node(1)));
        assertEquals(res.tail().head(), nothing());
    }

    @Test
    public void singletonItemNotFoundFromOtherNode() {
        StrictQueue<Node<Integer>> res = bfPath(AdjListGraph.<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>singletonGraph(node(1)), node(2), 1);
        assertTrue(res.isEmpty());
    }

    @Test
    public void otherItemFoundInSingleEdgeGraph() {
        StrictQueue<Node<Integer>> res = bfPath(fromEdge(edgeFromTo(node(1), node(2))), node(1), 2);
        assertEquals(res.head(), just(node(1)));
        assertEquals(res.tail().head(), just(node(2)));
        assertEquals(res.tail().tail().head(), nothing());
    }

    @Test
    // TODO: should a failed search give the path traveled, or an empty path?
    public void onlyFollowsEdgeOneWay() {
        StrictQueue<Node<Integer>> res = bfPath(fromEdge(edgeFromTo(node(1), node(2))), node(2), 1);
        assertEquals(res.head(), just(node(2)));
        assertEquals(res.tail().head(), nothing());
    }

    @Test
    public void followsSeveralSteps() {
        StrictQueue<Node<Integer>> res = bfPath(fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(7), node(8)),
                edgeFromTo(node(8), node(9)),
                edgeFromTo(node(9), node(10)))),
                node(1), 10);
        assertEquals(res.head(), just(node(1)));
        assertEquals(res.tail().head(), just(node(2)));
        assertEquals(res.tail().tail().head(), just(node(3)));
        assertEquals(res.tail().tail().tail().tail().head(), just(node(5)));
        assertEquals(res.tail().tail().tail().tail().tail().tail().tail().tail().head(), just(node(9)));
    }

    @Test
    public void evenWithCycles() {
        StrictQueue<Node<Integer>> res = bfPath(fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(2), node(4)),
                edgeFromTo(node(4), node(1)),
                edgeFromTo(node(3), node(1)),
                edgeFromTo(node(4), node(5)))),
                node(1), 5);
        assertEquals(res.head(), just(node(1)));
        assertEquals(res.tail().head(), just(node(2)));
        assertEquals(res.tail().tail().head(), just(node(3)));
        assertEquals(res.tail().tail().tail().tail().head(), just(node(5)));
    }

}