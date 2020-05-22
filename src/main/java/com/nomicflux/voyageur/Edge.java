package com.nomicflux.voyageur;

public interface Edge<A> {
    Node<A> getNodeFrom();

    Node<A> getNodeTo();
}
