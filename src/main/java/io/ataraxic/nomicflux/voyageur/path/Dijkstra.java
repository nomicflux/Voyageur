package io.ataraxic.nomicflux.voyageur.path;

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
import io.ataraxic.nomicflux.voyageur.Graph;
import io.ataraxic.nomicflux.voyageur.Node;
import io.ataraxic.nomicflux.voyageur.WeightedEdge;

import java.util.Comparator;
import java.util.PriorityQueue;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Tupler2.tupler;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static com.jnape.palatable.lambda.io.IO.io;
import static io.ataraxic.nomicflux.voyageur.fold.FoldContinue.nodeOrTerminate;

public final class Dijkstra<A, N extends Node<A>, W extends Comparable<W>, E extends WeightedEdge<A, N, W, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> implements Fn4<Monoid<W>, G, N, A, StrictQueue<Tuple2<W, N>>> {
    private static Dijkstra<?, ?, ?, ?, ?, ?> INSTANCE = new Dijkstra<>();

    private Dijkstra() {
    }

    @Override
    // TODO: Replace with Shoki heap impl; highly unsafe as it stands
    public StrictQueue<Tuple2<W, N>> checkedApply(Monoid<W> wMonoid, G startGraph, N startNode, A a) {
        return startGraph.<Tuple2<Maybe<Tuple2<W, N>>, PriorityQueue<Tuple2<W, N>>>, StrictQueue<Tuple2<W, N>>>guidedCutFold(c -> c.getNode().getValue().equals(a),
                state(s -> io(() -> Monad.join(Either.trying(() -> s._2().poll()).fmap(Maybe::maybe).toMaybe()))
                        .fmap(t -> tuple(nodeOrTerminate(t.fmap(Tuple2::_2)), tuple(t, s._2())))
                        .unsafePerformIO()),
                (acc, c) -> state(s -> tuple(acc.snoc(s._1().orElse(tuple(wMonoid.identity(), c.getNode()))),
                        foldLeft((IO<PriorityQueue<Tuple2<W, N>>> ac, E next) -> ac
                                        .flatMap(s_ -> io(() -> s_.add(tuple(wMonoid.apply(s._1().fmap(Tuple2::_1).orElse(wMonoid.identity()), next.getWeight()), next.getNodeTo())))
                                                .safe().fmap(constantly(s_))),
                                io(s::_2),
                                c.getOutboundEdges())
                                .fmap(tupler(s._1()))
                                .unsafePerformIO())),
                tuple(nothing(), new PriorityQueue<Tuple2<W, N>>(Comparator.comparing(Tuple2::_1)) {{
                    add(tuple(wMonoid.identity(), startNode));
                }}),
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
