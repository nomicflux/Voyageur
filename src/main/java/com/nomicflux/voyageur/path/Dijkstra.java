package com.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.adt.Either;
import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functions.Fn4;
import com.jnape.palatable.lambda.io.IO;
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.lambda.monoid.Monoid;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.WeightedEdge;

import java.util.Comparator;
import java.util.PriorityQueue;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.io.IO.io;
import static com.nomicflux.voyageur.fold.FoldContinue.maybeTerminates;

public final class Dijkstra<A, N extends Node<A>, W extends Comparable<W>, E extends WeightedEdge<A, N, W, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> implements Fn4<Monoid<W>, G, N, A, StrictQueue<Tuple2<W, N>>> {
    private static Dijkstra<?, ?, ?, ?, ?, ?> INSTANCE = new Dijkstra<>();

    private Dijkstra() {
    }

    @Override
    // TODO: Replace with Shoki heap impl; highly unsafe as it stands
    public StrictQueue<Tuple2<W, N>> checkedApply(Monoid<W> wMonoid, G startGraph, N startNode, A a) {
        return startGraph.<PriorityQueue<Tuple2<W, N>>, StrictQueue<Tuple2<W, N>>>foldG(c -> c.getNode().getValue().equals(a),
                maybeTerminates(s -> Monad.join(Either.trying(s::peek).fmap(Maybe::maybe).toMaybe()).<N>fmap(Tuple2::_2)),
                (s, acc, mc) -> mc.match(constantly(s),
                        c -> {
                            W current = Monad.join(Either.trying(s::peek).fmap(Maybe::maybe).toMaybe()).<W>fmap(Tuple2::_1).orElse(wMonoid.identity());
                            return foldLeft((IO<PriorityQueue<Tuple2<W, N>>> ac, E next) -> ac
                                            .flatMap(s_ -> io(() -> s_.add(tuple(wMonoid.apply(current, next.getWeight()), next.getNodeTo())))
                                                    .safe().fmap(constantly(s_))),
                                    io(() -> s),
                                    c.getOutboundEdges())
                                    .unsafePerformIO();
                        }),
                new PriorityQueue<Tuple2<W, N>>(Comparator.comparing(Tuple2::_1)) {{
                    add(tuple(wMonoid.identity(), startNode));
                }},
                (s, acc, c) -> acc.snoc(Either.trying(s::poll).toMaybe().flatMap(Maybe::maybe).orElse(tuple(wMonoid.identity(), c.getNode()))),
                StrictQueue.<Tuple2<W, N>>strictQueue());
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, W extends Comparable<W>, E extends WeightedEdge<A, N, W, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn4<Monoid<W>, G, N, A, StrictQueue<Tuple2<W, N>>> dijkstra() {
        return (Dijkstra<A, N, W, E, I, G>) INSTANCE;
    }

    public static <A, N extends Node<A>, W extends Comparable<W>, E extends WeightedEdge<A, N, W, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn3<G, N, A, StrictQueue<Tuple2<W, N>>> dijkstra(Monoid<W> wMonoid) {
        return Dijkstra.<A, N, W, E, I, G>dijkstra().apply(wMonoid);
    }

    public static <A, N extends Node<A>, W extends Comparable<W>, E extends WeightedEdge<A, N, W, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn2<N, A, StrictQueue<Tuple2<W, N>>> dijkstra(Monoid<W> wMonoid, G graph) {
        return Dijkstra.<A, N, W, E, I, G>dijkstra(wMonoid).apply(graph);
    }

    public static <A, N extends Node<A>, W extends Comparable<W>, E extends WeightedEdge<A, N, W, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn1<A, StrictQueue<Tuple2<W, N>>> dijkstra(Monoid<W> wMonoid, G graph, N node) {
        return dijkstra(wMonoid, graph).apply(node);
    }

    public static <A, N extends Node<A>, W extends Comparable<W>, E extends WeightedEdge<A, N, W, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> StrictQueue<Tuple2<W, N>> dijkstra(Monoid<W> wMonoid, G graph, N node, A a) {
        return dijkstra(wMonoid, graph, node).apply(a);
    }
}
