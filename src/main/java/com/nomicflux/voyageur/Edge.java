package com.nomicflux.voyageur;

public interface Edge<A, W> {
    A getNodeTo();
    A getNodeFrom();
    W getWeight();
}
