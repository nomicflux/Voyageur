package com.nomicflux.voyageur;

public interface Edge<A, N extends Node<A>> {
    N getNodeFrom();

    N getNodeTo();
}
