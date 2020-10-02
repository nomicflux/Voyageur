package com.nomicflux.voyageur.search;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.functions.Fn3;
import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Graph;
import com.nomicflux.voyageur.Node;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functor.builtin.State.state;
import static com.nomicflux.voyageur.fold.FoldContinue.nodeOrTerminate;

public abstract class Search<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>, G extends Graph<A, N, E, I, G>, S> implements Fn3<G, N, A, Boolean> {
    @Override
    public Boolean checkedApply(G startGraph, N startNode, A a) {
        return startGraph.<S, Boolean>guidedCutFold(c -> c.getNode().getValue().equals(a),
                state(s -> tuple(nodeOrTerminate(nextNode(s)), stateTail(s))),
                (acc, c) -> state(s -> tuple(acc || c.getNode().getValue().equals(a), stateAddNode(s, c))),
                startingState(startNode),
                false);
    }

    abstract Maybe<N> nextNode(S s);

    abstract S stateTail(S s);

    abstract S stateAddNode(S s, Context<A, N, E, I> mc);

    abstract S startingState(N startNode);
}
