package com.nomicflux.voyageur;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn1;

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
}
