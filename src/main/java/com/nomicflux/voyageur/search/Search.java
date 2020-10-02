package com.nomicflux.voyageur.search;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn3;
import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.fold.FoldContinue;

import static com.jnape.palatable.lambda.functions.Fn1.fn1;

public abstract class Search<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S> implements Fn3<G, N, A, Boolean> {
    @Override
    public Boolean checkedApply(G startGraph, N startNode, A a) {
        return startGraph.foldG(c -> c.getNode().getValue().equals(a),
                fn1(this::nextNode).fmap(FoldContinue::maybeTerminates),
                (s, acc, c) -> nextState(s, c),
                startingState(startNode),
                (__, acc, c) -> acc || c.getNode().getValue().equals(a),
                false);
    }

    abstract Maybe<N> nextNode(S s);

    abstract S nextState(S s, Context<A, N, E, I> c);

    abstract S startingState(N startNode);
}
