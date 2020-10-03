package io.ataraxic.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.shoki.impl.HashSet;
import io.ataraxic.nomicflux.voyageur.Context;
import org.junit.jupiter.api.Test;

import static com.jnape.palatable.lambda.adt.Maybe.nothing;
import static com.jnape.palatable.shoki.impl.HashSet.hashSet;
import static io.ataraxic.nomicflux.voyageur.impl.AdjListGraph.*;
import static io.ataraxic.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static io.ataraxic.nomicflux.voyageur.impl.ValueNode.labeledNode;
import static io.ataraxic.nomicflux.voyageur.impl.ValueNode.node;
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
        AdjListGraph<Integer, Unit, Unit> graph = singletonGraph(node(1));
        Tuple2<Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>>, AdjListGraph<Integer, Unit, Unit>> decomposed = graph.decompose().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node(1)));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet()));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet()));
        assertTrue(decomposed._2().isEmpty());
    }

    @Test
    public void labeledNodeRemainsLabeled() {
        ValueNode<Integer, String> node = labeledNode(1, "hello");
        AdjListGraph<Integer, String, Unit> graph = singletonGraph(node);
        Tuple2<Context<Integer, ValueNode<Integer, String>, ValueEdge<Integer, String, Unit>, HashSet<ValueEdge<Integer, String, Unit>>>, AdjListGraph<Integer, String, Unit>> decomposed = graph.decompose().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node));
        assertThat(decomposed._1().getNode().getLabel(), equalTo(node.getLabel()));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet()));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet()));
        assertTrue(decomposed._2().isEmpty());
    }

    @Test
    public void singletonCanBeDecomposedAtNode() {
        AdjListGraph<Integer, Unit, Unit> graph = singletonGraph(node(1));
        Tuple2<Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>>, AdjListGraph<Integer, Unit, Unit>> decomposed = graph.atNode(node(1)).projectB().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node(1)));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet()));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet()));
        assertTrue(decomposed._2().isEmpty());
    }

    @Test
    public void singletonReturnsGraphForOtherNodes() {
        AdjListGraph<Integer, Unit, Unit> graph = singletonGraph(node(1));
        AdjListGraph<Integer, Unit, Unit> atNode = graph.atNode(node(2)).projectA().orElseThrow(AssertionError::new);

        assertFalse(atNode.isEmpty());
    }

    @Test
    public void singleEdgeCanBeDecomposed() {
        ValueEdge<Integer, Unit, Unit> edge = edgeFromTo(node(1), node(2));
        AdjListGraph<Integer, Unit, Unit> graph = fromEdge(edge);
        Tuple2<Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>>, AdjListGraph<Integer, Unit, Unit>> decomposed = graph.decompose().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getInboundEdges().union(decomposed._1().getOutboundEdges()), equalTo(hashSet(edge)));
        assertFalse(decomposed._2().isEmpty());
    }

    @Test
    public void singleEdgeAtFromNode() {
        ValueEdge<Integer, Unit, Unit> edge = edgeFromTo(node(1), node(2));
        AdjListGraph<Integer, Unit, Unit> graph = fromEdge(edge);
        Tuple2<Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>>, AdjListGraph<Integer, Unit, Unit>> decomposed = graph.atNode(node(1)).projectB().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node(1)));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet()));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet(edge)));
        assertFalse(decomposed._2().isEmpty());
    }

    @Test
    public void singleEdgeAtToNode() {
        ValueEdge<Integer, Unit, Unit> edge = edgeFromTo(node(1), node(2));
        AdjListGraph<Integer, Unit, Unit> graph = fromEdge(edge);
        Tuple2<Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>>, AdjListGraph<Integer, Unit, Unit>> decomposed = graph.atNode(node(2)).projectB().orElseThrow(AssertionError::new);
        assertThat(decomposed._1().getNode(), equalTo(node(2)));
        assertThat(decomposed._1().getInboundEdges(), equalTo(hashSet(edge)));
        assertThat(decomposed._1().getOutboundEdges(), equalTo(hashSet()));
        assertFalse(decomposed._2().isEmpty());
    }

    @Test
    public void removesNodeFromAllRelevantEdges() {
        ValueEdge<Integer, Unit, Unit> edge12 = edgeFromTo(node(1), node(2));
        ValueEdge<Integer, Unit, Unit> edge21 = edgeFromTo(node(2), node(1));
        ValueEdge<Integer, Unit, Unit> edge13 = edgeFromTo(node(1), node(3));
        ValueEdge<Integer, Unit, Unit> edge23 = edgeFromTo(node(2), node(3));
        AdjListGraph<Integer, Unit, Unit> graph = fromEdges(asList(edge12, edge21, edge13, edge23));
        AdjListGraph<Integer, Unit, Unit> withoutOne = graph.removeNode(edge12.getNodeFrom());
        assertThat(withoutOne.atNode(edge12.getNodeFrom()).projectB(), equalTo(nothing()));
        Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>> c2 = withoutOne.atNode(edge23.getNodeFrom()).projectB().orElseThrow(AssertionError::new)._1();
        Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>> c3 = withoutOne.atNode(edge23.getNodeTo()).projectB().orElseThrow(AssertionError::new)._1();
        assertFalse(c2.getInboundEdges().contains(edge12));
        assertFalse(c2.getOutboundEdges().contains(edge21));
        assertFalse(c3.getInboundEdges().contains(edge13));
        assertTrue(c2.getOutboundEdges().contains(edge23));
        assertTrue(c3.getInboundEdges().contains(edge23));
    }

    @Test
    public void addsLinearChains() {
        AdjListGraph<Integer, Unit, Unit> graph = fromChain(asList(1, 2, 3, 4, 5));
        Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>> c2 = graph.atNode(node(2)).projectB().orElseThrow(AssertionError::new)._1();
        Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>> c5 = graph.atNode(node(5)).projectB().orElseThrow(AssertionError::new)._1();
        assertTrue(c2.getInboundEdges().contains(edgeFromTo(node(1), node(2))));
        assertTrue(c2.getOutboundEdges().contains(edgeFromTo(node(2), node(3))));
        assertTrue(c5.getInboundEdges().contains(edgeFromTo(node(4), node(5))));
        assertTrue(c5.getOutboundEdges().isEmpty());
    }

    @Test
    public void addsMultipleChains() {
        AdjListGraph<Integer, Unit, Unit> graph = fromChains(asList(asList(1, 2, 3), asList(2, 4, 5), asList(3, 4, 6)));
        Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>> c2 = graph.atNode(node(2)).projectB().orElseThrow(AssertionError::new)._1();
        Context<Integer, ValueNode<Integer, Unit>, ValueEdge<Integer, Unit, Unit>, HashSet<ValueEdge<Integer, Unit, Unit>>> c4 = graph.atNode(node(4)).projectB().orElseThrow(AssertionError::new)._1();
        assertTrue(c2.getInboundEdges().contains(edgeFromTo(node(1), node(2))));
        assertTrue(c2.getOutboundEdges().contains(edgeFromTo(node(2), node(3))));
        assertTrue(c2.getOutboundEdges().contains(edgeFromTo(node(2), node(4))));
        assertTrue(c4.getInboundEdges().contains(edgeFromTo(node(2), node(4))));
        assertTrue(c4.getInboundEdges().contains(edgeFromTo(node(3), node(4))));
        assertTrue(c4.getOutboundEdges().contains(edgeFromTo(node(4), node(5))));
        assertTrue(c4.getOutboundEdges().contains(edgeFromTo(node(4), node(6))));
    }
}
