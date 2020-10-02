package com.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.jnape.palatable.shoki.impl.StrictStack;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static com.nomicflux.voyageur.fold.FoldContinue.nodeOrTerminate;

public final class DFPath<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> implements Fn3<G, N, A, StrictQueue<N>> {
    private static DFPath<?, ?, ?, ?, ?> INSTANCE = new DFPath<>();

    private DFPath() {
    }

    @Override
    public StrictQueue<N> checkedApply(G startGraph, N startNode, A a) {
        return startGraph.guidedCutFold(c -> c.getNode().getValue().equals(a),
                state(s -> tuple(nodeOrTerminate(s.head()), s.tail())),
                (acc, c) -> state(s -> tuple(acc.snoc(c.getNode()), foldLeft((ac, next) -> ac.cons(next.getNodeTo()), s, c.getOutboundEdges()))),
                StrictStack.<N>strictStack(startNode),
                StrictQueue.<N>strictQueue());
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn3<G, N, A, StrictQueue<N>> dfPath() {
        return (DFPath<A, N, E, I, G>) INSTANCE;
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn2<N, A, StrictQueue<N>> dfPath(G graph) {
        return DFPath.<A, N, E, I, G>dfPath().apply(graph);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn1<A, StrictQueue<N>> dfPath(G graph, N node) {
        return dfPath(graph).apply(node);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> StrictQueue<N> dfPath(G graph, N node, A a) {
        return dfPath(graph, node).apply(a);
    }
}
