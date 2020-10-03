package io.ataraxic.nomicflux.voyageur;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.shoki.impl.StrictQueue;
import io.ataraxic.nomicflux.voyageur.impl.AdjListGraph;
import io.ataraxic.nomicflux.voyageur.impl.ValueEdge;
import io.ataraxic.nomicflux.voyageur.impl.ValueNode;
import io.ataraxic.nomicflux.voyageur.path.DFPath;

import java.util.List;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Iterate.iterate;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Map.map;
import static com.jnape.palatable.lambda.functions.builtin.fn2.Take.take;
import static com.jnape.palatable.lambda.functions.builtin.fn2.ToCollection.toCollection;
import static io.ataraxic.nomicflux.voyageur.impl.AdjListGraph.fromChains;
import static io.ataraxic.nomicflux.voyageur.impl.ValueNode.node;
import static java.time.LocalDateTime.now;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;

public class PerformanceTests {

    public static void main(String[] args) {
        Iterable<Integer> nums = iterate(x -> x + 1, 0);
        List<Iterable<Integer>> values = asList(map(x -> x * 2, take(500, nums)),
                map(x -> x * 2 + 1, take(500, nums)),
                map(x -> x * 3, take(333, nums)),
                map(x -> x * 3 + 1, take(333, nums)),
                map(x -> x * 3 + 2, take(333, nums)),
                map(x -> x * 5, take(200, nums)),
                map(x -> x * 5 + 1, take(200, nums)),
                map(x -> x * 5 + 2, take(200, nums)),
                map(x -> x * 5 + 3, take(200, nums)),
                map(x -> x * 5 + 4, take(200, nums)));
        AdjListGraph<Integer, Unit, Unit> graph = fromChains(values);

        for(int i : take(100, nums)) {
            long start = now().atZone(UTC).toInstant().toEpochMilli();
            StrictQueue<Tuple2<Maybe<ValueEdge<Integer, Unit, Unit>>, ValueNode<Integer, Unit>>> result = DFPath.dfPath(graph, node(0), 999);
            result.forEach(t -> {

            });
//            System.out.println(result);
            long end = now().atZone(UTC).toInstant().toEpochMilli();
            System.out.println(end - start);
        }
    }
}
