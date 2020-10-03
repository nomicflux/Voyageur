package com.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.nomicflux.voyageur.LabeledNode;
import com.nomicflux.voyageur.Node;

import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;

public final class ValueNode<A, L> implements LabeledNode<A, L> {
    private final A value;
    private final L label;

    private ValueNode(A value, L label) {
        this.value = value;
        this.label = label;
    }

    public static <A> ValueNode<A, Unit> node(A a) {
        return new ValueNode<A, Unit>(a, UNIT);
    }

    public static <A> Fn1<A, ValueNode<A, Unit>> node() {
        return ValueNode::node;
    }


    public static <A, L> ValueNode<A, L> labeledNode(A a, L label) {
        return new ValueNode<>(a, label);
    }

    public static <A, L> Fn1<L, ValueNode<A, L>> labeledNode(A a) {
        return label -> labeledNode(a, label);
    }

    public static <A, L> Fn2<A, L, ValueNode<A, L>> labeledNode() {
        return ValueNode::labeledNode;
    }

    public static <A, L> ValueNode<A, L> labelNode(Node<A> node, L label) {
        return new ValueNode<A, L>(node.getValue(), label);
    }

    public static <A, L> Fn1<L, ValueNode<A, L>> labelNode(Node<A> node) {
        return label -> labelNode(node, label);
    }

    public static <A, L> Fn2<Node<A>, L, ValueNode<A, L>> labelNode() {
        return ValueNode::labelNode;
    }

    public A getValue() {
        return value;
    }

    @Override
    public L getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueNode<?, ?> that = (ValueNode<?, ?>) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, label);
    }

    @Override
    public String toString() {
        return "Node{" + label.toString() + "}<" + value.toString() + ">";
    }
}
