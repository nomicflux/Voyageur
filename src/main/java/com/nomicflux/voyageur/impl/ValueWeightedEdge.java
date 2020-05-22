package com.nomicflux.voyageur.impl;

import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.WeightedEdge;

public final class ValueWeightedEdge<A, W> implements WeightedEdge<A, W> {
    private final Node<A> nodeFrom;
    private final Node<A> nodeTo;
    private final W weight;

    private ValueWeightedEdge(Node<A> nodeFrom, Node<A> nodeTo, W weight) {
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.weight = weight;
    }

    public static <A, W> WeightedEdge<A, W> valueWeightedEdge(Node<A> nodeFrom, Node<A> nodeTo, W weight) {
        return new ValueWeightedEdge<>(nodeFrom, nodeTo, weight);
    }

    @Override
    public W getWeight() {
        return weight;
    }

    @Override
    public Node<A> getNodeFrom() {
        return nodeFrom;
    }

    @Override
    public Node<A> getNodeTo() {
        return nodeTo;
    }
}
