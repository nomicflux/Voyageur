package io.ataraxic.nomicflux.voyageur.impl;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.shoki.impl.HashMap;
import com.jnape.palatable.shoki.impl.HashSet;
import io.ataraxic.nomicflux.voyageur.Context;
import io.ataraxic.nomicflux.voyageur.Graph;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Drop.drop;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Zip.zip;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.shoki.impl.HashMap.hashMap;
import static com.jnape.palatable.shoki.impl.HashSet.hashSet;
import static io.ataraxic.nomicflux.voyageur.impl.ValueContext.context;
import static io.ataraxic.nomicflux.voyageur.impl.ValueEdge.edgeFromTo;
import static io.ataraxic.nomicflux.voyageur.impl.ValueNode.node;

public final class AdjListGraph<A, L, W> implements Graph<A, ValueNode<A, L>, ValueEdge<A, L, W>, HashSet<ValueEdge<A, L, W>>, AdjListGraph<A, L, W>> {
    private final HashMap<ValueNode<A, L>, Tuple2<HashSet<ValueEdge<A, L, W>>, HashSet<ValueEdge<A, L, W>>>> graph;

    private AdjListGraph(HashMap<ValueNode<A, L>, Tuple2<HashSet<ValueEdge<A, L, W>>, HashSet<ValueEdge<A, L, W>>>> graph) {
        this.graph = graph;
    }

    public Boolean isEmpty() {
        return graph.isEmpty();
    }

    public static <A, L, W> AdjListGraph<A, L, W> emptyGraph() {
        return new AdjListGraph<>(hashMap());
    }

    public static <A, L, W> AdjListGraph<A, L, W> singletonGraph(ValueNode<A, L> a) {
        return new AdjListGraph<>(hashMap(tuple(a, tuple(hashSet(), hashSet()))));
    }

    public static <A, L, W> AdjListGraph<A, L, W> fromEdge(ValueEdge<A, L, W> edge) {
        return AdjListGraph.<A, L, W>emptyGraph().addEdge(edge);
    }

    public static <A, L, W, I extends Iterable<ValueEdge<A, L, W>>> AdjListGraph<A, L, W> fromEdges(I edges) {
        AdjListGraph<A, L, W> empty = AdjListGraph.<A, L, W>emptyGraph();
        return foldLeft(AdjListGraph::addEdge, empty, edges);
    }

    public static <A> AdjListGraph<A, Unit, Unit> fromChain(Iterable<A> values) {
        return addFromChain(emptyGraph(), values);
    }

    private static <A> AdjListGraph<A, Unit, Unit> addFromChain(AdjListGraph<A, Unit, Unit> graph, Iterable<A> values) {
        return foldLeft((acc, next) -> acc.addEdge(edgeFromTo(node(next._1()), node(next._2()))), graph, zip(values, drop(1, values)));
    }

    public static <A> AdjListGraph<A, Unit, Unit> fromChains(Iterable<Iterable<A>> values) {
        AdjListGraph<A, Unit, Unit> empty = emptyGraph();
        return foldLeft(AdjListGraph::addFromChain, empty, values);
    }

    @Override
    public AdjListGraph<A, L, W> addEdge(ValueEdge<A, L, W> edge) {
        HashMap<ValueNode<A, L>, Tuple2<HashSet<ValueEdge<A, L, W>>, HashSet<ValueEdge<A, L, W>>>> withNodes =
                this.addNode(edge.getNodeFrom()).addNode(edge.getNodeTo()).graph;

        HashMap<ValueNode<A, L>, Tuple2<HashSet<ValueEdge<A, L, W>>, HashSet<ValueEdge<A, L, W>>>> updateFromEdge =
                withNodes
                        .get(edge.getNodeFrom())
                        .fmap(res -> withNodes.put(edge.getNodeFrom(), res.biMapL(l -> l.add(edge))))
                        .orElse(withNodes);
        HashMap<ValueNode<A, L>, Tuple2<HashSet<ValueEdge<A, L, W>>, HashSet<ValueEdge<A, L, W>>>> updateToEdge =
                updateFromEdge
                        .get(edge.getNodeTo())
                        .fmap(res -> updateFromEdge.put(edge.getNodeTo(), res.biMapR(r -> r.add(edge))))
                        .orElse(updateFromEdge);

        return new AdjListGraph<A, L, W>(updateToEdge);
    }

    @Override
    public AdjListGraph<A, L, W> removeEdge(ValueEdge<A, L, W> edge) {

        HashMap<ValueNode<A, L>, Tuple2<HashSet<ValueEdge<A, L, W>>, HashSet<ValueEdge<A, L, W>>>> updateFromEdge =
                graph
                        .get(edge.getNodeFrom())
                        .fmap(res -> graph.put(edge.getNodeFrom(), res.biMapL(l -> l.remove(edge))))
                        .orElse(graph);
        HashMap<ValueNode<A, L>, Tuple2<HashSet<ValueEdge<A, L, W>>, HashSet<ValueEdge<A, L, W>>>> updateToEdge =
                updateFromEdge
                        .get(edge.getNodeTo())
                        .fmap(res -> updateFromEdge.put(edge.getNodeTo(), res.biMapR(r -> r.remove(edge))))
                        .orElse(updateFromEdge);

        return new AdjListGraph<A, L, W>(updateToEdge);
    }

    @Override
    public AdjListGraph<A, L, W> addNode(ValueNode<A, L> node) {
        Maybe<Tuple2<HashSet<ValueEdge<A, L, W>>, HashSet<ValueEdge<A, L, W>>>> tuple2Maybe = graph.get(node);
        return new AdjListGraph<>(tuple2Maybe.match(constantly(graph.put(node, tuple(hashSet(), hashSet()))),
                edges -> graph.put(node, edges)));
    }

    @Override
    public AdjListGraph<A, L, W> removeNode(ValueNode<A, L> node) {
        AdjListGraph<A, L, W> match = graph.get(node).match(constantly(this),
                res -> foldLeft((g, e) -> g.removeEdge(e).removeEdge(e.swap()), this, res._1().union(res._2())));
        return new AdjListGraph<>(match.graph.remove(node));
    }

    @Override
    public Maybe<Tuple2<Context<A, ValueNode<A, L>, ValueEdge<A, L, W>, HashSet<ValueEdge<A, L, W>>>, AdjListGraph<A, L, W>>> decompose() {
        return graph.head().flatMap(n -> atNode(n._1()).projectB());
    }

    @Override
    public Choice2<AdjListGraph<A, L, W>, Tuple2<Context<A, ValueNode<A, L>, ValueEdge<A, L, W>, HashSet<ValueEdge<A, L, W>>>, AdjListGraph<A, L, W>>> atNode(ValueNode<A, L> node) {
        Maybe<Tuple2<HashSet<ValueEdge<A, L, W>>, HashSet<ValueEdge<A, L, W>>>> view = graph.get(node);
        return view.match(constantly(Choice2.a(this)),
                res -> {
                    Fn2<AdjListGraph<A, L, W>, ValueEdge<A, L, W>, AdjListGraph<A, L, W>> removeEdge = AdjListGraph::removeEdge;
                    Context<A, ValueNode<A, L>, ValueEdge<A, L, W>, HashSet<ValueEdge<A, L, W>>> context = context(node, res._1(), res._2());
                    return Choice2.b(tuple(context, this.removeNode(node)));
                });
    }
}
