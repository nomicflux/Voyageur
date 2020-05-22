package com.nomicflux.voyageur;

public interface WeightedEdge<A, N extends Node<A>, W> extends Edge<A, N> {
    W getWeight();
}
