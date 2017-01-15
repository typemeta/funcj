package org.javafp.parsec4j;

import org.javafp.data.Functions.*;

import static org.javafp.parsec4j.Parser.ap;

public class ApplyBuilder {
    public static class _2<I, CTX extends Parser.Context<I>, A, B> {
        private final Parser<I, CTX, A> pa;
        private final Parser<I, CTX, B> pb;

        _2(Parser<I, CTX, A> pa, Parser<I, CTX, B> pb) {
            this.pa = pa;
            this.pb = pb;
        }

        public <R> Parser<I, CTX, R> map(F<A, F<B, R>> f) {
            return ap(pa.map(f), pb);
        }

        public <R> Parser<I, CTX, R> map(F2<A, B, R> f) {
            return map(f.curry());
        }

        public <C> _2<I, CTX, A, B> andL(Parser<I, CTX, C> pc) {
            return new _2<I, CTX, A, B>(pa, pb.andL(pc));
        }

        public <C> _3<C> and(Parser<I, CTX, C> pc) {
            return new _3<C>(pc);
        }

        public class _3<C> {
            private final Parser<I, CTX, C> pc;

            private _3(Parser<I, CTX, C> pc) {
                this.pc = pc;
            }

            public <R> Parser<I, CTX, R> map(F<A, F<B, F<C, R>>> f) {
                return ap(_2.this.map(f), pc);
            }

            public <R> Parser<I, CTX, R> map(F3<A, B, C, R> f) {
                return map(f.curry());
            }

            public <D> _3<C> andL(Parser<I, CTX, D> pd) {
                return new _3<C>(pc.andL(pd));
            }

            public <D> _4<D> and(Parser<I, CTX, D> pd) {
                return new _4<D>(pd);
            }

            public class _4<D> {
                private final Parser<I, CTX, D> pd;

                private _4(Parser<I, CTX, D> pd) {
                    this.pd = pd;
                }

                public <R> Parser<I, CTX, R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                    return ap(_3.this.map(f), pd);
                }

                public <R> Parser<I, CTX, R> map(F4<A, B, C, D, R> f) {
                    return map(f.curry());
                }

                public <E> _4<D> andL(Parser<I, CTX, E> pe) {
                    return new _4<D>(pd.andL(pe));
                }

                public <E> _5<E> and(Parser<I, CTX, E> pe) {
                    return new _5<E>(pe);
                }

                public class _5<E> {
                    private final Parser<I, CTX, E> pe;

                    private _5(Parser<I, CTX, E> pe) {
                        this.pe = pe;
                    }

                    public <R> Parser<I, CTX, R> map(F<A, F<B, F<C, F<D, F<E, R>>>>> f) {
                        return ap(_4.this.map(f), pe);
                    }

                    public <R> Parser<I, CTX, R> map(F5<A, B, C, D, E, R> f) {
                        return map(f.curry());
                    }

                    public <G> _5<E> andL(Parser<I, CTX, G> pg) {
                        return new _5<E>(pe.andL(pg));
                    }

                    public <G> _6<G> and(Parser<I, CTX, G> pg) {
                        return new _6<G>(pg);
                    }

                    public class _6<G> {
                        private final Parser<I, CTX, G> pg;

                        private _6(Parser<I, CTX, G> pg) {
                            this.pg = pg;
                        }

                        public <R> Parser<I, CTX, R> map(F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> f) {
                            return ap(_5.this.map(f), pg);
                        }

                        public <R> Parser<I, CTX, R> map(F6<A, B, C, D, E, G, R> f) {
                            return map(f.curry());
                        }

                        public <H> _6<G> andL(Parser<I, CTX, H> ph) {
                            return new _6<G>(pg.andL(ph));
                        }
                    }
                }
            }
        }
    }
}
