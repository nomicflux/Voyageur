package com.nomicflux.voyageur;

public interface Context<A, W> {
    Node<A> getNode();
    Iterable<Edge<A, W>> getOutboundEdges();
    Iterable<Edge<A, W>> getInboundEdges();
}
