package com.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.functions.Fn1;
import com.nomicflux.voyageur.Node;

import java.util.Objects;

public final class ValueNode<A> implements Node<A> {
    private final A value;

    private ValueNode(A value) {
        this.value = value;
    }

    public static <A> Node<A> node(A a) {
        return new ValueNode<A>(a);
    }

    public static <A> Fn1<A, Node<A>> node() {
        return ValueNode::node;
    }

    public A getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueNode<?> valueNode = (ValueNode<?>) o;
        return Objects.equals(value, valueNode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Node<" + value.toString() + ">";
    }
}
