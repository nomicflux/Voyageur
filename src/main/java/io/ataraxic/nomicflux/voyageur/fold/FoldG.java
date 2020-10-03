package io.ataraxic.nomicflux.voyageur.fold;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import io.ataraxic.nomicflux.voyageur.Context;
import io.ataraxic.nomicflux.voyageur.Edge;
import io.ataraxic.nomicflux.voyageur.Graph;
import io.ataraxic.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into3.into3;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.recurse;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.terminate;
import static com.jnape.palatable.lambda.functions.recursion.Trampoline.trampoline;
import static io.ataraxic.nomicflux.voyageur.fold.FoldContinue.decompose;

@Deprecated
// Moving methods to {@link Graph} class to reduce type parameter overhead, though this may change based on performance / erogonomic factors
public class FoldG<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S, Acc> {
    private static FoldG<?, ?, ?, ?, ?, ?, ?> INSTANCE = new FoldG<>();

    public Acc checkedApply(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                            Fn1<S, FoldContinue<N>> contextGetter,
                            Fn3<S, Acc, Context<A, N, E, I>, S> updateState,
                            S defState,
                            Fn3<S, Acc, Context<A, N, E, I>, Acc> accumulator,
                            Acc defAcc,
                            G graph) {
        return trampoline(into3((G g, S state, Acc acc) -> {
                    FoldContinue<N> apply = contextGetter.apply(state);
                    Maybe<Tuple2<Context<A, N, E, I>, G>> match = apply.match(constantly(nothing()),
                            constantly(g.decompose()),
                            n -> g.atNode(n).projectB());
                    return match
                            .match(constantly(terminate(acc)),
                                    c -> destinationCheck.apply(c._1())
                                            ? terminate(accumulator.apply(state, acc, c._1()))
                                            : recurse(tuple(c._2(), updateState.apply(state, acc, c._1()), accumulator.apply(state, acc, c._1()))));
                }),
                tuple(graph, defState, defAcc));
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S, Acc>
    Acc foldG(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
              Fn1<S, FoldContinue<N>> contextGetter,
              Fn3<S, Acc, Context<A, N, E, I>, S> updateState,
              S defState,
              Fn3<S, Acc, Context<A, N, E, I>, Acc> accumulator,
              Acc defAcc,
              G graph) {
        return ((FoldG<A, N, E, I, G, S, Acc>) INSTANCE).checkedApply(destinationCheck, contextGetter, updateState, defState, accumulator, defAcc, graph);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S, Acc>
    Acc guidedCutFold(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                      Fn1<S, FoldContinue<N>> contextGetter,
                      Fn3<S, Acc, Context<A, N, E, I>, S> updateState,
                      S defState, Fn3<S, Acc, Context<A, N, E, I>, Acc> accumulator,
                      Acc defAcc,
                      G graph) {
        return ((FoldG<A, N, E, I, G, S, Acc>) INSTANCE).checkedApply(destinationCheck, contextGetter, updateState, defState, accumulator, defAcc, graph);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S, Acc>
    Acc guidedFold(Fn1<S, FoldContinue<N>> contextGetter,
                   Fn3<S, Acc, Context<A, N, E, I>, S> updateState,
                   S defState, Fn3<S, Acc, Context<A, N, E, I>, Acc> accumulator,
                   Acc defAcc,
                   G graph) {
        return ((FoldG<A, N, E, I, G, S, Acc>) INSTANCE).checkedApply(constantly(false), contextGetter, updateState, defState, accumulator, defAcc, graph);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, Acc>
    Acc simpleCutFold(Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                      Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                      Acc defAcc,
                      G graph) {
        return ((FoldG<A, N, E, I, G, Unit, Acc>) INSTANCE).checkedApply(destinationCheck, constantly(decompose()), (_s, _acc, _c) -> UNIT, UNIT, (__, acc, c) -> accumulator.apply(acc, c), defAcc, graph);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, Acc>
    Acc simpleFold(Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                   Acc defAcc,
                   G graph) {
        return ((FoldG<A, N, E, I, G, Unit, Acc>) INSTANCE).checkedApply(constantly(false), constantly(decompose()), (_s, _acc, _c) -> UNIT, UNIT, (__, acc, c) -> accumulator.apply(acc, c), defAcc, graph);
    }
}
