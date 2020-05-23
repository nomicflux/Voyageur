package com.nomicflux.voyageur;

public interface Edge<A, N extends Node<A>, E extends Edge<A, N, E>> {
    N getNodeFrom();

    N getNodeTo();

    E swap();
}
