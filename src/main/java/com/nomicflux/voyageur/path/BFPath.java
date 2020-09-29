package com.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.nomicflux.voyageur.fold.FoldG.foldG;

public final class BFPath<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> implements Fn3<G, N, A, StrictQueue<N>> {
    private static BFPath<?, ?, ?, ?, ?> INSTANCE = new BFPath<>();

    private BFPath() {
    }

    @Override
    public StrictQueue<N> checkedApply(G startGraph, N startNode, A a) {
        return foldG(c -> c.getNode().getValue() == a,
                StrictQueue::head,
                constantly(false),
                (s, c) -> foldLeft((acc, next) -> acc.snoc(next.getNodeTo()), s.tail(), c.getOutboundEdges()),
                (acc, c) -> acc.snoc(c.getNode()),
                StrictQueue.<N>strictQueue(),
                StrictQueue.<N>strictQueue(startNode),
                startGraph);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn3<G, N, A, StrictQueue<N>> bfPath() {
        return (BFPath<A, N, E, I, G>) INSTANCE;
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn2<N, A, StrictQueue<N>> bfPath(G graph) {
        return BFPath.<A, N, E, I, G>bfPath().apply(graph);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn1<A, StrictQueue<N>> bfPath(G graph, N node) {
        return bfPath(graph).apply(node);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> StrictQueue<N> bfPath(G graph, N node, A a) {
        return bfPath(graph, node).apply(a);
    }
}
