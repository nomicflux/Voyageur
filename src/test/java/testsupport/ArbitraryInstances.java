package testsupport;

import com.jnape.palatable.lambda.adt.Unit;
import io.ataraxic.nomicflux.voyageur.impl.AdjListGraph;
import io.ataraxic.nomicflux.voyageur.impl.ValueEdge;
import io.ataraxic.nomicflux.voyageur.impl.ValueNode;
import dev.marksman.gauntlet.Arbitrary;

import java.util.ArrayList;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Into.into;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static com.jnape.palatable.lambda.io.IO.io;
import static io.ataraxic.nomicflux.voyageur.impl.AdjListGraph.fromChains;
import static java.util.Arrays.asList;

public final class ArbitraryInstances {
    public static <A> Arbitrary<ValueNode<A, Unit>> nodes(Arbitrary<A> values) {
        return values.convert(ValueNode::node, ValueNode::getValue);
    }

    public static <A> Arbitrary<ValueEdge<A, Unit, Unit>> edges(Arbitrary<A> values) {
        return nodes(values).pair().convert(into(ValueEdge::edgeFromTo), e -> tuple(e.getNodeFrom(), e.getNodeTo()));
    }

    public static <A> Arbitrary<AdjListGraph<A, Unit, Unit>> graphs(Arbitrary<A> values) {
        return edges(values)
                .arrayList()
                .convert(lls -> fromChains(map(t -> asList(t.getNodeFrom().getValue(), t.getNodeTo().getValue()), lls)),
                        g -> g.simpleFold((l, c) -> foldLeft((acc, next) -> acc.flatMap(l_ -> io(() -> l_.add(next)).fmap(constantly(l_))),
                                io(() -> l),
                                c.getInboundEdges().union(c.getOutboundEdges()))
                                        .unsafePerformIO(),
                                new ArrayList<>()));
    }
}
