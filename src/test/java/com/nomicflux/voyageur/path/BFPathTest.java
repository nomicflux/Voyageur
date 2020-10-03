package com.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueEdge;
import com.nomicflux.voyageur.impl.ValueNode;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.nomicflux.voyageur.impl.AdjListGraph.*;
import static com.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static com.nomicflux.voyageur.path.BFPath.bfPath;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static testsupport.matchers.IterableMatcher.iterates;

public class BFPathTest {
    @Test
    public void nothingFoundInEmptyGraph() {
        StrictQueue<Tuple2<Maybe<ValueEdge<Integer, Unit, Unit>>, ValueNode<Integer, Unit>>> res = bfPath(AdjListGraph.<Integer, Unit, Unit>emptyGraph(), node(1), 1);
        assertTrue(res.isEmpty());
    }

    @Test
    public void singletonItemFoundInSingletonGraph() {
        StrictQueue<Tuple2<Maybe<ValueEdge<Integer, Unit, Unit>>, ValueNode<Integer, Unit>>> res = bfPath(AdjListGraph.<Integer, Unit, Unit>singletonGraph(node(1)), node(1), 1);
        assertEquals(res.head(), just(tuple(nothing(), node(1))));
        assertEquals(res.tail().head(), nothing());
    }

    @Test
    public void singletonItemNotFoundFromOtherNode() {
        StrictQueue<Tuple2<Maybe<ValueEdge<Integer, Unit, Unit>>, ValueNode<Integer, Unit>>> res = bfPath(AdjListGraph.<Integer, Unit, Unit>singletonGraph(node(1)), node(2), 1);
        assertTrue(res.isEmpty());
    }

    @Test
    public void otherItemFoundInSingleEdgeGraph() {
        StrictQueue<Tuple2<Maybe<ValueEdge<Integer, Unit, Unit>>, ValueNode<Integer, Unit>>> res = bfPath(fromEdge(edgeFromTo(node(1), node(2))), node(1), 2);
        assertEquals(res.head(), just(tuple(nothing(), node(1))));
        assertEquals(res.tail().head(), just(tuple(just(edgeFromTo(node(1), node(2))), node(2))));
        assertEquals(res.tail().tail().head(), nothing());
    }

    @Test
    // TODO: should a failed search give the path traveled, or an empty path?
    public void onlyFollowsEdgeOneWay() {
        StrictQueue<Tuple2<Maybe<ValueEdge<Integer, Unit, Unit>>, ValueNode<Integer, Unit>>> res = bfPath(fromEdge(edgeFromTo(node(1), node(2))), node(2), 1);
        assertEquals(res.head(), just(tuple(nothing(), node(2))));
        assertEquals(res.tail().head(), nothing());
    }

    @Test
    public void followsSeveralSteps() {
        StrictQueue<Tuple2<Maybe<ValueEdge<Integer, Unit, Unit>>, ValueNode<Integer, Unit>>> res = bfPath(fromChain(asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)),
                node(1), 10);
        assertEquals(res.head(), just(tuple(nothing(), node(1))));
        assertEquals(res.tail().head(), just(tuple(just(edgeFromTo(node(1), node(2))), node(2))));
        assertEquals(res.tail().tail().head(), just(tuple(just(edgeFromTo(node(2), node(3))), node(3))));
        assertEquals(res.tail().tail().tail().tail().head(), just(tuple(just(edgeFromTo(node(4), node(5))), node(5))));
        assertEquals(res.tail().tail().tail().tail().tail().tail().tail().tail().head(), just(tuple(just(edgeFromTo(node(8), node(9))), node(9))));
    }

    @Test
    public void evenWithCycles() {
        StrictQueue<Tuple2<Maybe<ValueEdge<Integer, Unit, Unit>>, ValueNode<Integer, Unit>>> res = bfPath(fromChains(asList(asList(1, 2, 3, 1), asList(2, 4, 1), asList(4, 5))),
                node(1), 5);
        assertThat(res, iterates(tuple(nothing(), node(1)),
                tuple(just(edgeFromTo(node(1), node(2))), node(2)),
                tuple(just(edgeFromTo(node(2), node(3))), node(3)),
                tuple(just(edgeFromTo(node(2), node(4))), node(4)),
                tuple(just(edgeFromTo(node(4), node(5))), node(5))));
    }

    @Test
    public void catchesRepeatsInQueue() {
        AdjListGraph<Integer, Unit, Unit> graph = fromChains(asList(asList(0, 2, 4, 6, 8, 10), asList(1, 3, 5, 7, 9), asList(0, 3, 6, 9), asList(0, 5, 10)));
        StrictQueue<Tuple2<Maybe<ValueEdge<Integer, Unit, Unit>>, ValueNode<Integer, Unit>>> res = bfPath(graph, node(0), 10);
        assertEquals(just(tuple(just(edgeFromTo(node(5), node(10))), node(10))), res.reverse().head());
    }
}