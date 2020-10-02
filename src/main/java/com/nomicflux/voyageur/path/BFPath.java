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
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static com.nomicflux.voyageur.fold.FoldContinue.maybeTerminates;

public final class BFPath<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> implements Fn3<G, N, A, StrictQueue<N>> {
    private static BFPath<?, ?, ?, ?, ?> INSTANCE = new BFPath<>();

    private BFPath() {
    }

    @Override
    public StrictQueue<N> checkedApply(G startGraph, N startNode, A a) {
        return startGraph.foldG(c -> c.getNode().getValue().equals(a),
                maybeTerminates(StrictQueue::head),
                (s, acc, mc) -> mc.match(constantly(s.tail()),
                        c -> foldLeft((ac, next) -> ac.snoc(next.getNodeTo()), s.tail(), c.getOutboundEdges())),
                strictQueue(startNode),
                (__, acc, c) -> acc.snoc(c.getNode()),
                strictQueue());
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
