package org.javafp.parsec4j;

import org.javafp.util.Functions.Predicate;
import org.javafp.util.Unit;

import java.util.*;

public abstract class SymSet<I> {
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

    static <I> SymSet<I> pred(String name, Predicate<I> pred) {
        return new Pred<I>(name, pred);
    }

    static class Empty<I> extends SymSet<I> {
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

        @Override
        public StringBuilder append(StringBuilder sb) {
            return sb.append("<empty>");
        }
    }

    static class All<I> extends SymSet<I> {
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

        @Override
        public StringBuilder append(StringBuilder sb) {
            return sb.append("all");
        }
    }

    static class Value<I> extends SymSet<I> {

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

        @Override
        public SymSet<I> union(SymSet<I> rhs) {
            switch(rhs.type()) {
                case EMPTY:
                    return this;
                case ALL:
                    return rhs;
                case VALUE:
                    return new Union<I>(this, (Value<I>)rhs);
                case PRED:
                    return new Union<I>(this, (Pred<I>)rhs);
                case UNION:
                    return new Union<I>((Union<I>)rhs, this);
                default:
                    throw new IllegalArgumentException("SymSet rhs has unrecognised type - " + rhs.type());
            }
        }

        @Override
        public StringBuilder append(StringBuilder sb) {
            // TODO: handle whitespace.
            return sb.append(value);
        }
    }

    static class Pred<I> extends SymSet<I> {

        public String name;
        public final Predicate<I> pred;

        public Pred(String name, Predicate<I> pred) {
            this.name = name;
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

        @Override
        public SymSet<I> union(SymSet<I> rhs) {
            switch(rhs.type()) {
                case EMPTY:
                    return this;
                case ALL:
                    return rhs;
                case VALUE:
                    return new Union<I>((Value<I>)rhs, this);
                case PRED:
                    return new Union<I>(this, (Pred<I>)rhs);
                case UNION:
                    return new Union<I>((Union<I>)rhs, this);
                default:
                    throw new IllegalArgumentException("SymSet rhs has unrecognised type - " + rhs.type());
            }
        }

        @Override
        public StringBuilder append(StringBuilder sb) {
            return sb.append('<').append(name).append('>');
        }
    }

    static class Union<I> extends SymSet<I> {

        public final Set<I> values;
        public final List<Pred<I>> preds;

        public Union(Union<I> unionA, Union<I> unionB) {
            this.values = new HashSet<I>(unionA.values);
            this.values.addAll(unionB.values);
            this.preds = new ArrayList<Pred<I>>(unionA.preds);
            this.preds.addAll(unionB.preds);
        }

        public Union(Union<I> union, Value<I> value) {
            this.values = new HashSet<I>(union.values);
            this.values.add(value.value);
            this.preds = union.preds;
        }

        public Union(Union<I> union, Pred<I> pred) {
            this.values = union.values;
            this.preds = new ArrayList<Pred<I>>(union.preds);
            this.preds.add(pred);
        }

        public Union(Value<I> valueA, Value<I> valueB) {
            this.values = new HashSet<I>();
            this.values.add(valueA.value);
            this.values.add(valueB.value);
            this.preds = Collections.emptyList();
        }

        public Union(Value<I> value, Pred<I> pred) {
            this.values = Collections.singleton(value.value);
            this.preds = Collections.singletonList(pred);
        }

        public Union(Pred<I> predA, Pred<I> predB) {
            this.values = Collections.emptySet();
            this.preds = new ArrayList<Pred<I>>(2);
            this.preds.add(predA);
            this.preds.add(predB);
        }

        @Override
        public Type type() {
            return Type.UNION;
        }

        @Override
        public boolean matches(I value) {
            if (values.contains(value)) {
                return true;
            } else {
                for (Pred<I> pred : preds) {
                    if (pred.matches(value)) {
                        return true;
                    }
                }

                return false;
            }
        }

        @Override
        public SymSet<I> union(SymSet<I> rhs) {
            switch(rhs.type()) {
                case EMPTY:
                    return this;
                case ALL:
                    return rhs;
                case VALUE:
                    return new Union<I>(this, (Value<I>)rhs);
                case PRED:
                    return new Union<I>(this, (Pred<I>)rhs);
                case UNION:
                    return new Union<I>(this, (Union<I>)rhs);
                default:
                    throw new IllegalArgumentException("SymSet rhs has unrecognised type - " + rhs.type());
            }
        }

        @Override
        public StringBuilder append(StringBuilder sb) {
            boolean first = true;
            for (I val : values) {
                if (first) {
                    first = false;
                } else {
                    sb.append(' ');
                }
                sb.append(val);
            }
            for (Pred<I> pred : preds) {
                if (first) {
                    first = false;
                } else {
                    sb.append(' ');
                }
                pred.append(sb);
            }
            return sb;
        }
    }

    abstract Type type();

    abstract boolean matches(I value);

    abstract SymSet<I> union(SymSet<I> rhs);

    abstract StringBuilder append(StringBuilder sb);

    @Override
    public String toString() {
        return append(new StringBuilder()).toString();
    }

}
