package io.ataraxic.nomicflux.voyageur.path;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.shoki.impl.StrictQueue;
import io.ataraxic.nomicflux.voyageur.Edge;
import io.ataraxic.nomicflux.voyageur.Graph;
import io.ataraxic.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static com.jnape.palatable.shoki.impl.StrictQueue.strictQueue;
import static io.ataraxic.nomicflux.voyageur.fold.FoldContinue.nodeOrTerminate;

public final class BFPath<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> implements Fn3<G, N, A, StrictQueue<Tuple2<Maybe<E>, N>>> {
    private static BFPath<?, ?, ?, ?, ?> INSTANCE = new BFPath<>();

    private BFPath() {
    }

    @Override
    public StrictQueue<Tuple2<Maybe<E>, N>> checkedApply(G startGraph, N startNode, A a) {
        return startGraph.<Tuple2<Maybe<E>, StrictQueue<Tuple2<Maybe<E>, N>>>, StrictQueue<Tuple2<Maybe<E>, N>>>guidedCutFold(c -> c.getNode().getValue().equals(a),
                state(s -> tuple(nodeOrTerminate(s._2().head().fmap(Tuple2::_2)), tuple(s._2().head().flatMap(Tuple2::_1), s._2().tail()))),
                (acc, c) -> state(s -> tuple(acc.snoc(tuple(s._1(), c.getNode())),
                        tuple(s._1(), foldLeft((s_, next) -> s_.snoc(tuple(just(next), next.getNodeTo())), s._2(), c.getOutboundEdges())))),
                tuple(nothing(), strictQueue(tuple(nothing(), startNode))),
                strictQueue());
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn3<G, N, A, StrictQueue<Tuple2<Maybe<E>, N>>> bfPath() {
        return (BFPath<A, N, E, I, G>) INSTANCE;
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn2<N, A, StrictQueue<Tuple2<Maybe<E>, N>>> bfPath(G graph) {
        return BFPath.<A, N, E, I, G>bfPath().apply(graph);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> Fn1<A, StrictQueue<Tuple2<Maybe<E>, N>>> bfPath(G graph, N node) {
        return bfPath(graph).apply(node);
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> StrictQueue<Tuple2<Maybe<E>, N>> bfPath(G graph, N node, A a) {
        return bfPath(graph, node).apply(a);
    }
}
