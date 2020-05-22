package com.nomicflux.voyageur;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;

public interface Graph<A, N extends Node<A>, E extends Edge<A>, G extends Graph<A, N, E, G>> {
    Maybe<Tuple2<Context<A, N, E>, G>> decompose();

    Maybe<Choice2<G, Tuple2<Context<A, N, E>, G>>> atNode(N node);
}
