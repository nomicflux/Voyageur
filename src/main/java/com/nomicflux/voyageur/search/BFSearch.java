package com.nomicflux.voyageur.search;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;

public final class BFSearch<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> extends Search<A, N, E, I, G, StrictQueue<N>> {
    private static BFSearch<?, ?, ?, ?, ?> INSTANCE = new BFSearch<>();

    private BFSearch() {
    }

    @Override
    Maybe<N> nextNode(StrictQueue<N> ns) {
        return ns.head();
    }

    @Override
    StrictQueue<N> nextState(StrictQueue<N> s, Maybe<Context<A, N, E, I>> mc) {
        return mc.match(constantly(s.tail()),
                c -> foldLeft((acc, next) -> acc.snoc(next.getNodeTo()), s.tail(), c.getOutboundEdges()));
    }

    @Override
    StrictQueue<N> startingState(N startNode) {
        return StrictQueue.<N>strictQueue(startNode);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn3<G, N, A, Boolean> bfSearch() {
        return (BFSearch<A, N, E, I, G>) INSTANCE;
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn2<N, A, Boolean> bfSearch(G graph) {
        return BFSearch.<A, N, E, I, G>bfSearch().apply(graph);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn1<A, Boolean> bfSearch(G graph, N node) {
        return bfSearch(graph).apply(node);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Boolean bfSearch(G graph, N node, A a) {
        return bfSearch(graph, node).apply(a);
    }
}
