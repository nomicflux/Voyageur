package com.nomicflux.voyageur;

public interface WeightedEdge<A, N extends Node<A>, W, E extends WeightedEdge<A, N, W, E>> extends Edge<A, N, E> {
    W getWeight();
}
