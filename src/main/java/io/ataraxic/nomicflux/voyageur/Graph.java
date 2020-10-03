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
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.recurse;
import static com.jnape.palatable.lambda.functions.recursion.RecursiveResult.terminate;
import static com.jnape.palatable.lambda.functor.builtin.State.state;

/**
 * Main interface for working with inductive graphs
 *
 * @param <A>  The type of a {@link Node Node's} ID
 * @param <N>  The concrete type of a {@code Node}
 * @param <E>  The concrete type of an {@link Edge}
 * @param <I>  The concrete type of an {@link Iterable Iterable} of edges
 * @param <G>  Unification type for the concrete {@code Graph} implementation
 */
public interface Graph<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>> {
    /**
     * @return  Whether the current graph is empty
     */
    Boolean isEmpty();

    /**
     * @param edge  {@link Edge} to add to graph
     * @return      Graph {@code G} with {@code edge} added
     */
    G addEdge(E edge);

    default Fn1<E, G> addEdge() {
        return this::addEdge;
    }

    /**
     * @param edge  {@link Edge} to remove from graph
     * @return      Graph {@code G} with {@code edge} removed
     */
    G removeEdge(E edge);

    default Fn1<E, G> removeEdge() {
        return this::removeEdge;
    }

    /**
     * @param node  {@link Node} to add to graph
     * @return      Graph {@code G} with disconnected {@code node} added
     */
    G addNode(N node);

    default Fn1<N, G> addNode() {
        return this::addNode;
    }

    /**
     * @param node  {@link Node} to remove from graph
     * @return      Graph {@code G} with {@code node} and all connected edges removed
     */
    G removeNode(N node);

    default Fn1<N, G> removeNode() {
        return this::removeNode;
    }

    /**
     * Implementation-dependent and possibly non-deterministic decomposition of a graph into a {@link Context}
     * and the rest of the graph
     *
     * @return  Either the {@code Context} and the graph with the context's {@link Node} and {@link Edge Edges} removed,
     *          or {@link Maybe#nothing nothing} to indicate the graph is empty
     */
    Maybe<Tuple2<Context<A, N, E, I>, G>> decompose();

    /**
     * Decompose a graph at a given {@link Node} into the node's {@link Context} and the rest of the graph
     *
     * @param node  {@code Node} to find in the graph, around which to base a decomposition
     * @return      Either the whole graph {@code G} if the node cannot be found, or a decomposition into a context
     *              centered on {@code node} and the rest of the graph
     */
    Choice2<G, Tuple2<Context<A, N, E, I>, G>> atNode(N node);

    default Fn1<N, Choice2<G, Tuple2<Context<A, N, E, I>, G>>> atNode() {
        return this::atNode;
    }

    /**
     * Fold a graph into an accumulation, using stateful methods to guide the traversal of the graph, and a cutoff
     * function to end the fold early
     *
     * @param destinationCheck  End the fold if a {@link Context} matches this predicate
     * @param contextGetter     Retrieve the next {@link Node} from a given {@link State State} {@code S}. If the state
     *                          can't produce a new node, a {@link FoldContinue} determines whether the fold should
     *                          decompose arbitrarily or end.
     * @param accumulator       Stateful function to update the accumulator based on the new {@link Context} and the {@link State State}
     * @param defState          Starting {@link State State} {@code S}
     * @param defAcc            Starting accumulator {@code Acc}
     * @param <S>               Type of the {@link State State} updated in the fold
     * @param <Acc>             Type of the accumulator returned from the fold
     * @return                  The accumulator at the end of the fold
     */
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

    /**
     * Fold a graph into an accumulation, using stateful methods to guide the traversal of the graph. Fold traverses entire graph.
     *
     * @param contextGetter     Retrieve the next {@link Node} from a given {@link State State} {@code S}. If the state
     *                          can't produce a new node, a {@link FoldContinue} determines whether the fold should
     *                          decompose arbitrarily or end.
     * @param accumulator       Stateful function to update the accumulator based on the new {@link Context} and the {@link State State}
     * @param defState          Starting {@link State State} {@code S}
     * @param defAcc            Starting accumulator {@code Acc}
     * @param <S>               Type of the {@link State State} updated in the fold
     * @param <Acc>             Type of the accumulator returned from the fold
     * @return                  The accumulator at the end of the fold
     */
    default <S, Acc> Acc guidedFold(State<S, FoldContinue<N>> contextGetter,
                                    Fn2<Acc, Context<A, N, E, I>, State<S, Acc>> accumulator,
                                    S defState,
                                    Acc defAcc) {
        return guidedCutFold(constantly(false), contextGetter, accumulator, defState, defAcc);
    }

    /**
     * Fold a graph into an accumulation, using the implementation's {@link Graph#decompose} method, and a cutoff
     * function to end the fold early
     *
     * @param destinationCheck  End the fold if a {@link Context} matches this predicate
     * @param accumulator       Function to update the accumulator based on the new {@link Context}
     * @param defAcc            Starting accumulator {@code Acc}
     * @param <Acc>             Type of the accumulator returned from the fold
     * @return                  The accumulator at the end of the fold
     */
    default <Acc> Acc simpleCutFold(Fn1<Context<A, N, E, I>, Boolean> destinationCheck,
                                    Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                                    Acc defAcc) {
        return guidedCutFold(destinationCheck,
                state(FoldContinue.decompose()),
                (acc, c) -> state(accumulator.apply(acc, c)),
                UNIT,
                defAcc);
    }

    /**
     * Fold a graph into an accumulation, using the implementation's {@link Graph#decompose} method. Traverses the entire graph.
     *
     * @param accumulator       Function to update the accumulator based on the new {@link Context}
     * @param defAcc            Starting accumulator {@code Acc}
     * @param <Acc>             Type of the accumulator returned from the fold
     * @return                  The accumulator at the end of the fold
     */
    default <Acc> Acc simpleFold(Fn2<Acc, Context<A, N, E, I>, Acc> accumulator,
                                 Acc defAcc) {
        return guidedCutFold(constantly(false),
                state(FoldContinue.decompose()),
                (acc, c) -> state(accumulator.apply(acc, c)),
                UNIT,
                defAcc);
    }

}
