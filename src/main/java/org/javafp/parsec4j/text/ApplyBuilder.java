package org.javafp.parsec4j.text;

import org.javafp.util.Functions.*;

import static org.javafp.parsec4j.text.Parser.ap;

public class ApplyBuilder {
    public static class _2<A, B> {
        private final Parser<A> pa;
        private final Parser<B> pb;

        _2(Parser<A> pa, Parser<B> pb) {
            this.pa = pa;
            this.pb = pb;
        }

        public <R> Parser<R> map(F<A, F<B, R>> f) {
            return ap(pa.map(f), pb);
        }

        public <R> Parser<R> map(F2<A, B, R> f) {
            return map(f.curry());
        }

        public <C> _2<A, B> andL(Parser<C> pc) {
            return new _2<A, B>(pa, pb.andL(pc));
        }

        public <C> _3<C> and(Parser<C> pc) {
            return new _3<C>(pc);
        }

        public class _3<C> {
            private final Parser<C> pc;

            private _3(Parser<C> pc) {
                this.pc = pc;
            }

            public <R> Parser<R> map(F<A, F<B, F<C, R>>> f) {
                return ap(_2.this.map(f), pc);
            }

            public <R> Parser<R> map(F3<A, B, C, R> f) {
                return map(f.curry());
            }

            public <D> _3<C> andL(Parser<D> pd) {
                return new _3<C>(pc.andL(pd));
            }

            public <D> _4<D> and(Parser<D> pd) {
                return new _4<D>(pd);
            }

            public class _4<D> {
                private final Parser<D> pd;

                private _4(Parser<D> pd) {
                    this.pd = pd;
                }

                public <R> Parser<R> map(F<A, F<B, F<C, F<D, R>>>> f) {
                    return ap(_3.this.map(f), pd);
                }

                public <R> Parser<R> map(F4<A, B, C, D, R> f) {
                    return map(f.curry());
                }

                public <E> _4<D> andL(Parser<E> pe) {
                    return new _4<D>(pd.andL(pe));
                }

                public <E> _5<E> and(Parser<E> pe) {
                    return new _5<E>(pe);
                }

                public class _5<E> {
                    private final Parser<E> pe;

                    private _5(Parser<E> pe) {
                        this.pe = pe;
                    }

                    public <R> Parser<R> map(F<A, F<B, F<C, F<D, F<E, R>>>>> f) {
                        return ap(_4.this.map(f), pe);
                    }

                    public <R> Parser<R> map(F5<A, B, C, D, E, R> f) {
                        return map(f.curry());
                    }

                    public <G> _5<E> andL(Parser<G> pg) {
                        return new _5<E>(pe.andL(pg));
                    }

                    public <G> _6<G> and(Parser<G> pg) {
                        return new _6<G>(pg);
                    }

                    public class _6<G> {
                        private final Parser<G> pg;

                        private _6(Parser<G> pg) {
                            this.pg = pg;
                        }

                        public <R> Parser<R> map(F<A, F<B, F<C, F<D, F<E, F<G, R>>>>>> f) {
                            return ap(_5.this.map(f), pg);
                        }

                        public <R> Parser<R> map(F6<A, B, C, D, E, G, R> f) {
                            return map(f.curry());
                        }

                        public <H> _6<G> andL(Parser<H> ph) {
                            return new _6<G>(pg.andL(ph));
                        }
                    }
                }
            }
        }
    }
}
