package com.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static com.nomicflux.voyageur.fold.FoldContinue.nodeOrTerminate;

public final class BFPath<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> implements Fn3<G, N, A, StrictQueue<N>> {
    private static BFPath<?, ?, ?, ?, ?> INSTANCE = new BFPath<>();

    private BFPath() {
    }

    @Override
    public StrictQueue<N> checkedApply(G startGraph, N startNode, A a) {
        return startGraph.guidedCutFold(c -> c.getNode().getValue().equals(a),
                state(s -> tuple(nodeOrTerminate(s.head()), s.tail())),
                (acc, c) -> state(s -> tuple(acc.snoc(c.getNode()), foldLeft((ac, next) -> ac.snoc(next.getNodeTo()), s, c.getOutboundEdges()))),
                strictQueue(startNode),
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
