package io.ataraxic.nomicflux.voyageur.impl;

import com.jnape.palatable.shoki.impl.HashSet;
import io.ataraxic.nomicflux.voyageur.Context;
import io.ataraxic.nomicflux.voyageur.Edge;
import io.ataraxic.nomicflux.voyageur.Node;

import java.util.Objects;

/**
 * A {@link Context} which is a value object, isomorphic to a tuple containing its node, a set of edges into the
 * node, and a set of edges coming out of the node
 *
 * @param <A>  The type of the node's ID value
 * @param <L>  The type of the node's label metadata
 * @param <W>  The type of the weight metadata on the edges
 */
public final class ValueContext<A, L, W> implements Context<A, ValueNode<A, L>, ValueEdge<A, L, W>, HashSet<ValueEdge<A, L, W>>> {
    private final ValueNode<A, L> node;
    private final HashSet<ValueEdge<A, L, W>> outboundEdges;
    private final HashSet<ValueEdge<A, L, W>> inboundEdges;

    private ValueContext(ValueNode<A, L> node, HashSet<ValueEdge<A, L, W>> outboundEdges, HashSet<ValueEdge<A, L, W>> inboundEdges) {
        this.node = node;
        this.outboundEdges = outboundEdges;
        this.inboundEdges = inboundEdges;
    }

    /**
     * Create a new {@link Context}
     *
     * @param node            The {@link ValueNode} the context is centered on
     * @param outboundEdges   The {@link HashSet HashSet} of {@link ValueEdge ValueEdges} leading out of the node
     * @param inboundEdges    The {@link HashSet HashSet} of {@link ValueEdge ValueEdges} coming into the node
     * @param <A>  The type of the node's ID value
     * @param <L>  The type of the node's label metadata
     * @param <W>  The type of the weight metadata on the edges
     * @return     A context which is isomorphic to a {@code Tuple3<ValueNode, HashSet<ValueEdge>, HashSet<ValueEdge>}
     */
    public static <A, L, W> ValueContext<A, L, W> context(ValueNode<A, L> node, HashSet<ValueEdge<A, L, W>> outboundEdges, HashSet<ValueEdge<A, L, W>> inboundEdges) {
        return new ValueContext<A, L, W>(node, outboundEdges, inboundEdges);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValueNode<A, L> getNode() {
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashSet<ValueEdge<A, L, W>> getOutboundEdges() {
        return outboundEdges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashSet<ValueEdge<A, L, W>> getInboundEdges() {
        return inboundEdges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueContext<?, ?, ?> that = (ValueContext<?, ?, ?>) o;
        return Objects.equals(node, that.node) &&
                Objects.equals(outboundEdges, that.outboundEdges) &&
                Objects.equals(inboundEdges, that.inboundEdges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(node, outboundEdges, inboundEdges);
    }

    @Override
    public String toString() {
        return "Context(" + inboundEdges.toString() + " -> " + node.toString() + " <- " + outboundEdges.toString() + ")";
    }
}
