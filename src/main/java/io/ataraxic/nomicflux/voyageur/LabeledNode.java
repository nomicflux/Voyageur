package io.ataraxic.nomicflux.voyageur;

public interface LabeledNode<A, L> extends Node<A> {
    L getLabel();
}
