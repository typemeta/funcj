package org.javafp.parsec4j;

import org.javafp.data.Functions.*;

import static org.javafp.parsec4j.Parsers.ap;

public class ApplyBuilder {
    public static class _2<I, A, B> {
        private final P<I, A> pa;
        private final P<I, B> pb;

        _2(P<I, A> pa, P<I, B> pb) {
            this.pa = pa;
            this.pb = pb;
        }

        public <R> P<I, R> map(F<A, F<B, R>> f) {
            return ap(pa.map(f), pb);
        }

        public <R> P<I, R> map(F2<A, B, R> f) {
            return map(f.curry());
        }

        public <C> _2<I, A, B> andL(P<I, C> pc) {
            return new _2<I, A, B>(pa, pb.andL(pc));
        }

        public <C> _3<C> and(P<I, C> pc) {
            return new _3<C>(pc);
        }

        public class _3<C> {
            private final P<I, C> pc;

            private _3(P<I, C> pc) {
                this.pc = pc;
            }

            public <R> P<I, R> map(F<A, F<B, F<C, R>>> f) {
                return ap(_2.this.map(f), pc);
            }

            public <R> P<I, R> map(F3<A, B, C, R> f) {
                return map(f.curry());
            }

            public <D> _3<C> andL(P<I, D> pd) {
                return new _3<C>(pc.andL(pd));
            }

            public <D> _4<D> and(P<I, D> pd) {
                return new _4<D>(pd);
            }

            public class _4<D> {
                private final P<I, D> pd;

                private _4(P<I, D> pd) {
                    this.pd = pd;
                }

                public <R> P<I, R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                    return ap(_3.this.map(f), pd);
                }

                public <R> P<I, R> map(F4<A, B, C, D, R> f) {
                    return map(f.curry());
                }

                public <E> _4<D> andL(P<I, E> pe) {
                    return new _4<D>(pd.andL(pe));
                }

                public <E> _5<E> and(P<I, E> pe) {
                    return new _5<E>(pe);
                }

                public class _5<E> {
                    private final P<I, E> pe;

                    private _5(P<I, E> pe) {
                        this.pe = pe;
                    }

                    public <R> P<I, R> map(F<A, F<B, F<C, F<D, F<E, R>>>>> f) {
                        return ap(_4.this.map(f), pe);
                    }

                    public <R> P<I, R> map(F5<A, B, C, D, E, R> f) {
                        return map(f.curry());
                    }

                    public <G> _5<E> andL(P<I, G> pg) {
                        return new _5<E>(pe.andL(pg));
                    }

                    public <G> _6<G> and(P<I, G> pg) {
                        return new _6<G>(pg);
                    }

                    public class _6<G> {
                        private final P<I, G> pg;

                        private _6(P<I, G> pg) {
                            this.pg = pg;
                        }

                        public <R> P<I, R> map(F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> f) {
                            return ap(_5.this.map(f), pg);
                        }

                        public <R> P<I, R> map(F6<A, B, C, D, E, G, R> f) {
                            return map(f.curry());
                        }

                        public <H> _6<G> andL(P<I, H> ph) {
                            return new _6<G>(pg.andL(ph));
                        }
                    }
                }
            }
        }
    }
}
