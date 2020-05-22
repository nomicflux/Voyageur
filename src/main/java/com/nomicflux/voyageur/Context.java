package com.nomicflux.voyageur;

public interface Context<A, N extends Node<A>, E extends Edge<A>> {
    N getNode();

    Iterable<E> getOutboundEdges();

    Iterable<E> getInboundEdges();
}
