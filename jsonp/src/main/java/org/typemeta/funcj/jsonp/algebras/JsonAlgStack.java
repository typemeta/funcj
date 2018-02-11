package org.typemeta.funcj.jsonp.algebras;

import javax.json.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.typemeta.funcj.jsonp.algebras.JsonAlgStackUtils.*;

/**
 * Iterative, stack-based implementation of applying an object algebra.
 * <p>
 * To avoid {@code StackOverflowException}s, we use a stack to traverse the JSON tree structure.
 */
public abstract class JsonAlgStack {
    private static final class Entry {
        // The JSON value.
        final JsonValue value;

        // Indicates if this value is ready to be evaluated.
        boolean ready;

        private Entry(JsonValue value) {
            this.value = value;
        }
    }

    /**
     * Process a {Link JsonValue} by applying a {@code JsonAlg} to it.
     * @param value     the value to be processed
     * @param alg       the object algebra to be applied
     * @param <T>       the result type
     * @return          the result of applying the object algebra
     */
    public static <T> T apply(JsonValue value, JsonAlg<T> alg) {
        final Deque<Entry> pendingStack = new ArrayDeque<>();
        final Deque<T> resultsStack = new ArrayDeque<>();

        pendingStack.push(new Entry(value));

        // The basic approach is:
        // Pull the next value off the pendingStack.
        // 1) For a value with no JsonValue children,
        //    process the value and push the result onto the resultsStack.
        // 2) For a value with children, if the value is not ready to be evaluated,
        //    then set ready to true, and push it and its children onto the stack.
        // 3) For a value with children, if the value is ready to be evaluated,
        //    pull the child results off the resultsStack,
        //    and then process the value and its children,
        //    and push the result onto the resultsStack.
        while (!pendingStack.isEmpty()) {
            final Entry next = pendingStack.pop();
            final JsonValue.ValueType jsType = next.value.getValueType();

            switch (jsType) {

                case ARRAY:
                    final JsonArray jsArr = (JsonArray)next.value;
                    if (next.ready) {
                        final List<T> results = popN(resultsStack, jsArr.size());
                        resultsStack.push(alg.arr(results));
                    } else {
                        next.ready = true;
                        pendingStack.push(next);
                        jsArr.stream().map(Entry::new).forEach(pendingStack::push);
                    }
                    break;
                case OBJECT:
                    final JsonObject jsObj = (JsonObject)next.value;
                    final List<String> names = new ArrayList<>(jsObj.keySet());
                    Collections.sort(names);
                    if (next.ready) {
                        final List<T> values = popN(resultsStack, jsObj.size());
                        final LinkedHashMap<String, T> map = zip(names, values);
                        resultsStack.push(alg.obj(map));
                    } else {
                        next.ready = true;
                        pendingStack.push(next);
                        names.forEach(name -> pendingStack.push(new Entry(jsObj.get(name))));
                    }
                    break;
                case STRING:
                    final JsonString jsStr = (JsonString)next.value;
                    resultsStack.push(alg.str(jsStr.getString()));
                    break;
                case NUMBER:
                    final JsonNumber jsNum = (JsonNumber)next.value;
                    T t;
                    final Number number = jsNum.numberValue();
                    if (number instanceof Double || number instanceof Float || number instanceof Long) {
                        t = alg.num(number.doubleValue());
                    } else if (number instanceof BigDecimal || number instanceof BigInteger) {
                        t = alg.num(jsNum.bigDecimalValue());
                    } else {
                        t = alg.num(number.intValue());
                    }
                    resultsStack.push(t);
                    break;
                case TRUE:
                    resultsStack.push(alg.bool(true));
                    break;
                case FALSE:
                    resultsStack.push(alg.bool(false));
                    break;
                case NULL:
                    resultsStack.push(alg.nul());
                    break;
            }
        }

        if (resultsStack.size() != 1) {
            throw new IllegalStateException(
                    "resultsStack expected to have exactly 1 entry, but has " + resultsStack.size());
        }

        return resultsStack.pop();
    }
}

abstract class JsonAlgStackUtils {
    static <T> List<T> popN(Deque<T> stack, int n) {
        final List<T> l = new ArrayList<>(n);

        for (int i = 0; i < n; ++i) {
            l.add(stack.pop());
        }

        return l;
    }

    static <K, V> LinkedHashMap<K, V> zip(List<K> names, List<V> values) {
        final LinkedHashMap<K, V> m = new LinkedHashMap<K, V>();

        for (int i = 0; i < names.size(); ++i) {
            m.put(names.get(i), values.get(i));
        }

        return m;
    }
}
