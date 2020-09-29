package com.nomicflux.voyageur.fold;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.Fn2.fn2;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into3.into3;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.recurse;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.terminate;
import static com.jnape.palatable.lambda.functions.recursion.Trampoline.trampoline;

public class FoldG<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S, Acc> {
    private static FoldG<?, ?, ?, ?, ?, ?, ?> INSTANCE = new FoldG<>();

    public Acc checkedApply(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                            Fn1<S, Maybe<N>> contextGetter,
                            Fn2<S, Context<A, N, E, I>, S> whereTo,
                            Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                            Acc defAcc,
                            S defState,
                            G graph) {
        return trampoline(into3((g, state, acc) -> contextGetter.apply(state)
                        .flatMap(n -> g.atNode(n).projectB())
                        .match(constantly(g.decompose()),
                                Maybe::just)
                        .match(constantly(terminate(acc)),
                                x -> destinationCheck.apply(x._1())
                                        ? terminate(accumulator.apply(acc, x._1()))
                                        : recurse(tuple(x._2(), whereTo.apply(state, x._1()), accumulator.apply(acc, x._1()))))),
                tuple(graph, defState, defAcc));
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S, Acc>
    Acc foldG(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
              Fn1<S, Maybe<N>> contextGetter,
              Fn2<S, Context<A, N, E, I>, S> whereTo,
              Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
              Acc defAcc,
              S defState,
              G graph) {
        return guidedCutFold(destinationCheck, contextGetter, whereTo, accumulator, defAcc, defState, graph);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S, Acc>
    Acc guidedCutFold(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                      Fn1<S, Maybe<N>> contextGetter,
                      Fn2<S, Context<A, N, E, I>, S> whereTo,
                      Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                      Acc defAcc,
                      S defState,
                      G graph) {
        return ((FoldG<A, N, E, I, G, S, Acc>) INSTANCE).checkedApply(destinationCheck, contextGetter, whereTo, accumulator, defAcc, defState, graph);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S, Acc>
    Acc guidedFold(Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                   Fn1<S, Maybe<N>> contextGetter,
                   Fn2<S, Context<A, N, E, I>, S> whereTo,
                   Acc defAcc,
                   S defState,
                   G graph) {
        return ((FoldG<A, N, E, I, G, S, Acc>) INSTANCE).checkedApply(constantly(true), contextGetter, whereTo, accumulator, defAcc, defState, graph);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, Acc>
    Acc simpleCutFold(Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                      Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                      Acc defAcc,
                      G graph) {
        return ((FoldG<A, N, E, I, G, Unit, Acc>) INSTANCE).checkedApply(destinationCheck, constantly(nothing()), fn2((__, ___) -> UNIT), accumulator, defAcc, UNIT, graph);
    }

    @SuppressWarnings("unchecked")
    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, Acc>
    Acc simpleFold(Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                   Acc defAcc,
                   G graph) {
        return ((FoldG<A, N, E, I, G, Unit, Acc>) INSTANCE).checkedApply(constantly(true), constantly(nothing()), fn2((__, ___) -> UNIT), accumulator, defAcc, UNIT, graph);
    }
}
