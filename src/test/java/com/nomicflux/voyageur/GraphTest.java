package com.nomicflux.voyageur;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.jnape.palatable.shoki.impl.StrictStack;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueNode;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static com.jnape.palatable.shoki.impl.StrictStack.strictStack;
import static com.nomicflux.voyageur.fold.FoldContinue.nodeOrTerminate;
import static com.nomicflux.voyageur.impl.AdjListGraph.fromChains;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphTest {
    @Test
    public void sum() {
        AdjListGraph<Integer, Unit, Unit> graph = fromChains(asList(asList(1, 2, 3, 4, 5, 6, 9, 10), asList(6, 8), asList(6, 7)));

        Integer res = graph.<Integer>simpleFold((acc, c) -> acc + c.getNode().getValue(), 0);

        assertEquals(55, res);
    }

    @Test
    public void dfWholeSum() {
        AdjListGraph<Integer, Unit, Unit> graph = fromChains(asList(asList(1, 2, 3, 4, 5, 6, 7, 10), asList(6, 8), asList(6, 9)));

        Integer res = graph.<StrictStack<ValueNode<Integer, Unit>>, Integer>guidedFold(state(s -> tuple(nodeOrTerminate(s.head()), s.tail())),
                (acc, c) -> state(s -> tuple(acc + c.getNode().getValue(), foldLeft((a, next) -> a.cons(next.getNodeTo()), s, c.getOutboundEdges()))),
                strictStack(node(1)),
                0);

        assertEquals(55, res);
    }

    @Test
    public void bfWholeSum() {
        AdjListGraph<Integer, Unit, Unit> graph = fromChains(asList(asList(1, 2, 3, 4, 5, 6, 7, 10), asList(6, 8), asList(6, 9)));

        Integer res = graph.<StrictQueue<ValueNode<Integer, Unit>>, Integer>guidedFold(state(s -> tuple(nodeOrTerminate(s.head()), s.tail())),
                (acc, c) -> state(s -> tuple(acc + c.getNode().getValue(), foldLeft((a, next) -> a.snoc(next.getNodeTo()), s, c.getOutboundEdges()))),
                strictQueue(node(1)),
                0);

        assertEquals(55, res);
    }

    @Test
    public void dfCutSum() {
        AdjListGraph<Integer, Unit, Unit> graph = fromChains(asList(asList(1, 2, 3, 4, 5, 6, 7, 10), asList(6, 8), asList(6, 9), asList(9, 10)));

        Integer res = graph.<StrictStack<ValueNode<Integer, Unit>>, Integer>guidedCutFold(
                c -> c.getNode().getValue().equals(8),
                state(s -> tuple(nodeOrTerminate(s.head()), s.tail())),
                (acc, c) -> state(s -> tuple(acc + c.getNode().getValue(), foldLeft((a, next) -> a.cons(next.getNodeTo()), s, c.getOutboundEdges()))),
                strictStack(node(1)),
                0);

        assertEquals(48, res);
    }

    @Test
    public void bfCutSum() {
        AdjListGraph<Integer, Unit, Unit> graph = fromChains(asList(asList(1, 2, 3, 4, 5, 6, 7, 10), asList(6, 8), asList(6, 9), asList(9, 10)));

        Integer res = graph.<StrictQueue<ValueNode<Integer, Unit>>, Integer>guidedCutFold(
                c -> c.getNode().getValue().equals(8),
                state(s -> tuple(nodeOrTerminate(s.head()), s.tail())),
                (acc, c) -> state(s -> tuple(acc + c.getNode().getValue(), foldLeft((a, next) -> a.snoc(next.getNodeTo()), s, c.getOutboundEdges()))),
                strictQueue(node(1)),
                0);

        assertEquals(36, res);
    }
}