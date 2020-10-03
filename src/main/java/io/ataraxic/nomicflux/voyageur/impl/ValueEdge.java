package io.ataraxic.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import io.ataraxic.nomicflux.voyageur.WeightedEdge;

import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;

public final class ValueEdge<A, L, W> implements WeightedEdge<A, ValueNode<A, L>, W, ValueEdge<A, L, W>> {
    private final ValueNode<A, L> nodeFrom;
    private final ValueNode<A, L> nodeTo;
    private final W weight;

    private ValueEdge(ValueNode<A, L> nodeFrom, ValueNode<A, L> nodeTo, W weight) {
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.weight = weight;
    }

    public static <A, L> ValueEdge<A, L, Unit> edgeFromTo(ValueNode<A, L> from, ValueNode<A, L> to) {
        return new ValueEdge<>(from, to, UNIT);
    }

    public static <A, L> Fn2<ValueNode<A, L>, ValueNode<A, L>, ValueEdge<A, L, Unit>> edgeFromTo() {
        return ValueEdge::edgeFromTo;
    }

    public static <A, L> ValueEdge<A, L, Unit> edgeToFrom(ValueNode<A, L> to, ValueNode<A, L> from) {
        return new ValueEdge<>(from, to, UNIT);
    }

    public static <A, L> Fn2<ValueNode<A, L>, ValueNode<A, L>, ValueEdge<A, L, Unit>> edgeToFrom() {
        return ValueEdge::edgeToFrom;
    }

    public static <A, L> Fn1<ValueNode<A, L>, ValueEdge<A, L, Unit>> edgeTo(ValueNode<A, L> to) {
        return from -> new ValueEdge<>(from, to, UNIT);
    }

    public static <A, L> Fn1<ValueNode<A, L>, ValueEdge<A, L, Unit>> edgeFrom(ValueNode<A, L> from) {
        return to -> new ValueEdge<>(from, to, UNIT);
    }

    public static <A, L, W> ValueEdge<A, L, W> weightedEdgeFromTo(ValueNode<A, L> nodeFrom, ValueNode<A, L> nodeTo, W weight) {
        return new ValueEdge<>(nodeFrom, nodeTo, weight);
    }

    public static <A, L, W> Fn1<W, ValueEdge<A, L, W>> weightedEdgeFromTo(ValueNode<A, L> nodeFrom, ValueNode<A, L> nodeTo) {
        return weight -> new ValueEdge<>(nodeFrom, nodeTo, weight);
    }

    public static <A, L, W> Fn3<ValueNode<A, L>, ValueNode<A, L>, W, ValueEdge<A, L, W>> weightedEdgeFromTo() {
        return ValueEdge::weightedEdgeFromTo;
    }

    public static <A, L, W> ValueEdge<A, L, W> weightedEdgeToFrom(ValueNode<A, L> nodeTo, ValueNode<A, L> nodeFrom, W weight) {
        return new ValueEdge<>(nodeFrom, nodeTo, weight);
    }

    public static <A, L, W> Fn1<W, ValueEdge<A, L, W>> weightedEdgeToFrom(ValueNode<A, L> nodeTo, ValueNode<A, L> nodeFrom) {
        return weight -> new ValueEdge<>(nodeFrom, nodeTo, weight);
    }

    public static <A, L, W> Fn3<ValueNode<A, L>, ValueNode<A, L>, W, ValueEdge<A, L, W>> weightedEdgeToFrom() {
        return ValueEdge::weightedEdgeToFrom;
    }

    public static <A, L, W> Fn2<ValueNode<A, L>, W, ValueEdge<A, L, W>> weightedEdgeTo(ValueNode<A, L> to) {
        return (from, weight) -> weightedEdgeToFrom(to, from, weight);
    }

    public static <A, L, W> Fn2<ValueNode<A, L>, W, ValueEdge<A, L, W>> weightedEdgeFrom(ValueNode<A, L> from) {
        return (to, weight) -> weightedEdgeToFrom(to, from, weight);
    }

    @Override
    public W getWeight() {
        return weight;
    }

    @Override
    public ValueNode<A, L> getNodeFrom() {
        return nodeFrom;
    }

    @Override
    public ValueNode<A, L> getNodeTo() {
        return nodeTo;
    }

    @Override
    public ValueEdge<A, L, W> swap() {
        return weightedEdgeFromTo(nodeTo, nodeFrom, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueEdge<?, ?, ?> that = (ValueEdge<?, ?, ?>) o;
        return Objects.equals(nodeFrom, that.nodeFrom) &&
                Objects.equals(nodeTo, that.nodeTo) &&
                Objects.equals(weight, that.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeFrom, nodeTo, weight);
    }

    @Override
    public String toString() {
        return "Edge[" + nodeFrom.toString() + " -> " + nodeTo.toString() + ": " + weight.toString() + "]";
    }
}
