package io.ataraxic.nomicflux.voyageur;

/**
 * An edge from one {@link Node}  to another
 * 
 * @param <A>  The type of the node's ID
 * @param <N>  The concrete type of the Node interface
 * @param <E>  Unification type to return a concrete edge in certain methods
 */
public interface Edge<A, N extends Node<A>, E extends Edge<A, N, E>> {
    /**
     * @return  The node the edge leaves from
     */
    N getNodeFrom();

    /**
     * @return  The node the edge goes to
     */
    N getNodeTo();

    /**
     * @return  An edge going in the reverse direction
     */
    E swap();
}
