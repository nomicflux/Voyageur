package com.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.nomicflux.voyageur.Node;
import com.nomicflux.voyageur.WeightedEdge;

import java.util.Objects;

public final class ValueWeightedEdge<A, N extends Node<A>, W> implements WeightedEdge<A, N, W, ValueWeightedEdge<A, N, W>> {
    private final N nodeFrom;
    private final N nodeTo;
    private final W weight;

    private ValueWeightedEdge(N nodeFrom, N nodeTo, W weight) {
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.weight = weight;
    }

    public static <A, N extends Node<A>, W> ValueWeightedEdge<A, N, W> weightedEdgeFromTo(N nodeFrom, N nodeTo, W weight) {
        return new ValueWeightedEdge<>(nodeFrom, nodeTo, weight);
    }

    public static <A, N extends Node<A>, W> Fn1<W, ValueWeightedEdge<A, N, W>> weightedEdgeFromTo(N nodeFrom, N nodeTo) {
        return weight -> new ValueWeightedEdge<>(nodeFrom, nodeTo, weight);
    }

    public static <A, N extends Node<A>, W> Fn3<N, N, W, ValueWeightedEdge<A, N, W>> weightedEdgeFromTo() {
        return ValueWeightedEdge::weightedEdgeFromTo;
    }

    public static <A, N extends Node<A>, W> ValueWeightedEdge<A, N, W> weightedEdgeToFrom(N nodeTo, N nodeFrom, W weight) {
        return new ValueWeightedEdge<>(nodeFrom, nodeTo, weight);
    }

    public static <A, N extends Node<A>, W> Fn1<W, ValueWeightedEdge<A, N, W>> weightedEdgeToFrom(N nodeTo, N nodeFrom) {
        return weight -> new ValueWeightedEdge<>(nodeFrom, nodeTo, weight);
    }

    public static <A, N extends Node<A>, W> Fn3<N, N, W, ValueWeightedEdge<A, N, W>> weightedEdgeToFrom() {
        return ValueWeightedEdge::weightedEdgeToFrom;
    }

    public static <A, N extends Node<A>, W> Fn2<N, W, ValueWeightedEdge<A, N, W>> weightedEdgeTo(N to) {
        return (from, weight) -> weightedEdgeToFrom(to, from, weight);
    }

    public static <A, N extends Node<A>, W> Fn2<N, W, ValueWeightedEdge<A, N, W>> weightedEdgeFrom(N from) {
        return (to, weight) -> weightedEdgeToFrom(to, from, weight);
    }

    @Override
    public W getWeight() {
        return weight;
    }

    @Override
    public N getNodeFrom() {
        return nodeFrom;
    }

    @Override
    public N getNodeTo() {
        return nodeTo;
    }

    @Override
    public ValueWeightedEdge<A, N, W> swap() {
        return weightedEdgeFromTo(nodeTo, nodeFrom, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueWeightedEdge<?, ?, ?> that = (ValueWeightedEdge<?, ?, ?>) o;
        return Objects.equals(nodeFrom, that.nodeFrom) &&
                Objects.equals(nodeTo, that.nodeTo) &&
                Objects.equals(weight, that.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeFrom, nodeTo, weight);
    }
}
