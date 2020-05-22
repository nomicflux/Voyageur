package com.nomicflux.voyageur.impl;

import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Node;

public final class ValueContext<A, N extends Node<A>, E extends Edge<A>> implements Context<A, N, E> {
    private final N node;
    private final Iterable<E> outboundEdges;
    private final Iterable<E> inboundEdges;

    private ValueContext(N node, Iterable<E> outboundEdges, Iterable<E> inboundEdges) {
        this.node = node;
        this.outboundEdges = outboundEdges;
        this.inboundEdges = inboundEdges;
    }

    public static <A, N extends Node<A>, E extends Edge<A>> Context<A, N, E> valueContext(N node, Iterable<E> outboundEdges, Iterable<E> inboundEdges) {
        return new ValueContext<A, N, E>(node, outboundEdges, inboundEdges);
    }

    @Override
    public N getNode() {
        return node;
    }

    @Override
    public Iterable<E> getOutboundEdges() {
        return outboundEdges;
    }

    @Override
    public Iterable<E> getInboundEdges() {
        return inboundEdges;
    }
}
