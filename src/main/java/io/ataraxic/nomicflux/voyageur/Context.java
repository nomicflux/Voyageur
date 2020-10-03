package io.ataraxic.nomicflux.voyageur;

/**
 * A context surrounding a {@link Node} and it's outgoing and ingoing {@link Edge Edges}
 *
 * @param <A>  The type of the node's ID
 * @param <N>  The concrete implementation of the node
 * @param <E>  The concrete implementation of the edge
 * @param <I>  A client-defined iterable type containing the incoming and outgoiging edges
 */
public interface Context<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>> {
    /**
     * @return  The node that the context is centered around
     */
    N getNode();

    /**
     * @return  All edges leaving the context's node
     */
    I getOutboundEdges();

    /**
     * @return  All edges entering the context's node
     */
    I getInboundEdges();
}
