package com.nomicflux.voyageur;

public interface WeightedEdge<A, W> extends Edge<A> {
    W getWeight();
}
