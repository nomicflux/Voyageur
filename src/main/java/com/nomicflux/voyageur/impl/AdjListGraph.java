package com.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.shoki.impl.HashMap;
import com.jnape.palatable.shoki.impl.HashSet;
import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.shoki.impl.HashMap.hashMap;
import static com.jnape.palatable.shoki.impl.HashSet.hashSet;
import static com.nomicflux.voyageur.impl.ValueContext.context;

public final class AdjListGraph<A, N extends Node<A>, E extends Edge<A, N>> implements Graph<A, N, E, HashSet<E>, AdjListGraph<A, N, E>> {
    private final HashMap<N, Tuple2<HashSet<E>, HashSet<E>>> graph;

    private AdjListGraph(HashMap<N, Tuple2<HashSet<E>, HashSet<E>>> graph) {
        this.graph = graph;
    }

    public Boolean isEmpty() {
        return graph.isEmpty();
    }

    public static <A extends Comparable, N extends Node<A>, E extends Edge<A, N>> AdjListGraph<A, N, E> emptyGraph() {
        return new AdjListGraph<A, N, E>(HashMap.<N, Tuple2<HashSet<E>, HashSet<E>>>hashMap());
    }

    public static <A extends Comparable, N extends Node<A>, E extends Edge<A, N>> AdjListGraph<A, N, E> singletonGraph(N a) {
        return new AdjListGraph<A, N, E>(hashMap(tuple(a, tuple(HashSet.<E>hashSet(), HashSet.<E>hashSet()))));
    }

    public static <A extends Comparable, N extends Node<A>, E extends Edge<A, N>> AdjListGraph<A, N, E> fromEdge(E edge) {
        return AdjListGraph.<A, N, E>emptyGraph().addEdge(edge);
    }

    @Override
    public AdjListGraph<A, N, E> addEdge(E edge) {
        HashMap<N, Tuple2<HashSet<E>, HashSet<E>>> withNodes = this.addNode(edge.getNodeFrom()).addNode(edge.getNodeTo()).graph;

        HashMap<N, Tuple2<HashSet<E>, HashSet<E>>> updateFromEdge =
                withNodes
                        .get(edge.getNodeFrom())
                        .fmap(res -> withNodes.put(edge.getNodeFrom(), res.biMapL(l -> l.add(edge))))
                        .orElse(withNodes);
        HashMap<N, Tuple2<HashSet<E>, HashSet<E>>> updateToEdge =
                updateFromEdge
                        .get(edge.getNodeTo())
                        .fmap(res -> updateFromEdge.put(edge.getNodeTo(), res.biMapR(r -> r.add(edge))))
                        .orElse(updateFromEdge);

        return new AdjListGraph<A, N, E>(updateToEdge);
    }

    @Override
    public AdjListGraph<A, N, E> removeEdge(E edge) {

        HashMap<N, Tuple2<HashSet<E>, HashSet<E>>> updateFromEdge =
                graph
                        .get(edge.getNodeFrom())
                        .fmap(res -> graph.put(edge.getNodeFrom(), res.biMapL(l -> l.remove(edge))))
                        .orElse(graph);
        HashMap<N, Tuple2<HashSet<E>, HashSet<E>>> updateToEdge =
                updateFromEdge
                        .get(edge.getNodeTo())
                        .fmap(res -> updateFromEdge.put(edge.getNodeTo(), res.biMapR(r -> r.remove(edge))))
                        .orElse(updateFromEdge);

        return new AdjListGraph<A, N, E>(updateToEdge);
    }

    @Override
    public AdjListGraph<A, N, E> addNode(N node) {
        Maybe<Tuple2<HashSet<E>, HashSet<E>>> tuple2Maybe = graph.get(node);
        return new AdjListGraph<>(tuple2Maybe.match(constantly(graph.put(node, tuple(hashSet(), hashSet()))),
                edges -> graph.put(node, edges)));
    }

    @Override
    public AdjListGraph<A, N, E> removeNode(N node) {
        return new AdjListGraph<>(foldLeft((m, k) -> m
                        .get(k)
                        .match(constantly(m),
                                v -> m.put(k,
                                        tuple(foldLeft((s1, e1) -> e1.getNodeFrom() == node ? s1 : s1.add(e1), hashSet(), v._1()),
                                                foldLeft((s, e) -> e.getNodeTo() == node ? s : s.add(e), hashSet(), v._1())))),
                graph.remove(node),
                graph.remove(node).keys()));
    }

    @Override
    public Maybe<Tuple2<Context<A, N, E, HashSet<E>>, AdjListGraph<A, N, E>>> decompose() {
        return graph.head().flatMap(n -> atNode(n._1()).projectB());
    }

    @Override
    public Choice2<AdjListGraph<A, N, E>, Tuple2<Context<A, N, E, HashSet<E>>, AdjListGraph<A, N, E>>> atNode(N node) {
        Maybe<Tuple2<HashSet<E>, HashSet<E>>> view = graph.get(node);
        return view.match(constantly(Choice2.a(this)),
                res -> {
                    Fn2<AdjListGraph<A, N, E>, E, AdjListGraph<A, N, E>> removeEdge = AdjListGraph::removeEdge;
                    return Choice2.b(tuple(context(node, res._1(), res._2()),
                            foldLeft(removeEdge, this, res._1().union(res._2()))
                                    .removeNode(node)));
                });
    }
}
