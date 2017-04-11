package org.javafp.parsec4j;

import org.javafp.util.*;
import org.javafp.util.Functions.Predicate;

import java.util.*;

public interface SymSet<I> {
    enum Type {
        EMPTY,
        ALL,
        VALUE,
        PRED,
        UNION
    }

    static <I> SymSet<I> empty() {
        return (SymSet<I>) Empty.INSTANCE;
    }

    static <I> SymSet<I> all() {
        return (SymSet<I>) All.INSTANCE;
    }

    static <I> SymSet<I> value(I value) {
        return new Value<I>(value);
    }

    static <I> SymSet<I> pred(Predicate<I> pred) {
        return new Pred<I>(pred);
    }

    class Empty<I> implements SymSet<I> {
        static final Empty<Unit> INSTANCE = new Empty<Unit>();

        @Override
        public Type type() {
            return Type.EMPTY;
        }

        @Override
        public boolean matches(I value) {
            return false;
        }

        @Override
        public SymSet<I> union(SymSet<I> rhs) {
            return rhs;
        }
    }

    class All<I> implements SymSet<I> {
        static final All<Unit> INSTANCE = new All<Unit>();

        @Override
        public Type type() {
            return Type.ALL;
        }

        @Override
        public boolean matches(I value) {
            return true;
        }

        @Override
        public SymSet<I> union(SymSet<I> rhs) {
            return this;
        }
    }

    interface Leaf<I> extends SymSet<I> {
        @Override
        default SymSet<I> union(SymSet<I> rhs) {
            switch(rhs.type()) {
                case EMPTY:
                    return this;
                case ALL:
                    return rhs;
                case VALUE:
                    return new Union<I>(this, (Leaf<I>)rhs);
                case PRED:
                    return new Union<I>(this, (Leaf<I>)rhs);
                case UNION:
                    return new Union<I>((Union<I>)rhs, this);
                default:
                    throw new IllegalArgumentException("SymSet rhs has unrecognised type - " + rhs.type());
            }
        }
    }

    class Value<I> implements Leaf<I> {

        public final I value;

        public Value(I value) {
            this.value = value;
        }

        @Override
        public Type type() {
            return Type.VALUE;
        }

        @Override
        public boolean matches(I value) {
            return this.value.equals(value);
        }

    }

    class Pred<I> implements Leaf<I> {

        public final Predicate<I> pred;

        public Pred(Predicate<I> pred) {
            this.pred = pred;
        }

        @Override
        public Type type() {
            return Type.PRED;
        }

        @Override
        public boolean matches(I value) {
            return pred.apply(value);
        }
    }

    class Union<I> implements SymSet<I> {

        public final Set<SymSet<I>> members;

        public Union(Set<SymSet<I>> members) {
            this.members = members;
        }

        public Union(Leaf<I> memberA, Leaf<I> memberB) {
            this.members = new HashSet<SymSet<I>>();
            this.members.add(memberA);
            this.members.add(memberB);
        }

        public Union(Union<I> comp, Leaf<I> member) {
            this.members = new HashSet<SymSet<I>>(comp.members);
            this.members.add(member);
        }

        public Union(Union<I> compA, Union<I> compB) {
            this.members = new HashSet<SymSet<I>>(compA.members);
            this.members.addAll(compB.members);
        }

        @Override
        public Type type() {
            return Type.UNION;
        }

        @Override
        public boolean matches(I value) {
            for (SymSet<I> ss : members) {
                if (ss.matches(value)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public SymSet<I> union(SymSet<I> rhs) {
            switch(rhs.type()) {
                case EMPTY:
                    return this;
                case ALL:
                    return rhs;
                case VALUE:
                    return new Union<I>(this, (Leaf<I>)rhs);
                case PRED:
                    return new Union<I>(this, (Leaf<I>)rhs);
                case UNION:
                    return new Union<I>(this, (Union<I>)rhs);
                default:
                    throw new IllegalArgumentException("SymSet rhs has unrecognised type - " + rhs.type());
            }
        }
    }

    Type type();

    boolean matches(I value);

    SymSet<I> union(SymSet<I> rhs);
}
