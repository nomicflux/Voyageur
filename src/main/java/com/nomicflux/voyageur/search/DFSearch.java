package com.nomicflux.voyageur.search;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.shoki.impl.StrictStack;
import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;

public final class DFSearch<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> extends Search<A, N, E, I, G, StrictStack<N>> {
    private static DFSearch<?, ?, ?, ?, ?> INSTANCE = new DFSearch<>();

    private DFSearch() {
    }

    @Override
    Maybe<N> nextNode(StrictStack<N> ns) {
        return ns.head();
    }

    @Override
    StrictStack<N> stateTail(StrictStack<N> s) {
        return s.tail();
    }

    @Override
    StrictStack<N> stateAddNode(StrictStack<N> s, Context<A, N, E, I> c) {
        return foldLeft((acc, next) -> acc.cons(next.getNodeTo()), s, c.getOutboundEdges());
    }

    @Override
    StrictStack<N> startingState(N startNode) {
        return StrictStack.<N>strictStack(startNode);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn3<G, N, A, Boolean> dfSearch() {
        return (DFSearch<A, N, E, I, G>) INSTANCE;
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn2<N, A, Boolean> dfSearch(G graph) {
        return DFSearch.<A, N, E, I, G>dfSearch().apply(graph);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn1<A, Boolean> dfSearch(G graph, N node) {
        return dfSearch(graph).apply(node);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Boolean dfSearch(G graph, N node, A a) {
        return dfSearch(graph, node).apply(a);
    }
}
