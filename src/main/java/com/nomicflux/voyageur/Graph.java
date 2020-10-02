package com.nomicflux.voyageur;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.nomicflux.voyageur.fold.FoldContinue;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into3.into3;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.recurse;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.terminate;
import static com.jnape.palatable.lambda.functions.recursion.Trampoline.trampoline;

public interface Graph<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> {
    Boolean isEmpty();

    G addEdge(E edge);

    default Fn1<E, G> addEdge() {
        return this::addEdge;
    }

    G removeEdge(E edge);

    default Fn1<E, G> removeEdge() {
        return this::removeEdge;
    }

    G addNode(N node);

    default Fn1<N, G> addNode() {
        return this::addNode;
    }

    G removeNode(N node);

    default Fn1<N, G> removeNode() {
        return this::removeNode;
    }

    Maybe<Tuple2<Context<A, N, E, I>, G>> decompose();

    Choice2<G, Tuple2<Context<A, N, E, I>, G>> atNode(N node);

    default Fn1<N, Choice2<G, Tuple2<Context<A, N, E, I>, G>>> atNode() {
        return this::atNode;
    }

    @SuppressWarnings("unchecked")
    // TODO: doesn't handle cases where the state S finds a node that was already visited, but isn't empty yet
    default <S, Acc> Acc foldG(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                               Fn1<S, FoldContinue<N>> contextGetter,
                               Fn3<S, Acc, Context<A, N, E, I>, S> updateState,
                               S defState,
                               Fn3<S, Acc, Context<A, N, E, I>, Acc> accumulator,
                               Acc defAcc) {
        return trampoline(into3((G g, S state, Acc acc) -> {
                    return contextGetter.apply(state)
                            .<Maybe<Tuple2<Context<A, N, E, I>, G>>>match(constantly(nothing()),
                                    constantly(g.decompose()),
                                    n -> g.atNode(n).projectB())
                            .match(constantly(terminate(acc)),
                                    c -> destinationCheck.apply(c._1())
                                            ? terminate(accumulator.apply(state, acc, c._1()))
                                            : recurse(tuple(c._2(), updateState.apply(state, acc, c._1()), accumulator.apply(state, acc, c._1()))));
                }),
                tuple((G) this, defState, defAcc));
    }

    default <S, Acc> Acc guidedFold(Fn1<S, FoldContinue<N>> contextGetter,
                                    Fn3<S, Acc, Context<A, N, E, I>, S> updateState,
                                    S defState, Fn3<S, Acc, Context<A, N, E, I>, Acc> accumulator,
                                    Acc defAcc) {
        return foldG(constantly(false), contextGetter, updateState, defState, accumulator, defAcc);
    }

    default <Acc> Acc simpleCutFold(Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                                    Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                                    Acc defAcc) {
        return foldG(destinationCheck, constantly(FoldContinue.decompose()), (_s, _acc, _c) -> UNIT, UNIT, (__, acc, c) -> accumulator.apply(acc, c), defAcc);
    }

    default <Acc> Acc simpleFold(Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                                 Acc defAcc) {
        return foldG(constantly(false),
                constantly(FoldContinue.decompose()),
                (_s, _acc, _c) -> UNIT,
                UNIT,
                (__, acc, c) -> accumulator.apply(acc, c),
                defAcc);
    }

}
