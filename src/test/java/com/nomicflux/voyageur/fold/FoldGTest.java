package com.nomicflux.voyageur.fold;

import com.jnape.palatable.shoki.impl.StrictQueue;
import com.jnape.palatable.shoki.impl.StrictStack;
import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueEdge;
import org.junit.Test;

import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.nomicflux.voyageur.fold.FoldG.*;
import static com.nomicflux.voyageur.impl.AdjListGraph.fromEdges;
import static com.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FoldGTest {
    @Test
    public void sum() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(6), node(8)),
                edgeFromTo(node(6), node(9)),
                edgeFromTo(node(9), node(10))));

        Integer res = simpleFold((acc, c) -> acc + c.getNode().getValue(), 0, graph);

        assertEquals(55, res);
    }

    @Test
    public void sumWithCut() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(6), node(8)),
                edgeFromTo(node(6), node(9)),
                edgeFromTo(node(9), node(10))));

        Integer res = simpleCutFold((acc, c) -> acc + c.getNode().getValue(), c -> c.getNode().getValue() == 6, 0, graph);

        assertEquals(21, res);
    }

    @Test
    public void dfWholeSum() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(6), node(8)),
                edgeFromTo(node(6), node(9)),
                edgeFromTo(node(7), node(10))));

        Integer res = guidedFold(StrictStack::head,
                (s, c) -> s.cons(c.getNode()),
                (acc, c) -> acc + c.getNode().getValue(),
                0,
                StrictStack.<Node<Integer>>strictStack(node(1)),
                graph);

        assertEquals(55, res);
    }

    @Test
    public void bfWholeSum() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(6), node(8)),
                edgeFromTo(node(6), node(9)),
                edgeFromTo(node(7), node(10))));

        Integer res = guidedFold(StrictQueue::head,
                (s, c) -> s.cons(c.getNode()),
                (acc, c) -> acc + c.getNode().getValue(),
                0,
                StrictQueue.<Node<Integer>>strictQueue(node(1)),
                graph);

        assertEquals(55, res);
    }

    @Test
    public void dfCutSum() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(6), node(8)),
                edgeFromTo(node(6), node(9)),
                edgeFromTo(node(7), node(10)),
                edgeFromTo(node(9), node(10))));

        Integer res = guidedCutFold(
                c -> c.getNode().getValue() == 8,
                StrictStack::head,
                (s, c) -> foldLeft((acc, next) -> acc.cons(next.getNodeTo()), s.tail(), c.getOutboundEdges()),
                (acc, c) -> acc + c.getNode().getValue(),
                0,
                StrictStack.<Node<Integer>>strictStack(node(1)),
                graph);

        assertEquals(48, res);
    }

    @Test
    public void bfCutSum() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(6), node(8)),
                edgeFromTo(node(6), node(9)),
                edgeFromTo(node(7), node(10)),
                edgeFromTo(node(9), node(10))));

        Integer res = guidedCutFold(
                c -> c.getNode().getValue() == 8,
                StrictQueue::head,
                (s, c) -> foldLeft((acc, next) -> acc.snoc(next.getNodeTo()), s.tail(), c.getOutboundEdges()),
                (acc, c) -> acc + c.getNode().getValue(),
                0,
                StrictQueue.<Node<Integer>>strictQueue(node(1)),
                graph);

        assertEquals(36, res);
    }
}