package io.ataraxic.nomicflux.voyageur;

public interface Context<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>> {
    N getNode();

    I getOutboundEdges();

    I getInboundEdges();
}
