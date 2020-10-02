package com.nomicflux.voyageur.fold;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.coproduct.CoProduct3;
import com.jnape.palatable.lambda.functions.Fn1;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;

public abstract class FoldContinue<N> implements CoProduct3<Unit, Unit, N, FoldContinue<N>> {

    public static <N> FoldContinue<N> nextNode(N node) {
        return new NextNode<>(node);
    }

    @SuppressWarnings("unchecked")
    public static <N> FoldContinue<N> maybeTerminates(Maybe<N> node) {
        return node.match(constantly((FoldContinue<N>) EndFold.INSTANCE),
                FoldContinue::nextNode);
    }

    @SuppressWarnings("unchecked")
    public static <N> FoldContinue<N> maybeContinues(Maybe<N> node) {
        return node.match(constantly((FoldContinue<N>) Decompose.INSTANCE),
                FoldContinue::nextNode);
    }

    @SuppressWarnings("unchecked")
    public static <N> FoldContinue<N> decompose() {
        return (FoldContinue<N>) Decompose.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public static <N> FoldContinue<N> endFold() {
        return (FoldContinue<N>) EndFold.INSTANCE;
    }

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
