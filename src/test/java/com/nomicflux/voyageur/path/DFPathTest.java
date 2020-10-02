package com.nomicflux.voyageur.path;

import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueEdge;
import com.nomicflux.voyageur.impl.ValueNode;
import org.junit.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.nomicflux.voyageur.impl.AdjListGraph.*;
import static com.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static com.nomicflux.voyageur.path.DFPath.dfPath;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DFPathTest {
    @Test
    public void nothingFoundInEmptyGraph() {
        StrictQueue<Node<Integer>> res = dfPath(AdjListGraph.<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>emptyGraph(), node(1), 1);
        assertTrue(res.isEmpty());
    }

    @Test
    public void singletonItemFoundInSingletonGraph() {
        StrictQueue<Node<Integer>> res = dfPath(AdjListGraph.<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>singletonGraph(node(1)), node(1), 1);
        assertEquals(res.head(), just(node(1)));
        assertEquals(res.tail().head(), nothing());
    }

    @Test
    public void singletonItemNotFoundFromOtherNode() {
        StrictQueue<Node<Integer>> res = dfPath(AdjListGraph.<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>singletonGraph(node(1)), node(2), 1);
        assertTrue(res.isEmpty());
    }

    @Test
    public void otherItemFoundInSingleEdgeGraph() {
        StrictQueue<Node<Integer>> res = dfPath(fromEdge(edgeFromTo(node(1), node(2))), node(1), 2);
        assertEquals(res.head(), just(node(1)));
        assertEquals(res.tail().head(), just(node(2)));
        assertEquals(res.tail().tail().head(), nothing());
    }

    @Test
    // TODO: should a failed search give the path traveled, or an empty path?
    public void onlyFollowsEdgeOneWay() {
        StrictQueue<Node<Integer>> res = dfPath(fromEdge(edgeFromTo(node(1), node(2))), node(2), 1);
        assertEquals(res.head(), just(node(2)));
        assertEquals(res.tail().head(), nothing());
    }

    @Test
    public void followsSeveralSteps() {
        StrictQueue<ValueNode<Integer>> res = dfPath(fromChain(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                node(1), 10);
        assertEquals(res.head(), just(node(1)));
        assertEquals(res.tail().head(), just(node(2)));
        assertEquals(res.tail().tail().head(), just(node(3)));
        assertEquals(res.tail().tail().tail().tail().head(), just(node(5)));
        assertEquals(res.tail().tail().tail().tail().tail().tail().tail().tail().head(), just(node(9)));
    }

    @Test
    public void evenWithCycles() {
        StrictQueue<ValueNode<Integer>> res = dfPath(fromChains(asList(asList(1, 2, 3, 1), asList(2, 4, 1), asList(4, 5))),
                node(1), 5);
        assertEquals(res.head(), just(node(1)));
        assertEquals(res.tail().head(), just(node(2)));
        assertEquals(res.tail().tail().head(), just(node(4)));
        assertEquals(res.tail().tail().tail().head(), just(node(5)));
    }

    @Test
    public void catchesRepeatsInQueue() {
        AdjListGraph<Integer, ValueNode<Integer>, ValueEdge<Integer, ValueNode<Integer>>> graph = fromChains(asList(asList(0, 2, 4, 6, 8, 10), asList(1, 3, 5, 7, 9), asList(0, 3, 6, 9), asList(0, 5, 10)));
        StrictQueue<ValueNode<Integer>> res = dfPath(graph, node(0), 10);
        assertEquals(res.reverse().head(), just(node(10)));
    }
}