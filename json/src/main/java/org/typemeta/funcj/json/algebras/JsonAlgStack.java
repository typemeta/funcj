package org.typemeta.funcj.json.algebras;

import org.typemeta.funcj.json.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.typemeta.funcj.json.algebras.JsonAlgStackUtils.popN;
import static org.typemeta.funcj.json.algebras.JsonAlgStackUtils.zip;

/**
 * Iterative, stack-based implementation of applying an object algebra.
 * <p>
 * To avoid {@code StackOverflowException}s, we use a stack to traverse the JSON tree structure.
 */
public abstract class JsonAlgStack {
    private static final class Entry {
        // The JSON value.
        final JsValue value;

        // Indicates if this value is ready to be evaluated.
        boolean ready;

        private Entry(JsValue value) {
            this.value = value;
        }
    }

    /**
     * Process a {Link JsValue} by applying a {@code JsonAlg} to it.
     * @param value     the value to be processed
     * @param alg       the object algebra to be applied
     * @param <T>       the result type
     * @return          the result of applying the object algebra
     */
    public static <T> T apply(JsValue value, JsonAlg<T> alg) {
        final Deque<Entry> pendingStack = new ArrayDeque<>();
        final Deque<T> resultsStack = new ArrayDeque<>();

        pendingStack.push(new Entry(value));

        // The basic approach is:
        // Pull the next value off the pendingStack.
        // 1) For a value with no JsValue children,
        //    process the value and push the result onto the resultsStack.
        // 2) For a value with children, if the value is not ready to be evaluated,
        //    then set ready to true, and push it and its children onto the stack.
        // 3) For a value with children, if the value is ready to be evaluated,
        //    pull the child results off the resultsStack,
        //    and then process the value and its children,
        //    and push the result onto the resultsStack.
        while (!pendingStack.isEmpty()) {
            final Entry next = pendingStack.pop();
            final Class<? extends JsValue> cls = next.value.getClass();

            if (cls.equals(JsArray.class)) {
                final JsArray jsArr = (JsArray)next.value;
                if (next.ready) {
                    final List<T> results = popN(resultsStack, jsArr.size());
                    resultsStack.push(alg.arr(results));
                } else {
                    next.ready = true;
                    pendingStack.push(next);
                    jsArr.stream().map(Entry::new).forEach(pendingStack::push);
                }
            } else if (cls.equals(JsBool.class)) {
                final JsBool jsBl = (JsBool)next.value;
                resultsStack.push(alg.bool(jsBl.value()));
            } else if (cls.equals(JsNull.class)) {
                resultsStack.push(alg.nul());
            } else if (cls.equals(JsNumber.class)) {
                final JsNumber jsNum = (JsNumber)next.value;
                resultsStack.push(alg.num(jsNum.value()));
            } else if (cls.equals(JsObject.class)) {
                final JsObject jsObj = (JsObject)next.value;
                if (next.ready) {
                    final List<String> names =
                            jsObj.stream()
                                    .map(JsObject.Field::name)
                                    .collect(Collectors.toList());
                    final List<T> values = popN(resultsStack, jsObj.size());
                    final LinkedHashMap<String, T> map = zip(names, values);
                    resultsStack.push(alg.obj(map));
                } else {
                    next.ready = true;
                    pendingStack.push(next);
                    jsObj.stream()
                            .map(JsObject.Field::value)
                            .map(Entry::new)
                            .forEach(pendingStack::push);
                }
            } else if (cls.equals(JsString.class)) {
                final JsString jsStr = (JsString)next.value;
                resultsStack.push(alg.str(jsStr.value()));
            } else {
                throw new IllegalStateException("Unrecognised JsValue sub-type : " + cls);
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
