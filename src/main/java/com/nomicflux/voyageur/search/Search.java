package com.nomicflux.voyageur.search;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.shoki.impl.StrictQueue;
import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.nomicflux.voyageur.fold.FoldG.foldG;

public abstract class Search<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S> implements Fn3<G, N, A, Boolean> {
    @Override
    public Boolean checkedApply(G startGraph, N startNode, A a) {
        return foldG(c -> c.getNode().getValue().equals(a),
                this::nextNode,
                constantly(false),
                this::nextState,
                (acc, c) -> acc || c.getNode().getValue().equals(a),
                false,
                startingState(startNode),
                startGraph);
    }

    abstract Maybe<N> nextNode(S s);

    abstract S nextState(S s, Context<A, N, E, I> c);

    abstract S startingState(N startNode);
}
