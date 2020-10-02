package com.nomicflux.voyageur;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.jnape.palatable.shoki.impl.StrictStack;
import com.nomicflux.voyageur.fold.FoldContinue;
import com.nomicflux.voyageur.impl.AdjListGraph;
import com.nomicflux.voyageur.impl.ValueEdge;
import com.nomicflux.voyageur.impl.ValueNode;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static com.jnape.palatable.shoki.impl.StrictStack.strictStack;
import static com.nomicflux.voyageur.fold.FoldContinue.maybeTerminates;
import static com.nomicflux.voyageur.impl.AdjListGraph.fromChains;
import static com.nomicflux.voyageur.impl.AdjListGraph.fromEdges;
import static com.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {
    @Test
    public void sum() {
        AdjListGraph<Integer, ValueNode<Integer>, ValueEdge<Integer, ValueNode<Integer>>> graph = fromChains(asList(asList(1,2,3,4,5,6,9,10), asList(6,8), asList(6,7)));

        Integer res = graph.<Integer>simpleFold((acc, c) -> acc + c.getNode().getValue(), 0);

        assertEquals(55, res);
    }

    @Test
    public void sumWithCut() {
        AdjListGraph<Integer, ValueNode<Integer>, ValueEdge<Integer, ValueNode<Integer>>> graph = fromChains(asList(asList(1,2,3,4,5,6,9,10), asList(6,8), asList(6,7)));

        Integer res = graph.<Integer>simpleCutFold((acc, c) -> acc + c.getNode().getValue(),
                c -> c.getNode().getValue().equals(6),
                0);

        assertEquals(21, res);
    }

    @Test
    public void dfWholeSum() {
        AdjListGraph<Integer, ValueNode<Integer>, ValueEdge<Integer, ValueNode<Integer>>> graph = fromChains(asList(asList(1,2,3,4,5,6,7,10), asList(6,8), asList(6,9)));

        Integer res = graph.guidedFold(maybeTerminates(StrictStack::head),
                (s, acc, mc) -> mc.match(constantly(s.tail()),
                        c -> foldLeft((a, next) -> a.cons(next.getNodeTo()), s.tail(), c.getOutboundEdges())),
                strictStack(node(1)), (__, acc, c) -> acc + c.getNode().getValue(),
                0);

        assertEquals(55, res);
    }

    @Test
    public void bfWholeSum() {
        AdjListGraph<Integer, ValueNode<Integer>, ValueEdge<Integer, ValueNode<Integer>>> graph = fromChains(asList(asList(1,2,3,4,5,6,7,10), asList(6,8), asList(6,9)));

        Integer res = graph.guidedFold(maybeTerminates(StrictQueue::head),
                (s, acc, mc) -> mc.match(constantly(s.tail()),
                        c -> foldLeft((a, next) -> a.snoc(next.getNodeTo()), s.tail(), c.getOutboundEdges())),
                strictQueue(node(1)), (__, acc, c) -> acc + c.getNode().getValue(),
                0);

        assertEquals(55, res);
    }

    @Test
    public void dfCutSum() {
        AdjListGraph<Integer, ValueNode<Integer>, ValueEdge<Integer, ValueNode<Integer>>> graph = fromChains(asList(asList(1,2,3,4,5,6,7,10), asList(6,8), asList(6,9), asList(9, 10)));

        Integer res = graph.foldG(
                c -> c.getNode().getValue().equals(8),
                maybeTerminates(StrictStack::head),
                (s, acc, mc) -> mc.match(constantly(s.tail()),
                        c -> foldLeft((a, next) -> a.cons(next.getNodeTo()), s.tail(), c.getOutboundEdges())),
                strictStack(node(1)), (__, acc, c) -> acc + c.getNode().getValue(),
                0);

        assertEquals(48, res);
    }

    @Test
    public void bfCutSum() {
        AdjListGraph<Integer, ValueNode<Integer>, ValueEdge<Integer, ValueNode<Integer>>> graph = fromChains(asList(asList(1,2,3,4,5,6,7,10), asList(6,8), asList(6,9), asList(9,10)));

        Integer res = graph.foldG(
                c -> c.getNode().getValue().equals(8),
                maybeTerminates(StrictQueue::head),
                (s, acc, mc) -> mc.match(constantly(s.tail()),
                        c -> foldLeft((a, next) -> a.snoc(next.getNodeTo()), s.tail(), c.getOutboundEdges())),
                strictQueue(node(1)), (__, acc, c) -> acc + c.getNode().getValue(),
                0);

        assertEquals(36, res);
    }
}