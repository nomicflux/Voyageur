package com.nomicflux.voyageur.impl;

import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Node;

public final class ValueEdge<A> implements Edge<A> {
    private final Node<A> nodeFrom;
    private final Node<A> nodeTo;

    private ValueEdge(Node<A> nodeFrom, Node<A> nodeTo) {
        this.nodeTo = nodeTo;
        this.nodeFrom = nodeFrom;
    }

    public static <A> Edge<A> valueEdge(Node<A> from, Node<A> to) {
        return new ValueEdge<A>(from, to);
    }

    @Override
    public Node<A> getNodeTo() {
        return nodeTo;
    }

    @Override
    public Node<A> getNodeFrom() {
        return nodeFrom;
    }
}
