package com.nomicflux.voyageur.impl;

import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.Edge;
import com.nomicflux.voyageur.Node;

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
}
