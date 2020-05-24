package com.nomicflux.voyageur.search;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functions.builtin.fn1.Id;
import com.jnape.palatable.lambda.functions.recursion.RecursiveResult;
import com.jnape.palatable.shoki.impl.StrictStack;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.recurse;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.terminate;
import static com.jnape.palatable.lambda.functions.recursion.Trampoline.trampoline;
import static com.jnape.palatable.shoki.impl.StrictStack.strictStack;

public final class DFSearch<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> implements Fn3<G, N, A, Boolean> {
    private static DFSearch<?, ?, ?, ?, ?> INSTANCE = new DFSearch<>();

    private DFSearch() {
    }

    @Override
    public Boolean checkedApply(G startGraph, N startNode, A a) {
        return trampoline(into((g, n) -> n.head().flatMap(x ->
                        g.atNode(x).projectB().fmap(into((c, g2) -> {
                            RecursiveResult<Tuple2<G, StrictStack<N>>, Boolean> res =
                                    c.getNode().getValue().equals(a)
                                            ? terminate(true)
                                            : recurse(tuple(g2, foldLeft((n2, e2) -> n2.cons(e2.getNodeTo()), n.tail(), c.getOutboundEdges())));
                            return res;
                        })))
                        .match(constantly(terminate(false)),
                                Id.id())),
                tuple(startGraph, strictStack(startNode)));
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
