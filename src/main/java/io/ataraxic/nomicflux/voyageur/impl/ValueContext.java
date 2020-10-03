package io.ataraxic.nomicflux.voyageur.impl;

import io.ataraxic.nomicflux.voyageur.Context;
import io.ataraxic.nomicflux.voyageur.Edge;
import io.ataraxic.nomicflux.voyageur.Node;

import java.util.Objects;

public final class ValueContext<A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>> implements Context<A, N, E, I> {
    private final N node;
    private final I outboundEdges;
    private final I inboundEdges;

    private ValueContext(N node, I outboundEdges, I inboundEdges) {
        this.node = node;
        this.outboundEdges = outboundEdges;
        this.inboundEdges = inboundEdges;
    }

    public static <A, N extends Node<A>, E extends Edge<A, N, E>, I extends Iterable<E>> Context<A, N, E, I> context(N node, I outboundEdges, I inboundEdges) {
        return new ValueContext<A, N, E, I>(node, outboundEdges, inboundEdges);
    }

    @Override
    public N getNode() {
        return node;
    }

    @Override
    public I getOutboundEdges() {
        return outboundEdges;
    }

    @Override
    public I getInboundEdges() {
        return inboundEdges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueContext<?, ?, ?, ?> that = (ValueContext<?, ?, ?, ?>) o;
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
