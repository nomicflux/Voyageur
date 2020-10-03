package io.ataraxic.nomicflux.voyageur;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.recursion.RecursiveResult;
import com.jnape.palatable.lambda.functor.builtin.State;
import io.ataraxic.nomicflux.voyageur.fold.FoldContinue;

import static com.jnape.palatable.lambda.adt.Maybe.just;
import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Tupler2.tupler;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.recurse;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.terminate;
import static com.jnape.palatable.lambda.functor.builtin.State.state;

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
    default <S, Acc> Acc guidedCutFold(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                                       State<S, FoldContinue<N>> contextGetter,
                                       Fn2<Acc, Context<A, N, E, I>, State<S, Acc>> accumulator,
                                       S defState,
                                       Acc defAcc) {
        return State.<S, Tuple2<G, Acc>>state(tuple((G) this, defAcc))
                .trampolineM(into((G g, Acc acc) -> contextGetter
                        .<RecursiveResult<Tuple2<G, Acc>, Acc>>flatMap(fc -> fc
                                .<Maybe<Choice2<G, Tuple2<Context<A, N, E, I>, G>>>>match(constantly(nothing()),
                                        constantly(g.decompose().fmap(Choice2::b)),
                                        n -> just(g.atNode(n)))
                                .<State<S, RecursiveResult<Tuple2<G, Acc>, Acc>>>match(constantly(state(terminate(acc))),
                                        gc -> gc.match(g_ -> state(recurse(tuple(g_, acc))),
                                                c -> accumulator.apply(acc, c._1()).fmap(ac -> destinationCheck.apply(c._1())
                                                        ? terminate(ac)
                                                        : recurse(tuple(c._2(), ac)))
                                        )))))
                .eval(defState);
    }

    default <S, Acc> Acc guidedFold(State<S, FoldContinue<N>> contextGetter,
                                    Fn2<Acc, Context<A, N, E, I>, State<S, Acc>> accumulator,
                                    S defState,
                                    Acc defAcc) {
        return guidedCutFold(constantly(false), contextGetter, accumulator, defState, defAcc);
    }

    // Since there is no guidance in how to deconstruct the graph, this method can be non-deterministic
    default <Acc> Acc simpleCutFold(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                                    Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                                    Acc defAcc) {
        return guidedCutFold(destinationCheck,
                state(FoldContinue.decompose()),
                (acc, c) -> state(accumulator.apply(acc, c)),
                UNIT,
                defAcc);
    }

    default <Acc> Acc simpleFold(Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                                 Acc defAcc) {
        return guidedCutFold(constantly(false),
                state(FoldContinue.decompose()),
                (acc, c) -> state(accumulator.apply(acc, c)),
                UNIT,
                defAcc);
    }

}
