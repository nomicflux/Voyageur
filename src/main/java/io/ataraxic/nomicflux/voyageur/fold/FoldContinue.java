package io.ataraxic.nomicflux.voyageur.fold;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.coproduct.CoProduct3;
import com.jnape.palatable.lambda.functions.Fn1;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;

/**
 * Provide the next node in a fold, decompose a graph arbitrarily, or end the fold
 * Isomorphic to {@code Choice3<Unit, Unit, N>}
 *
 * @param <N> Value provided in the {@link NextNode} case
 */
public abstract class FoldContinue<N> implements CoProduct3<Unit, Unit, N, FoldContinue<N>> {

    /**
     * Provide the next node in a fold.
     *
     * @param node The node to provide
     * @param <N>  The type of the node
     * @return A {@code FoldContinue} with {@code node}
     */
    public static <N> FoldContinue<N> nextNode(N node) {
        return new NextNode<>(node);
    }

    /**
     * Provide the next node if available, otherwise terminate the fold.
     *
     * @param node A {@link Maybe} node to provide, if available
     * @param <N>  The type of the node
     * @return Either a node {@code N}, or a signal to terminate the fold
     */
    @SuppressWarnings("unchecked")
    public static <N> FoldContinue<N> nodeOrTerminate(Maybe<N> node) {
        return node.match(constantly((FoldContinue<N>) EndFold.INSTANCE),
                FoldContinue::nextNode);
    }

    /**
     * Provide the next node if available, otherwise decompose the graph arbitrarily.
     *
     * @param node A {@link Maybe} node to provide, if available
     * @param <N>  The type of the node
     * @return Either a node {@code N}, or a signal to decompose the graph without a node
     */
    @SuppressWarnings("unchecked")
    public static <N> FoldContinue<N> nodeOrDecompose(Maybe<N> node) {
        return node.match(constantly((FoldContinue<N>) Decompose.INSTANCE),
                FoldContinue::nextNode);
    }

    /**
     * @param <N> The type of a node that would have been provided
     * @return A signal to decompose the graph without a node
     */
    @SuppressWarnings("unchecked")
    public static <N> FoldContinue<N> decompose() {
        return (FoldContinue<N>) Decompose.INSTANCE;
    }

    /**
     * @param <N> The type of a node that would have been provided
     * @return A signal to end the fold
     */
    @SuppressWarnings("unchecked")
    public static <N> FoldContinue<N> endFold() {
        return (FoldContinue<N>) EndFold.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    abstract public <R> R match(Fn1<? super Unit, ? extends R> terminateFn, Fn1<? super Unit, ? extends R> decomposeFn, Fn1<? super N, ? extends R> nextNodeFn);

    private static class EndFold<N> extends FoldContinue<N> {
        private static EndFold INSTANCE = new EndFold();

        private EndFold() {
        }

        @Override
        public <R> R match(Fn1<? super Unit, ? extends R> terminateFn, Fn1<? super Unit, ? extends R> decomposeFn, Fn1<? super N, ? extends R> nextNodeFn) {
            return terminateFn.apply(UNIT);
        }
    }

    private static class Decompose<N> extends FoldContinue<N> {
        private static Decompose INSTANCE = new Decompose();

        private Decompose() {
        }

        @Override
        public <R> R match(Fn1<? super Unit, ? extends R> terminateFn, Fn1<? super Unit, ? extends R> decomposeFn, Fn1<? super N, ? extends R> nextNodeFn) {
            return decomposeFn.apply(UNIT);
        }
    }

    private static class NextNode<N> extends FoldContinue<N> {
        private N node;

        private NextNode(N node) {
            this.node = node;
        }

        @Override
        public <R> R match(Fn1<? super Unit, ? extends R> terminateFn, Fn1<? super Unit, ? extends R> decomposeFn, Fn1<? super N, ? extends R> nextNodeFn) {
            return nextNodeFn.apply(node);
        }
    }

}
