package com.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.adt.hlist.Tuple3;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functions.builtin.fn1.Id;
import com.jnape.palatable.lambda.functions.recursion.RecursiveResult;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into3.into3;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.recurse;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.terminate;
import static com.jnape.palatable.lambda.functions.recursion.Trampoline.trampoline;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;

public final class BFPath<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> implements Fn3<G, N, A, StrictQueue<N>> {
    private static BFPath<?, ?, ?, ?, ?> INSTANCE = new BFPath<>();

    private BFPath() {
    }

    @Override
    public StrictQueue<N> checkedApply(G startGraph, N startNode, A a) {
        return trampoline(into3((g, n, v) -> n.head().flatMap(x -> g.atNode(x).projectB().fmap(into((c, g2) -> {
                    RecursiveResult<Tuple3<G, StrictQueue<N>, StrictQueue<N>>, StrictQueue<N>> res =
                            c.getNode().getValue().equals(a)
                                    ? terminate(v.snoc(c.getNode()))
                                    : recurse(tuple(g2, foldLeft((n2, e2) -> n2.cons(e2.getNodeTo()), n.tail(), c.getOutboundEdges()), v.snoc(c.getNode())));
                    return res;
                }))).match(constantly(terminate(strictQueue())),
                Id.id())),
                tuple(startGraph, strictQueue(startNode), StrictQueue.<N>strictQueue()));
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
