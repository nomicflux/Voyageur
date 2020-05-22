package com.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.functions.Fn1;
import com.nomicflux.voyageur.Node;

public final class ValueNode<A extends Comparable<A>> implements Node<A>, Comparable<Node<A>> {
    private final A value;

    private ValueNode(A value) {
        this.value = value;
    }

    public static <A extends Comparable<A>> Node<A> valueNode(A a) {
        return new ValueNode<A>(a);
    }

    public static <A extends Comparable<A>> Fn1<A, Node<A>> valueNode() {
        return ValueNode::valueNode;
    }

    public A getValue() {
        return value;
    }

    @Override
    public int compareTo(Node<A> o) {
        return getValue().compareTo(o.getValue());
    }
}
