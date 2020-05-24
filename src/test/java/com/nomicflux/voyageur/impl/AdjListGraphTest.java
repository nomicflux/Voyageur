package com.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.shoki.impl.HashSet;
import com.nomicflux.voyageur.Context;
import com.nomicflux.voyageur.LabeledNode;
import com.nomicflux.voyageur.Node;
import org.junit.Test;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.shoki.impl.HashSet.hashSet;
import static com.nomicflux.voyageur.impl.AdjListGraph.*;
import static com.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static com.nomicflux.voyageur.impl.ValueLabeledNode.labeledNode;
import static com.nomicflux.voyageur.impl.ValueNode.node;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdjListGraphTest {

    @Test
    public void emptyDecomposesIntoEmpty() {
        assertThat(emptyGraph().decompose(), equalTo(nothing()));
    }

    @Test
    public void singletonCanBeDecomposed() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = singletonGraph(node(1));
        Tuple2<Context<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>, HashSet<ValueEdge<Integer, Node<Integer>>>>, AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>> decomposed = graph.decompose().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node(1)));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet()));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet()));
        assertTrue(decomposed._2().isEmpty());
    }

    @Test
    public void labeledNodeRemainsLabeled() {
        LabeledNode<Integer, String> node = labeledNode(1, "hello");
        AdjListGraph<Integer, LabeledNode<Integer, String>, ValueEdge<Integer, LabeledNode<Integer, String>>> graph = singletonGraph(node);
        Tuple2<Context<Integer, LabeledNode<Integer, String>, ValueEdge<Integer, LabeledNode<Integer, String>>, HashSet<ValueEdge<Integer, LabeledNode<Integer, String>>>>, AdjListGraph<Integer, LabeledNode<Integer, String>, ValueEdge<Integer, LabeledNode<Integer, String>>>> decomposed = graph.decompose().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node));
        assertThat(decomposed._1().getNode().getLabel(), equalTo(node.getLabel()));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet()));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet()));
        assertTrue(decomposed._2().isEmpty());
    }

    @Test
    public void singletonCanBeDecomposedAtNode() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = singletonGraph(node(1));
        Tuple2<Context<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>, HashSet<ValueEdge<Integer, Node<Integer>>>>, AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>> decomposed = graph.atNode(node(1)).projectB().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node(1)));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet()));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet()));
        assertTrue(decomposed._2().isEmpty());
    }

    @Test
    public void singletonReturnsGraphForOtherNodes() {
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = singletonGraph(node(1));
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> atNode = graph.atNode(node(2)).projectA().orElseThrow(AssertionError::new);

        assertFalse(atNode.isEmpty());
    }

    @Test
    public void singleEdgeCanBeDecomposed() {
        ValueEdge<Integer, Node<Integer>> edge = edgeFromTo(node(1), node(2));
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdge(edge);
        Tuple2<Context<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>, HashSet<ValueEdge<Integer, Node<Integer>>>>, AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>> decomposed = graph.decompose().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet(edge)));
        assertFalse(decomposed._2().isEmpty());
    }

    @Test
    public void singleEdgeAtFromNode() {
        ValueEdge<Integer, Node<Integer>> edge = edgeFromTo(node(1), node(2));
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdge(edge);
        Tuple2<Context<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>, HashSet<ValueEdge<Integer, Node<Integer>>>>, AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>> decomposed = graph.atNode(node(1)).projectB().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node(1)));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet()));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet(edge)));
        assertFalse(decomposed._2().isEmpty());
    }

    @Test
    public void singleEdgeAtToNode() {
        ValueEdge<Integer, Node<Integer>> edge = edgeFromTo(node(1), node(2));
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdge(edge);
        Tuple2<Context<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>, HashSet<ValueEdge<Integer, Node<Integer>>>>, AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>>> decomposed = graph.atNode(node(2)).projectB().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node(2)));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet(edge)));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet()));
        assertFalse(decomposed._2().isEmpty());
    }

    @Test
    public void removesNodeFromAllRelevantEdges() {
        ValueEdge<Integer, Node<Integer>> edge12 = edgeFromTo(node(1), node(2));
        ValueEdge<Integer, Node<Integer>> edge21 = edgeFromTo(node(2), node(1));
        ValueEdge<Integer, Node<Integer>> edge13 = edgeFromTo(node(1), node(3));
        ValueEdge<Integer, Node<Integer>> edge23 = edgeFromTo(node(2), node(3));
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> graph = fromEdges(asList(edge12, edge21, edge13, edge23));
        AdjListGraph<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>> withoutOne = graph.removeNode(edge12.getNodeFrom());
        assertThat(withoutOne.atNode(edge12.getNodeFrom()).projectB(), equalTo(nothing()));
        Context<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>, HashSet<ValueEdge<Integer, Node<Integer>>>> c2 = withoutOne.atNode(edge23.getNodeFrom()).projectB().orElseThrow(AssertionError::new)._1();
        Context<Integer, Node<Integer>, ValueEdge<Integer, Node<Integer>>, HashSet<ValueEdge<Integer, Node<Integer>>>> c3 = withoutOne.atNode(edge23.getNodeTo()).projectB().orElseThrow(AssertionError::new)._1();
        assertFalse(c2.getInboundEdges().contains(edge12));
        assertFalse(c2.getOutboundEdges().contains(edge21));
        assertFalse(c3.getInboundEdges().contains(edge13));
        assertTrue(c2.getOutboundEdges().contains(edge23));
        assertTrue(c3.getInboundEdges().contains(edge23));
    }
}
