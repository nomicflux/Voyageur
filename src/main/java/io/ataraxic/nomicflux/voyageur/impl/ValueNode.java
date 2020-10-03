package io.ataraxic.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import io.ataraxic.nomicflux.voyageur.LabeledNode;
import io.ataraxic.nomicflux.voyageur.Node;

import java.util.Objects;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;

/**
 * A {@link LabeledNode} which is a value object, isomorphic to its value and label
 *
 * @param <A>  The type of the node's ID value
 * @param <L>  The type of the node's metadata label
 */
public final class ValueNode<A, L> implements LabeledNode<A, L> {
    private final A value;
    private final L label;

    private ValueNode(A value, L label) {
        this.value = value;
        this.label = label;
    }

    /**
     * Create a new node with no label
     *
     * @param a    The node's ID value
     * @param <A>  The type of the node's ID value
     * @return     A node with the value {@code a}
     */
    public static <A> ValueNode<A, Unit> node(A a) {
        return new ValueNode<>(a, UNIT);
    }

    public static <A> Fn1<A, ValueNode<A, Unit>> node() {
        return ValueNode::node;
    }


    /**
     * Create a new node with a provided label
     *
     * @param a      The node's ID value
     * @param label  The node's metadata label
     * @param <A>    The type of the node's ID value
     * @param <L>    The type of the node's metadata label
     * @return       A node with the value {@code a} and label {@code label}
     */
    public static <A, L> ValueNode<A, L> labeledNode(A a, L label) {
        return new ValueNode<>(a, label);
    }

    public static <A, L> Fn1<L, ValueNode<A, L>> labeledNode(A a) {
        return label -> labeledNode(a, label);
    }

    public static <A, L> Fn2<A, L, ValueNode<A, L>> labeledNode() {
        return ValueNode::labeledNode;
    }

    /**
     * Label an existing node. Will use the existing node's value and discard other information in the node.
     *
     * @param node   Node to use for providing an ID value
     * @param label  The node's metadata label
     * @param <A>    The type of the node's ID value
     * @param <L>    The type of the node's metadata label
     * @return       A node with the value {@code a} and label {@code label}
     */
    public static <A, L> ValueNode<A, L> labelNode(Node<A> node, L label) {
        return new ValueNode<>(node.getValue(), label);
    }

    public static <A, L> Fn1<L, ValueNode<A, L>> labelNode(Node<A> node) {
        return label -> labelNode(node, label);
    }

    public static <A, L> Fn2<Node<A>, L, ValueNode<A, L>> labelNode() {
        return ValueNode::labelNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public A getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
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
