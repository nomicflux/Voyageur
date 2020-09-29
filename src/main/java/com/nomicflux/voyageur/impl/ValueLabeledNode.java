package com.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.nomicflux.voyageur.LabeledNode;
import com.nomicflux.voyageur.Node;

import java.util.Objects;

public final class ValueLabeledNode<A, L> implements LabeledNode<A, L> {
    private final A value;
    private final L label;

    private ValueLabeledNode(A value, L label) {
        this.value = value;
        this.label = label;
    }

    public static <A, L> LabeledNode<A, L> labeledNode(A a, L label) {
        return new ValueLabeledNode<A, L>(a, label);
    }

    public static <A, L> Fn1<L, LabeledNode<A, L>> labeledNode(A a) {
        return label -> labeledNode(a, label);
    }

    public static <A, L> Fn2<A, L, LabeledNode<A, L>> labeledNode() {
        return ValueLabeledNode::labeledNode;
    }

    public static <A, L> LabeledNode<A, L> labelNode(Node<A> node, L label) {
        return new ValueLabeledNode<A, L>(node.getValue(), label);
    }

    public static <A, L> Fn1<L, LabeledNode<A, L>> labelNode(Node<A> node) {
        return label -> labelNode(node, label);
    }

    public static <A, L> Fn2<Node<A>, L, LabeledNode<A, L>> labelNode() {
        return ValueLabeledNode::labelNode;
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
        ValueLabeledNode<?, ?> that = (ValueLabeledNode<?, ?>) o;
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
