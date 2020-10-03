package io.ataraxic.nomicflux.voyageur;

/**
 * An {@link Edge} with additional weight metadata
 *
 * @param <A>  The type of the {@link Node Node's} ID
 * @param <N>  The concrete type of the node implementation
 * @param <W>  Weight metadata added to the edge
 * @param <E>  Unification type to return a concrete Edge implementation
 */
public interface WeightedEdge<A, N extends Node<A>, W, E extends WeightedEdge<A, N, W, E>> extends Edge<A, N, E> {
    /**
     * @return  The edge's weight metadata
     */
    W getWeight();
}
