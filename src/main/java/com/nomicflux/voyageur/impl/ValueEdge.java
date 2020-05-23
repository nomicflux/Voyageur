package com.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Node;

import java.util.Objects;

public final class ValueEdge<A, N extends Node<A>> implements Edge<A, N, ValueEdge<A, N>> {
    private final N nodeFrom;
    private final N nodeTo;

    private ValueEdge(N nodeFrom, N nodeTo) {
        this.nodeTo = nodeTo;
        this.nodeFrom = nodeFrom;
    }

    public static <A, N extends Node<A>> ValueEdge<A, N> edgeFromTo(N from, N to) {
        return new ValueEdge<>(from, to);
    }

    public static <A, N extends Node<A>> Fn2<N, N, ValueEdge<A, N>> edgeFromTo() {
        return ValueEdge::edgeFromTo;
    }

    public static <A, N extends Node<A>> ValueEdge<A, N> edgeToFrom(N to, N from) {
        return new ValueEdge<>(from, to);
    }

    public static <A, N extends Node<A>> Fn2<N, N, ValueEdge<A, N>> edgeToFrom() {
        return ValueEdge::edgeToFrom;
    }


    public static <A, N extends Node<A>> Fn1<N, ValueEdge<A, N>> edgeTo(N to) {
        return from -> new ValueEdge<A, N>(from, to);
    }

    public static <A, N extends Node<A>> Fn1<N, ValueEdge<A, N>> edgeFrom(N from) {
        return to -> new ValueEdge<A, N>(from, to);
    }

    @Override
    public N getNodeTo() {
        return nodeTo;
    }

    @Override
    public ValueEdge<A, N> swap() {
        return edgeFromTo(nodeTo, nodeFrom);
    }

    @Override
    public N getNodeFrom() {
        return nodeFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueEdge<?, ?> valueEdge = (ValueEdge<?, ?>) o;
        return Objects.equals(nodeFrom, valueEdge.nodeFrom) &&
                Objects.equals(nodeTo, valueEdge.nodeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeFrom, nodeTo);
    }
}
