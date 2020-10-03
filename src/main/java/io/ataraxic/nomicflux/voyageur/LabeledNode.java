package io.ataraxic.nomicflux.voyageur;

/**
 * A {@link Node} which also carries a label containing node metadata
 *
 * @param <A>  The type of the node's ID
 * @param <L>  The type of the node's metadata
 */
public interface LabeledNode<A, L> extends Node<A> {

    /**
     * @return  The node metadata
     */
    L getLabel();
}
