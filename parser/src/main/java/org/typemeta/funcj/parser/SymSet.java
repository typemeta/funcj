package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.Unit;
import org.typemeta.funcj.functions.Functions;

import java.util.*;

import static org.typemeta.funcj.parser.SymSetUtils.typeError;

/**
 * An abstraction for an immutable set of symbols.
 * <p>
 * A {@code SymSet} represents an immutable set of symbols,
 * {@code SymSet}s are used for First Sets and Follow Sets.
 * Since the {@link Combinators#value(Object)} parsers are expressed using predictaes,
 * {@code SymSet} has to support these directly else every symbol that satisfies the predicate,
 * would have to be enumerated.
 * Consequently, a SymSet is expressed as the composition of symbols, predicates and set operations.
 * @param <I>           the input stream symbol type
 */
public interface SymSet<I> {
    enum Type {
        EMPTY,
        ALL,
        VALUE,
        PRED,
        UNION
    }

    @SuppressWarnings("unchecked")
    static <I> SymSet<I> empty() {
        return (SymSet<I>) Empty.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    static <I> SymSet<I> all() {
        return (SymSet<I>) All.INSTANCE;
    }

    static <I> SymSet<I> value(I value) {
        return new Value<I>(value);
    }

    static <I> SymSet<I> pred(String name, Functions.Predicate<I> pred) {
        return new Pred<I>(name, pred);
    }

    class Empty<I> implements SymSet<I> {
        static final Empty<Unit> INSTANCE = new Empty<>();

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
        public String toString() {
            return "<empty>";
        }

        @Override
        public StringBuilder append(StringBuilder sb) {
            return sb.append(toString());
        }
    }

    class All<I> implements SymSet<I> {
        static final All<Unit> INSTANCE = new All<>();

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
        public String toString() {
            return "all";
        }

        @Override
        public StringBuilder append(StringBuilder sb) {
            return sb.append(toString());
        }
    }

    class Value<I> implements SymSet<I> {

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
                    throw typeError(rhs.type());
            }
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public StringBuilder append(StringBuilder sb) {
            // TODO: handle whitespace.
            return sb.append(toString());
        }
    }

    class Pred<I> implements SymSet<I> {

        public final String name;
        public final Functions.Predicate<I> pred;

        public Pred(String name, Functions.Predicate<I> pred) {
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
                    throw typeError(rhs.type());
            }
        }

        @Override
        public String toString() {
            return append(new StringBuilder()).toString();
        }

        @Override
        public StringBuilder append(StringBuilder sb) {
            return sb.append('<').append(name).append('>');
        }
    }

    class Union<I> implements SymSet<I> {

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
                    throw typeError(rhs.type());
            }
        }

        @Override
        public String toString() {
            return append(new StringBuilder()).toString();
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

    Type type();

    boolean matches(I value);

    SymSet<I> union(SymSet<I> rhs);

    StringBuilder append(StringBuilder sb);
}

abstract class SymSetUtils {
    static RuntimeException typeError(SymSet.Type type) {
        return new IllegalArgumentException("SymSet rhs has unrecognised type - " + type);
    }
}