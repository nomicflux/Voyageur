package io.ataraxic.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import io.ataraxic.nomicflux.voyageur.WeightedEdge;

import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;

/**
 * A {@link WeightedEdge} which is a value object, isomorphic to the two {@link ValueNode ValueNodes} it connects and its weight.
 *
 * @param <A>  The type of the nodes' IDs
 * @param <L>  The type of the nodes' label metadata
 * @param <W>  The type of the weight metadata on the edge
 */
public final class ValueEdge<A, L, W> implements WeightedEdge<A, ValueNode<A, L>, W, ValueEdge<A, L, W>> {
    private final ValueNode<A, L> nodeFrom;
    private final ValueNode<A, L> nodeTo;
    private final W weight;

    private ValueEdge(ValueNode<A, L> nodeFrom, ValueNode<A, L> nodeTo, W weight) {
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.weight = weight;
    }

    /**
     * Create an edge from one node to another, with no weight information
     *
     * @param from  {@link ValueNode} the edge starts from
     * @param to    {@link ValueNode} the edge ends at
     * @param <A>   The type of the nodes' IDs
     * @param <L>   The type of the nodes' label metadata
     * @return      An edge from {@code from} going to {@code to}
     */
    public static <A, L> ValueEdge<A, L, Unit> edgeFromTo(ValueNode<A, L> from, ValueNode<A, L> to) {
        return new ValueEdge<>(from, to, UNIT);
    }

    public static <A, L> Fn2<ValueNode<A, L>, ValueNode<A, L>, ValueEdge<A, L, Unit>> edgeFromTo() {
        return ValueEdge::edgeFromTo;
    }

    /**
     * Create an edge to one node from another, with no weight information
     *
     * @param to    {@link ValueNode} the edge ends at
     * @param from  {@link ValueNode} the edge starts from
     * @param <A>   The type of the nodes' IDs
     * @param <L>   The type of the nodes' label metadata
     * @return      An edge to {@code to} coming from {@code from}
     */
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

    /**
     * Create an edge from one node to another with weight metadata
     *
     * @param from    {@link ValueNode} the edge starts from
     * @param to      {@link ValueNode} the edge ends at
     * @param weight  Weight metadata for the edge
     * @param <A>     The type of the nodes' IDs
     * @param <L>     The type of the nodes' label metadata
     * @param <W>     The type of the edge's weight metadata
     * @return        An edge from {@code from} going to {@code to} and with weight {@code weight}
     */
    public static <A, L, W> ValueEdge<A, L, W> weightedEdgeFromTo(ValueNode<A, L> from, ValueNode<A, L> to, W weight) {
        return new ValueEdge<>(from, to, weight);
    }

    public static <A, L, W> Fn1<W, ValueEdge<A, L, W>> weightedEdgeFromTo(ValueNode<A, L> from, ValueNode<A, L> to) {
        return weight -> new ValueEdge<>(from, to, weight);
    }

    public static <A, L, W> Fn3<ValueNode<A, L>, ValueNode<A, L>, W, ValueEdge<A, L, W>> weightedEdgeFromTo() {
        return ValueEdge::weightedEdgeFromTo;
    }

    /**
     * Create an edge to one node from another with weight metadata
     *
     * @param to      {@link ValueNode} the edge ends at
     * @param from    {@link ValueNode} the edge starts from
     * @param weight  Weight metadata for the edge
     * @param <A>     The type of the nodes' IDs
     * @param <L>     The type of the nodes' label metadata
     * @param <W>     The type of the edge's weight metadata
     * @return        An edge to {@code to} coming from {@code from} and with weight {@code weight}
     */
    public static <A, L, W> ValueEdge<A, L, W> weightedEdgeToFrom(ValueNode<A, L> to, ValueNode<A, L> from, W weight) {
        return new ValueEdge<>(from, to, weight);
    }

    public static <A, L, W> Fn1<W, ValueEdge<A, L, W>> weightedEdgeToFrom(ValueNode<A, L> to, ValueNode<A, L> from) {
        return weight -> new ValueEdge<>(from, to, weight);
    }

    public static <A, L, W> Fn3<ValueNode<A, L>, ValueNode<A, L>, W, ValueEdge<A, L, W>> weightedEdgeToFrom() {
        return ValueEdge::weightedEdgeToFrom;
    }

    /**
     * Curried function creating an edge to a given node
     *
     * @param to      {@link ValueNode} the edge ends at
     * @param <A>     The type of the nodes' IDs
     * @param <L>     The type of the nodes' label metadata
     * @param <W>     The type of the edge's weight metadata
     * @return        A function taking a from node and a weight to produce an edge
     */
    public static <A, L, W> Fn2<ValueNode<A, L>, W, ValueEdge<A, L, W>> weightedEdgeTo(ValueNode<A, L> to) {
        return (from, weight) -> weightedEdgeToFrom(to, from, weight);
    }

    /**
     * Curried function creating an edge from a given node
     *
     * @param from    {@link ValueNode} the edge comes from
     * @param <A>     The type of the nodes' IDs
     * @param <L>     The type of the nodes' label metadata
     * @param <W>     The type of the edge's weight metadata
     * @return        A function taking a to node and a weight to produce an edge
     */
    public static <A, L, W> Fn2<ValueNode<A, L>, W, ValueEdge<A, L, W>> weightedEdgeFrom(ValueNode<A, L> from) {
        return (to, weight) -> weightedEdgeToFrom(to, from, weight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public W getWeight() {
        return weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueNode<A, L> getNodeFrom() {
        return nodeFrom;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueNode<A, L> getNodeTo() {
        return nodeTo;
    }

    /**
     * {@inheritDoc}
     */
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
