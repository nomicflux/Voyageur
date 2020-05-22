package com.nomicflux.voyageur;

public interface LabeledNode<A, L> extends Node<A> {
    L getLabel();
}
