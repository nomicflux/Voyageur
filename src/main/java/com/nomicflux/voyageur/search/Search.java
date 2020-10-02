package com.nomicflux.voyageur.search;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn3;
import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.nomicflux.voyageur.fold.FoldContinue.maybeTerminates;

public abstract class Search<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S> implements Fn3<G, N, A, Boolean> {
    @Override
    public Boolean checkedApply(G startGraph, N startNode, A a) {
        return startGraph.foldG(c -> c.getNode().getValue().equals(a),
                maybeTerminates(this::nextNode),
                (s, acc, mc) -> nextState(s, mc),
                startingState(startNode),
                (__, acc, c) -> acc || c.getNode().getValue().equals(a),
                false);
    }

    abstract Maybe<N> nextNode(S s);

    abstract S nextState(S s, Maybe<Context<A, N, E, I>> mc);

    abstract S startingState(N startNode);
}
