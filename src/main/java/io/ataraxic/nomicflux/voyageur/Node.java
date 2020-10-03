package io.ataraxic.nomicflux.voyageur;

/**
 * A {@code Node} containing a value, which serves as its (presumed unique) ID
 *
 * @param <A>  The type of the node's ID
 */
public interface Node<A> {
    /**
     * @return  The node ID
     */
    A getValue();
}
