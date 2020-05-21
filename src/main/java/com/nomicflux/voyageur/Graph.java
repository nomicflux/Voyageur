package com.nomicflux.voyageur;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;

public interface Graph<A, W> {
    Maybe<Context<A, W>> head();
    Maybe<Context<A, W>> atNode(Node<A> node);
    Maybe<Graph<A, W>> rest();
}
