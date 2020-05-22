package com.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.nomicflux.voyageur.LabeledNode;
import com.nomicflux.voyageur.Node;

public final class ValueNodeWithLabel<A extends Comparable<A>, L> implements LabeledNode<A, L>, Comparable<LabeledNode<A, L>> {
    private final A value;
    private final L label;

    private ValueNodeWithLabel(A value, L label) {
        this.value = value;
        this.label = label;
    }

    public static <A extends Comparable<A>, L> LabeledNode<A, L> labeledNode(A a, L label) {
        return new ValueNodeWithLabel<A, L>(a, label);
    }

    public static <A extends Comparable<A>, L> Fn1<L, LabeledNode<A, L>> labeledNode(A a) {
        return label -> labeledNode(a, label);
    }

    public static <A extends Comparable<A>, L> Fn2<A, L, LabeledNode<A, L>> labeledNode() {
        return ValueNodeWithLabel::labeledNode;
    }

    public static <A extends Comparable<A>, L> LabeledNode<A, L> labelNode(Node<A> node, L label) {
        return new ValueNodeWithLabel<A, L>(node.getValue(), label);
    }

    public static <A extends Comparable<A>, L> Fn1<L, LabeledNode<A, L>> labelNode(Node<A> node) {
        return label -> labelNode(node, label);
    }

    public static <A extends Comparable<A>, L> Fn2<Node<A>, L, LabeledNode<A, L>> labelNode() {
        return ValueNodeWithLabel::labelNode;
    }

    public A getValue() {
        return value;
    }

    @Override
    public L getLabel() {
        return label;
    }

    @Override
    public int compareTo(LabeledNode<A, L> o) {
        return 0;
    }
}
