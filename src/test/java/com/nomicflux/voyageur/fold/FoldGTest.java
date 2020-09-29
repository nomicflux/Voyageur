package com.nomicflux.voyageur.fold;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.shoki.impl.HashSet;
import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueEdge;
import org.junit.Test;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.nomicflux.voyageur.fold.FoldG.simpleFold;
import static com.nomicflux.voyageur.fold.FoldG.simpleCutFold;
import static com.nomicflux.voyageur.impl.AdjListGraph.fromEdges;
import static com.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class FoldGTest {
    @Test
    public void sum() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(7), node(8)),
                edgeFromTo(node(8), node(9)),
                edgeFromTo(node(9), node(10))));

        Integer res = simpleFold((acc, c) -> acc + c.getNode().getValue(), 0, graph);

        assertEquals(res, 55);
    }

    @Test
    public void sumWithCut() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdges(asList(edgeFromTo(node(1), node(2)),
                edgeFromTo(node(2), node(3)),
                edgeFromTo(node(3), node(4)),
                edgeFromTo(node(4), node(5)),
                edgeFromTo(node(5), node(6)),
                edgeFromTo(node(6), node(7)),
                edgeFromTo(node(7), node(8)),
                edgeFromTo(node(8), node(9)),
                edgeFromTo(node(9), node(10))));

        Integer res = simpleCutFold((acc, c) -> acc + c.getNode().getValue(), c -> c.getNode().getValue() == 6, 0, graph);

        assertEquals(res, 21);
    }

}